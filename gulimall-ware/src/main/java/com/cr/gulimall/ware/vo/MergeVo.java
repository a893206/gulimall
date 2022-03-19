package com.cr.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author cr
 * @date 2021/6/28 00:41
 */
@Data
public class MergeVo {
    /**
     * 整单id
     */
    private Long purchaseId;

    /**
     * 合并项集合
     */
    private List<Long> items;
}
