package com.cr.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 完成采购Vo
 *
 * @author cr
 * @date 2021/8/25 23:53
 */
@Data
public class PurchaseDoneVo {
    /**
     * 采购单id
     */
    @NotNull
    private Long id;

    private List<PurchaseItemDoneVo> items;
}
