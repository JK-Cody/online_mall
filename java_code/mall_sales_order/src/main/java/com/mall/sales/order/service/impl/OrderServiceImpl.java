package com.mall.sales.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.common.constant.*;
import com.mall.common.exception.StockException;
import com.mall.common.to.OrderTO;
import com.mall.common.to.SeckillSkuCreateOrderTO;
import com.mall.common.utils.R;
import com.mall.common.vo.MemberRespVo;
import com.mall.common.vo.SkuHasStock;
import com.mall.sales.order.config.AliPaymentTemplate;
import com.mall.sales.order.config.WechatPaymentTemplate;
import com.mall.sales.order.entity.OrderItemEntity;
import com.mall.sales.order.entity.PaymentInfoEntity;
import com.mall.sales.order.feign.ProductFeignService;
import com.mall.sales.order.service.OrderItemService;
import com.mall.sales.order.service.PaymentInfoService;
import com.mall.sales.order.to.OrderInfoAndDeliveryInfoTO;
import com.mall.sales.order.vo.*;
import com.mall.sales.order.feign.HousewareFeignService;
import com.mall.sales.order.feign.MemberFeignService;
import com.mall.sales.order.feign.ShopCartFeignService;
import com.mall.sales.order.interceptor.LoginUserInterceptor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.order.dao.OrderDao;
import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static java.util.stream.Collectors.toList;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private ShopCartFeignService shopCartFeignService;

    @Autowired
    private HousewareFeignService housewareFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    WechatPaymentTemplate wechatPaymentTemplate;

    @Autowired
    AliPaymentTemplate aliPaymentTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存订单和购物车内容
     */
    @Override
    public OrderConfirmVO saveCreatedOrder() throws ExecutionException, InterruptedException {
//获取用户信息
        MemberRespVo MemberRespVo = LoginUserInterceptor.threadLocal.get();
//异步保存订单
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        /* 因为异步线程需要新的线程，没有附带原线程的请求头数据，需要保存原请求数据 */
        //从请求容器中获取原先线程的上下文
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //原先线程的上下文保存到当前异步线程
            RequestContextHolder.setRequestAttributes(attributes);
            //保存会员地址
            try {
                List<MemberAddressVO> memberAddressVOList = memberFeignService.getMemberAddressList(MemberRespVo.getId());
                orderConfirmVO.setMemberAddressVos(memberAddressVOList);
            } catch (Exception e) {
                log.warn("\n远程调用会员服务失败");
            }
        }, executor);
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //原先线程的上下文保存到当前异步线程
            RequestContextHolder.setRequestAttributes(attributes);
            //保存购物车内容
            List<CartItemVO> items = shopCartFeignService.getCartItemList();
            orderConfirmVO.setItems(items);
        }, executor).thenRunAsync(() -> {
            // 保存购物车商品库存
            List<CartItemVO> items = orderConfirmVO.getItems();
            List<Long> skus = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R hasStock = housewareFeignService.getSkusHasStock(skus);
            List<SkuHasStock> skuStockVOList = hasStock.getData("skusHasStockList", new TypeReference<List<SkuHasStock>>() {  });
            //判断键值对保存是否有库存
            if(skuStockVOList !=null && skuStockVOList.size() > 0) {
                Map<Long, Boolean> stocks = skuStockVOList.stream().collect(Collectors.toMap(SkuHasStock::getSkuId, SkuHasStock::getHasStock));
                orderConfirmVO.setHasStocks(stocks);
            }
        }, executor);
        //保存用户积分
        Integer integration = MemberRespVo.getIntegration();
        orderConfirmVO.setIntegration(integration);
        //保存订单的令牌(防重复提交)
        String orderToken = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVO.setOrderToken(orderToken);
        stringRedisTemplate.opsForValue().set(OrderConstant.ORDER_COMFIRM_ORDERTOKEN_PREFIX + MemberRespVo.getId(), orderToken, 10, TimeUnit.MINUTES);
//等待所有异步任务完成
        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return orderConfirmVO;
    }

    /**
     * 创建订单
     */
//  @GlobalTransactional
    @Transactional
    @Override
    public ResponseOfCreatingOrderVO createOrderWithSubmittedData(OrderMarkInfoOfSubmittedDataVO orderMarkInfoOfSubmittedDataVO) {

        ResponseOfCreatingOrderVO responseOfCreatingOrderVO = new ResponseOfCreatingOrderVO();
//获取用户信息
        MemberRespVo MemberRespVo = LoginUserInterceptor.threadLocal.get();
//删除用户的订单令牌
        String ORDERTOKEN = OrderConstant.ORDER_COMFIRM_ORDERTOKEN_PREFIX + MemberRespVo.getId();
        //缓存删除原子性指令,1表示成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderMarkInfoOfSubmittedDataVO.getOrderToken();
        Long exist = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(ORDERTOKEN), orderToken);
        //令牌不存在时
        if(null!=exist && exist == 0L){
            responseOfCreatingOrderVO.setCode(OrderConstant.AttrEnum.CHECK_ORDER_TOKEN_FAILURE.getCode());
            return responseOfCreatingOrderVO;
        }else {
//保存订单
            //保存前端提交的订单数据
            OrderInfoAndDeliveryInfoTO orderInfoAndDeliveryInfoTO = createOrderWithSubmitedData(orderMarkInfoOfSubmittedDataVO);
            //验证订单支付总价
            BigDecimal payAmount = orderInfoAndDeliveryInfoTO.getOrder().getPayAmount();
            BigDecimal payPrice = orderMarkInfoOfSubmittedDataVO.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //保存订单到数据库
                OrderSave(orderInfoAndDeliveryInfoTO);
//保存购物车内容
                String orderSn = orderInfoAndDeliveryInfoTO.getOrder().getOrderSn();
                OrderEntity order = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
                if(null!=order) {
                    for (OrderItemEntity orderItemEntity : orderInfoAndDeliveryInfoTO.getOrderItemList()) {
                        //补充订单号
                        orderItemEntity.setOrderId(order.getId());
                    }
                    orderItemService.saveBatch(orderInfoAndDeliveryInfoTO.getOrderItemList());
                }
//锁定购物车sku的库存
                SkuStockLockVO skuStockLockVO = new SkuStockLockVO();
                skuStockLockVO.setOrderSn(orderSn);
                List<CartItemVO> collect = orderInfoAndDeliveryInfoTO.getOrderItemList().stream().map(item -> {
                    CartItemVO cartItemVO = new CartItemVO();
                    cartItemVO.setSkuId(item.getSkuId());
                    cartItemVO.setCount(item.getSkuQuantity());
                    cartItemVO.setTitle(item.getSkuName());
                    return cartItemVO;
                }).collect(Collectors.toList());
                skuStockLockVO.setCartItemVOList(collect);
                //锁定sku库存
                R skuStockLock = housewareFeignService.getSkuStockLock(skuStockLockVO);
//发送订单创建消息
                //锁定成功时
                int SUBMIT_ORDER_SUCCEED = OrderConstant.AttrEnum.SUBMIT_ORDER_SUCCEED.getCode();
                if(skuStockLock.getCode()==SUBMIT_ORDER_SUCCEED){
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderInfoAndDeliveryInfoTO.getOrder());
//删除购物车里的数据
                    stringRedisTemplate.delete(ShopCartConstant.SHOP_CART_REDIS_PREFIX+MemberRespVo.getId());
                    responseOfCreatingOrderVO.setOrder(orderInfoAndDeliveryInfoTO.getOrder());
                    responseOfCreatingOrderVO.setCode(SUBMIT_ORDER_SUCCEED);
                    return responseOfCreatingOrderVO;
                //锁定失败时
                }else{
                    String msg = (String) skuStockLock.get("msg");
                    throw new StockException(msg);
                }
            //失败时
            } else {
                //设置支付总价错误
                responseOfCreatingOrderVO.setCode(OrderConstant.AttrEnum.CHECK_ORDER_PAYAMOUNT_FAILURE.getCode());
                return responseOfCreatingOrderVO;
            }
        }
    }

    /**
     * 根据订单号查询订单
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {

        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
    }

    /**
     * 关闭订单
     * 受监视器监控
     */
    @Override
    public void confirmOrderClose(OrderEntity order) {
//更新订单状态
        OrderEntity entity = this.getById(order.getId());
        //确认订单状态
        if(entity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
//当状态未支付时，再查询支付工具的订单支付状态
            String trade_state=null;
            Boolean equals=null;
            //向支付宝平台查询订单是未支付状态
            if(PaymentTypeConstant.aliPayment.equals(order.getPayType())){
                trade_state = aliPaymentTemplate.queryPaymentResult(order.getOrderSn());
                equals = Objects.equals(trade_state, NativeOfAliPaymentConstant.tradeState.WAIT_BUYER_PAY.getType());
            //向微信平台查询订单是未支付状态
            }else if(PaymentTypeConstant.wechatPayment.equals(order.getPayType())){
                trade_state = wechatPaymentTemplate.queryPaymentResult(order.getOrderSn());
                equals = Objects.equals(trade_state, NativeOfWechatPaymentConstant.tradeState.NOTPAY.getType());
            }else{
                throw new RuntimeException("不存在该订单的支付类型");
            }
          if(Boolean.TRUE.equals(equals)){
                //向支付宝平台查询订单是否已支付
                //设置状态
                OrderEntity update = new OrderEntity();
                update.setId(entity.getId());
                update.setStatus(OrderStatusEnum.CANCLED.getCode());
                this.updateById(update);
                OrderTO orderTO = new OrderTO();
                BeanUtils.copyProperties(entity,orderTO);
//发送订单关闭消息
                try {
                    //订单关闭路由键
                    rabbitTemplate.convertAndSend("order-event-exchange","order.release.closeInfo",orderTO);
                } catch (AmqpException e) {
                    e.printStackTrace();
                }
          }
        }
    }

    /**
     * 获取用户的所有订单和购物车商品内容,按条件分页
     */
    @Override
    public PageUtils returnOrderPage(Map<String, Object> params) {
//获取用户信息
        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
//获取所有订单
/* 方式一 */
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByAsc("id"));
//获取所有订单和购物车内容
        List<OrderEntity> orderEntityList = page.getRecords().stream().peek(order -> {
            //循环查询每个订单的购物车内容
            List<OrderItemEntity> itemEntityList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setOrderItemList(itemEntityList);
        }).collect(toList());
        page.setRecords(orderEntityList);
/* 方式二 */
//分页
//        List<OrderEntity> orderList = this.list(new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByAsc("id"));
//        List<Long> orderIdList=new ArrayList<>();
//        for (OrderEntity orderEntity : orderList) {
//            orderIdList.add(orderEntity.getId());
//        }
//        Map<Long, List<OrderItemEntity>> orderItemList = orderItemService.getListByOrderSnList(orderIdList);
//
//        List<OrderWithOrderItemListTO> orderWithOrderItemListTOList = orderList.stream().map(order -> {
//            OrderWithOrderItemListTO orderWithOrderItemListTO = new OrderWithOrderItemListTO();
//            //匹配订单的id
//            orderItemList.entrySet().stream().map(orderItem -> {
//                if (order.getId().equals(orderItem.getKey())) {
//                    orderWithOrderItemListTO.setOrder(order);
//                    orderWithOrderItemListTO.setOrderItemList(orderItem.getValue());
//                    //剔除已匹配
//                    orderItemList.remove(orderItem.getKey());
//                }
//                return null;
//            });
//            return orderWithOrderItemListTO;
//        }).collect(toList());
////保存为分页
//        IPage<OrderWithOrderItemListTO> page = new Query<OrderWithOrderItemListTO>().getPage(params);
//        page.setRecords(orderWithOrderItemListTOList);
        return new PageUtils(page);
    }

    /**
     * 处理支付宝返回的支付信息
     */
    @Override
    public String handleAliPaymentNotification(AliPaymentNotificationVO vo) {
//判断订单号、金额、平台录入的支付状态
        String out_trade_no = vo.getOut_trade_no();
        OrderEntity order = this.getOrderStatusByOrderSn(out_trade_no);
        if(null!=order) {
                BigDecimal total_amount = new BigDecimal(vo.getTotal_amount());
                if(order.getTotalAmount().equals(total_amount) && NativeOfAliPaymentConstant.tradeState.TRADE_SUCCESS.getType().equals(vo.getTrade_status())){
//保存支付信息到数据库
                //未付款时
                if (Objects.equals(order.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
                    PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
                    paymentInfoEntity.setPaymentTradeNo(vo.getTrade_no());
                    paymentInfoEntity.setOrderSn(out_trade_no);
                    String trade_status = vo.getTrade_status();
                    paymentInfoEntity.setPaymentStatus(trade_status);
                    //修改支付宝返回的日期格式与getNotify_time匹配的格式
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(vo.getNotify_time());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    paymentInfoEntity.setCallbackTime(date);
                    paymentInfoService.save(paymentInfoEntity);
//更新订单状态
                    if (trade_status.equals(NativeOfAliPaymentConstant.tradeState.TRADE_SUCCESS.getType()) || trade_status.equals(NativeOfAliPaymentConstant.tradeState.TRADE_FINISHED.getType())) {
                        this.updateOrderStatus(out_trade_no, OrderStatusEnum.PAYED.getCode(), PaymentTypeConstant.aliPayment);
                    }
                    return "success"; //返回给支付宝
                }
            }
            log.warn("金额、平台录入的支付状态不正确");
            return "error";
        }
        log.warn("数据库不存在支付宝返回的订单号");
        return "error";
    }

    /**
     * 处理微信支付返回的支付信息
     */
    @Override
    public void handleWechaPaymentNotification(HashMap map,String out_trade_no) {
//查询订单的状态
        OrderEntity orderEntity = this.getOrderStatusByOrderSn(out_trade_no);
        //未付款时
        if(Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            String transaction_id = (String) map.get("transaction_id");
            String trade_state = (String) map.get("trade_state");
            String success_time = (String) map.get("success_time");
//保存支付信息到数据库
            PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
            paymentInfoEntity.setPaymentTradeNo(transaction_id);
            paymentInfoEntity.setOrderSn(out_trade_no);
            paymentInfoEntity.setPaymentStatus(trade_state);
            //修改支付宝返回的日期格式与getNotify_time匹配的格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = simpleDateFormat.parse(success_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            paymentInfoEntity.setCallbackTime(date);
            paymentInfoService.save(paymentInfoEntity);
//更新订单状态
            if (trade_state.equals(NativeOfWechatPaymentConstant.tradeState.SUCCESS.getType())) {
                this.updateOrderStatus(out_trade_no, OrderStatusEnum.PAYED.getCode(), PaymentTypeConstant.wechatPayment);
            }
        }
    }

    /**
     * 查询订单
     */
    @Override
    public OrderEntity getOrderStatusByOrderSn(String orderSn) {

        return this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * 创建秒杀商品的订单
     */
    @Override
    public void saveSeckillSkuCreateOrder(SeckillSkuCreateOrderTO createOrder) {
//保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(createOrder.getOrderSn());
        orderEntity.setMemberId(createOrder.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal price = createOrder.getSeckillPrice();
        orderEntity.setPayAmount(price);
        this.save(orderEntity);
//保存购物车内容
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(createOrder.getOrderSn());
        //补充订单号
        OrderEntity order = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", createOrder.getOrderSn()));
        if(null!=order) {
            orderItemEntity.setOrderId(order.getId());
        }
        orderItemEntity.setRealAmount(price);
        orderItemEntity.setSkuQuantity(createOrder.getSkuCount());
        orderItemService.save(orderItemEntity);
    }


//++++++++++++++++++++++++++++
    /**
     * 将前端提交的订单数据创建为订单
     */
    private OrderInfoAndDeliveryInfoTO createOrderWithSubmitedData(OrderMarkInfoOfSubmittedDataVO orderMarkInfoOfSubmittedDataVO){

        OrderInfoAndDeliveryInfoTO orderInfoAndDeliveryInfoTO = new OrderInfoAndDeliveryInfoTO();
        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //保存订单的内容
        OrderEntity orderEntity = this.saveOrderInfo(orderMarkInfoOfSubmittedDataVO,orderSn);
        //保存订单的购物车内容
        List<OrderItemEntity> orderItemEntities = this.saveItemListInfo(orderSn);
        //保存订单支付总价
        this.computeOrderPayPrice(orderEntity,orderItemEntities);
        orderInfoAndDeliveryInfoTO.setOrder(orderEntity);
        orderInfoAndDeliveryInfoTO.setOrderItemList(orderItemEntities);
        return orderInfoAndDeliveryInfoTO;
    }

    /**
     * 将前端提交的订单数据创建为订单——订单的内容
     */
    private OrderEntity saveOrderInfo(OrderMarkInfoOfSubmittedDataVO orderMarkInfoOfSubmittedDataVO, String orderSn){
//获取用户登录信息
        MemberRespVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
//保存订单的内容
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberResponseVo.getUsername());
        //保存订单运费和会员地址
        R orderFare = housewareFeignService.getOrderFare(orderMarkInfoOfSubmittedDataVO.getAddrId());
        OrderFareVo orderFareVo = orderFare.getData("orderFare",new TypeReference<OrderFareVo>() {
        });
        orderEntity.setFreightAmount(orderFareVo.getOrderFare());
        orderEntity.setReceiverCity(orderFareVo.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(orderFareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(orderFareVo.getAddress().getName());
        orderEntity.setReceiverPhone(orderFareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(orderFareVo.getAddress( ).getPostCode());
        orderEntity.setReceiverProvince(orderFareVo.getAddress().getProvince());
        orderEntity.setReceiverRegion(orderFareVo.getAddress().getRegion());
        //设置订单的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }

    /**
     * 将前端提交的订单数据创建为订单——订单的商品内容
     */
    private List<OrderItemEntity> saveItemListInfo(String orderSn){
        //获取用户的所有商品内容
        List<CartItemVO> cartItemList = shopCartFeignService.getCartItemList();
        //保存订单的商品内容
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        if(cartItemList!= null &&cartItemList.size()>0){
            orderItemEntityList = cartItemList.stream().map(cartItem -> {
                //保存单个sku的内容
                OrderItemEntity orderItemEntity = OrderItemSeparatedCreate(cartItem) ;
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }). collect(Collectors.toList());
        }
        return orderItemEntityList;
    }

    /**
     * 将前端提交的订单数据创建为订单——单个sku的内容
     */
    private OrderItemEntity OrderItemSeparatedCreate(CartItemVO cartItemVO){
//获取商品对应的spu的信息
        Long skuId = cartItemVO.getSkuId();
        R spuInfo = productFeignService.getSpuInfo(skuId);
        SpuInfoVO spuInfoData = spuInfo.getData(new TypeReference<SpuInfoVO>() {
        });
//保存spu的信息
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandId().toString()); //以品牌id替代品牌名
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());
//保存sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(cartItemVO.getTitle());
        orderItemEntity.setSkuPic(cartItemVO.getImage());
        orderItemEntity.setSkuPrice(cartItemVO.getPrice());
        orderItemEntity.setSkuQuantity(cartItemVO.getCount());
        //保存sku属性列表
        /* 集合转换指定分隔符的字符串 */
        String skuAttrValues = StringUtils.collectionToDelimitedString(cartItemVO.getSkuAttrList(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);
//保存会员积分信息
        orderItemEntity.setGiftGrowth(cartItemVO.getPrice().multiply(new BigDecimal(cartItemVO.getCount())).intValue());
        orderItemEntity.setGiftIntegration(cartItemVO.getPrice().multiply(new BigDecimal(cartItemVO.getCount())).intValue());
//保存价格信息
        //分解金额
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //无优惠总价减去优惠价
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        //订单支付总价
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }

    /**
     * 将前端提交的订单数据创建为订单——保存订单价格
     * 单个订单项信息的价格已计算,OrderItemSeparatedCreate()
     */
    private void computeOrderPayPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
//保存订单价格
        BigDecimal totalPrice = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        Integer integrationTotal = 0;
        Integer growthTotal = 0;
        //循环获取单个订单项的支付总价
        for (OrderItemEntity orderItem : orderItemEntities) {
            //保存优惠价
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());
            //保存无优惠总价
            totalPrice = totalPrice.add(orderItem.getRealAmount());
            //保存积分值
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();
        }
        orderEntity.setTotalAmount(totalPrice);
        //保存运费
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);
//设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 保存订单到数据库
     */
    private void OrderSave(OrderInfoAndDeliveryInfoTO orderInfoAndDeliveryInfoTO){

        OrderEntity order = orderInfoAndDeliveryInfoTO.getOrder();
        order.setModifyTime(new Date());
        this.save(order);
    }

    /**
     * 更新订单的状态
     */
    private void updateOrderStatus(String outTradeNo, Integer statusCode,Integer payType) {

        this.baseMapper.updateOrderStatus(outTradeNo,statusCode,payType);
    }
}