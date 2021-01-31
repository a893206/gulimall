package com.cr.gulimall.product.exception;

import com.cr.common.exception.BizCodeEnum;
import com.cr.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cr
 * @date 2021/1/31 20:10
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.cr.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现异常{}，异常类型：{}", e.getMessage(),e.getClass());

        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>(2);
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return R.error(BizCodeEnum.VALID_EXCEPTION).put("data", errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        return R.error();
    }

}
