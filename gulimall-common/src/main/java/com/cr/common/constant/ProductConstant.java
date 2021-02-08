package com.cr.common.constant;

import lombok.AllArgsConstructor;

/**
 * @author cr
 * @date 2021/2/8 21:30
 */
public class ProductConstant {

    @AllArgsConstructor
    public enum AttrEnum {

        /**
         * 基本属性
         */
        ATTR_TYPE_BASE(1, "基本属性"),

        /**
         * 销售属性
         */
        ATTR_TYPE_SALE(0, "销售属性");

        public int code;

        public String msg;

    }

}
