package com.mall.sales.product.exception;

import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.mall.sales.product.controller")
public class ValueValidityGlobalException {

    /**
     * 处理数据效验
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R valueValidityException(MethodArgumentNotValidException e){

        log.error("数据校验出现问题{},异常类型{}",e.getMessage(),e.getClass());
        Map<String,String> map=new HashMap<>();
        //获取异常的属性值
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            String message = fieldError.getDefaultMessage();  //信息
            String field = fieldError.getField();  //字段
            map.put(field,message);
        });
        return R.error(BusinessCodeExceptionEnum.VALID_EXCEPTION.getCode(),BusinessCodeExceptionEnum.VALID_EXCEPTION.getMsg()).put("data",map);
    }

    /**
     * 其他的异常处理
     * @param throwable
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("未知异常{},异常类型{}",
                throwable.getMessage(),
                throwable.getClass());
        return R.error(BusinessCodeExceptionEnum.UNKNOW_EXEPTION.getCode(),
                BusinessCodeExceptionEnum.UNKNOW_EXEPTION.getMsg());
    }
}
