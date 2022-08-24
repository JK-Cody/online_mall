package com.mall.common.group;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义校验器,ValidatedGroup的Integer型
 */
public class ValidatedConstraintValidator implements ConstraintValidator<ValidatedGroup,Integer> {

    private Set<Integer> set=new HashSet<>();

    @Override
    public void initialize(ValidatedGroup constraintAnnotation) {

        int [] value= constraintAnnotation.value();
        for(int val:value){
            set.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return  set.contains(value);
    }
}
