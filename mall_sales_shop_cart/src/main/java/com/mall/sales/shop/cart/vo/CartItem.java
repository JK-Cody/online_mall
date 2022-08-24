package com.mall.sales.shop.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车的每样商品基本信息
 */
@ToString
public class CartItem {

    private Long skuId;
    private Boolean check =true;  //商品在购物车时默认被选中
    private String title;
    private String image;
    private List<String> skuAttrList;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttrList() {
        return skuAttrList;
    }

    public void setSkuAttrList(List<String> skuAttr) {
        this.skuAttrList = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    //计算当前购物项总价
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
