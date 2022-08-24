package com.mall.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 库存查询的异常类型
 */
public class StockException extends RuntimeException{

    @Getter @Setter
    private Long skuId;

    public StockException(){};

    public StockException(String msg) {
        super(msg);
    }

    /* 无库存时  */
    public StockException(Long skuId){
        super("skuId："+skuId+";stock:Not");
    }

}
