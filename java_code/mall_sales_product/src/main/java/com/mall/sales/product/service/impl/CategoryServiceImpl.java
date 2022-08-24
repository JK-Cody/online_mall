package com.mall.sales.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.sales.product.service.CategoryBrandRelationService;
import com.mall.sales.product.vo.Catalogs2VO;
import com.mall.sales.product.vo.CategoryVO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.CategoryDao;
import com.mall.sales.product.entity.CategoryEntity;
import com.mall.sales.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.cache.annotation.CachePut;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 返回商品列表带分页
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 获取商品目录列表
     * @return categoryEntityList
     */
    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> entities = baseMapper.selectList(null);
//        遍历获取第一级目录项目
        List<CategoryEntity> categoryEntityList = entities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                //获取下级目录项目
                .peek((categoryEntity) -> categoryEntity.setChildren(getChildren(categoryEntity, entities)))
                //排序 第一级目录+下级目录
                .sorted((cat1, cat2) -> {
                    //避免空值
                    return (cat1.getSort() == null?0:cat1.getSort()) - (cat2.getSort() == null?0:cat2.getSort());
                }).collect(Collectors.toList());
        return categoryEntityList;
    }

    /**
     * 删除最下级商品列表
     * @param asList
     */
    @Override
    public void removeListById(List<Long> asList) {

        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 获取属性分组的所属分类
     */
    @Override
    public Long[] getCategoryPath(Long catalogId) {

        List<Long> paths = new ArrayList<>();
        paths = findParentPath(catalogId, paths);
        // 将父与子catlogId倒序
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 修改pms_category表和关联表
     */
    // 缓存失效模式
//    /* 方式一 ：一次性清空所有value = "categoryCashe"的缓存 */
//    @CacheEvict(value = "categoryCashe",allEntries = true)
//    /* 方式二 ：清空多个指定缓存名的缓存 */
//      @Caching(evict = {
//              @CacheEvict(value = "categoryCashe ", key = "'getLevelOneCategorys'"),
//              @CacheEvict(value = "categoryCashe", key = "'XXXX'")
//      })
//    /* 方式三 ：清空单个指定缓存名的缓存 */
//    @CacheEvict(value = "categoryCashe",key = "'getLevelOneCategorys'")

//     缓存双写模式(有修改且必须有return值)
//    @CachePut
    @Override
    @Transactional
    public void updateTable(CategoryEntity categoryEntity) {

        this.updateById(categoryEntity); //自更新
        //更新pms_category_brand_relation表
        categoryBrandRelationService.updateByCatalog( categoryEntity.getCatId(), categoryEntity.getName());
    }

    /**
     * 递归收集父与子的catalogId
     */
    private List<Long> findParentPath(Long catalogId, List<Long> paths) {
        //获取自身catlogId
        paths.add(catalogId);
        CategoryEntity byId = this.getById(catalogId);
        //递归获取父catlogId
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 获取所有一级商品目录
     * 添加缓存到redis
     */
    @Cacheable(value = {"categoryCashe"}, key = "#root.method.name", sync = true) //自定义缓存名，缓存键值,使用本地锁
    @Override
    public List<CategoryEntity> getLevelOneCategorys() {
       return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }

    /**
     * 获取各级目录的联合列表
     * 添加redis缓存
     */
    @Override
    public Map<String, List<CategoryVO>> getCatalogAllLevel() {
//获取已有的缓存
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //转为实体类对象
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<CategoryVO>>>() { });
        }
//获取所有商品目录列表
//TODO 要求二级和三级目录需符合 catalogLoader.js 的书写格式
        /*方式一*/
//获取各级的商品目录列表（部分字段）
        List<CategoryVO> categoryVOList= baseMapper.selectPartInfo();
        //过滤3级目录
        List<CategoryVO> levelThreeCategories = categoryVOList.stream().filter(item -> {
            return item.getCatLevel() == 3;
        }).collect(Collectors.toList());
        //过滤2级目录
        List<CategoryVO> levelTwoCategories = categoryVOList.stream().filter(item -> {
            return item.getCatLevel() == 2;
        }).map(e -> {
            //保存3级目录
            List<CategoryVO> matchList = levelThreeCategories.stream().filter(c -> {
                return c.getParentCid().equals(e.getCatId());
            }).collect(Collectors.toList());
            e.setCatalog3List(matchList);
            //已保存的部分清除
            levelThreeCategories.removeAll(matchList);
            return e;
        }).collect(Collectors.toList());
        //过滤1级目录
        List<CategoryVO> levelOneCategories = categoryVOList.stream().filter(item -> {
            return item.getCatLevel() == 1;
        }).collect(Collectors.toList());
        //保存1级目录的id
        Map<String, List<CategoryVO>> levelTwoAndThreeCategories = levelOneCategories.stream().collect(Collectors.toMap(
            k -> k.getCatId().toString(), v -> {
                List<CategoryVO> matchList = levelTwoCategories.stream().filter(c -> {
                    return c.getParentCid().equals(v.getCatId());
                }).collect(Collectors.toList());
                List<CategoryVO> list = new ArrayList<>(matchList);
                levelTwoCategories.removeAll(matchList);
                return list;
        }));

        /*方式二*/
////获取所有商品目录的数据
//        List<CategoryEntity> allCategoryEntities = baseMapper.selectList(null);
////获取每一级目录的数据
//         //获取一级目录列表
//        List<CategoryEntity> levelOneCategories = getParentCategories(allCategoryEntities, 0L);
//        Map<String, List<Catalogs2VO>> levelTwoAndThreeCategories = levelOneCategories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            List<CategoryEntity> categoryOneEntities = getParentCategories(levelOneCategories, v.getCatId());
//            //获取二级目录列表
//            List<Catalogs2VO> catalogTwoList = null;
//            if (categoryOneEntities != null) {
//                catalogTwoList = categoryOneEntities.stream().map(itemTwo -> {
//                    Catalogs2VO catalogTwo = new Catalogs2VO(itemTwo.getCatId().toString(), itemTwo.getName(), v.getCatId().toString(),null );
//                    //获取三级目录列表
//                    List<CategoryEntity> catalogoThreeList = getParentCategories(levelOneCategories, itemTwo.getCatId());
//                    if (catalogoThreeList != null) {
//                        List<Catalogs2VO.Category3Vo> collect = catalogoThreeList.stream().map(itemThree -> {
//                            Catalogs2VO.Category3Vo catalogThree = new Catalogs2VO.Category3Vo(itemThree.getCatId().toString(), itemThree.getName(),itemTwo.getCatId().toString());
//                            return catalogThree;
//                        }).collect(Collectors.toList());
//                        //保存三级商品目录
//                        catalogTwo.setCatalog3List(collect);
//                    }
//                    return catalogTwo;
//                }).collect(Collectors.toList());
//            }
//            return catalogTwoList;
//        }));
//将对象转为json放在缓存中
        String s = JSON.toJSONString(levelTwoAndThreeCategories);
        stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return levelTwoAndThreeCategories;
    }


//++++++++++++++++++++++++++++++
    /**
     * 获取下级目录项目
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> leaves){

        //获取第二级目录项目
        List<CategoryEntity> children = leaves.stream().filter(categoryEntity -> {
            //当下级目录的id==自身id时，停止递归
            return categoryEntity.getParentCid().equals(root.getCatId());
           //递归来获取所有子项目
        }).peek(ChildCategoryEntity -> ChildCategoryEntity.setChildren(getChildren(ChildCategoryEntity, leaves)))
                .sorted((cat1, cat2) -> {
            return (cat1.getSort() == null?0:cat1.getSort()) - (cat2.getSort() == null?0:cat2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 从商品目录列表中截取父级的列表
     */
    private List<CategoryEntity> getParentCategories(List<CategoryEntity> selectList, Long parent_cid) {

        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        return collect;
    }

//    /**
//     * getCatalogAllLevel方法的本地锁
//     */
//    public Map<String, List<Catalogs2Vo>> getMethodWithLocalLock() {
//
//        synchronized (this) {
//            //TODO 得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
//            return getCatalogAllLevel();
//        }
//    }

//    /**
//     * getCatalogAllLevel方法的分布式锁
//     * 解决方法中断时而锁没有解除
//     * 解决满执行时锁解除时间快于方法结束
//     * 解决锁获取途中已经超时，误删它线程锁
//     */
//    public Map<String, List<Catalogs2Vo>> getMethodWithRedisLock() {
//      创建分布式锁
//        //设定锁Id
//        String uuid = UUID.randomUUID().toString();
//        //设定过期时间
//        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
//  给方法上锁
//        if (lock) {
//            System.out.println("获取分布式锁成功...");
//            //重设过期时间
//            redisTemplate.expire("lock",30,TimeUnit.SECONDS);
//            Map<String, List<Catalogs2Vo>> dataFromDb;
//            try {
//                dataFromDb = getCatalogAllLevel();
//            } finally {
//                //设定lua脚本锁,保证缓存 删除和查看时降低脏数据几率
//                //判断value是不是相等，若相等就删除，否则就直接return
//                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                //删除锁
//                Long redisLock = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList("lock"), uuid);
//            }
//             String lockValue = redisTemplate.opsForValue().get("lock");
//             if(uuid.equals(lockValue)){
//                  //删除我自己的锁
//                redisTemplate.delete("lock");//删除锁
//            }
//            return dataFromDb;
//        } else {
//            System.out.println("获取分布式锁失败...等待重试");
//            try {
//               Thread.sleep(200);
//            } catch (Exception e) {
//                System.out.println("获取分布式锁出现异常");
//            }
//            //自旋的方式来返回方法
//            return getMethodWithRedisLock();
//        }
//    }

}