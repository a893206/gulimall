package com.cr.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cr
 * @date 2021/6/28 00:46
 */
public class WareConstant {

    @Getter
    @AllArgsConstructor
    public enum PurchaseStatusEnum {

        /**
         * 新建
         */
        CREATED(0, "新建"),

        /**
         * 已分配
         */
        ASSIGNED(1, "已分配"),

        /**
         * 已领取
         */
        RECEIVED(2, "已领取"),

        /**
         * 已完成
         */
        FINISHED(3, "已完成"),

        /**
         * 有异常
         */
        HAS_ERROR(4, "有异常");

        private final int code;

        private final String msg;

    }

    @Getter
    @AllArgsConstructor
    public enum PurchaseDetailStatusEnum {

        /**
         * 新建
         */
        CREATED(0, "新建"),

        /**
         * 已分配
         */
        ASSIGNED(1, "已分配"),

        /**
         * 正在采购
         */
        BUYING(2, "正在采购"),

        /**
         * 已完成
         */
        FINISHED(3, "已完成"),

        /**
         * 采购失败
         */
        HAS_ERROR(4, "采购失败");

        private final int code;

        private final String msg;

    }

}
