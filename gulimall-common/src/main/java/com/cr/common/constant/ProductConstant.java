package com.cr.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cr
 * @date 2021/2/8 21:30
 */
public class ProductConstant {

    @Getter
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

        private final int code;

        private final String msg;

    }

    @Getter
    @AllArgsConstructor
    public enum StatusEnum {

        /**
         * 新建
         */
        NEW_SPU(0, "新建"),

        /**
         * 上架
         */
        SPU_UP(1, "上架"),

        /**
         * 下架
         */
        SPU_DOWN(2, "下架");

        private final int code;

        private final String msg;

    }

}
