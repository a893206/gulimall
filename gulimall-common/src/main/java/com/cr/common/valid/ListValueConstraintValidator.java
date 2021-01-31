package com.cr.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;

/**
 * @author cr
 * @date 2021/1/31 21:13
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private final HashSet<Integer> hashSet = new HashSet<>();

    /**
     * 初始化方法
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            hashSet.add(val);
        }
    }

    /**
     * 判断是否创建成功
     * @param integer 需要校验的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return hashSet.contains(integer);
    }
}
