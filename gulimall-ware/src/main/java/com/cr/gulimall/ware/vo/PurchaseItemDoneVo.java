package com.cr.gulimall.ware.vo;

import lombok.Data;

/**
 * 完成采购项Vo
 *
 * @author cr
 * @date 2021/8/25 23:54
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;

    private Integer status;

    private String reason;
}
