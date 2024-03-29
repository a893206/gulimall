package com.cr.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.ware.entity.PurchaseEntity;
import com.cr.gulimall.ware.vo.MergeVo;
import com.cr.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-06-28 00:09:27
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    /**
     * 领取采购单
     *
     * @param ids 采购单id集合
     */
    void received(List<Long> ids);

    /**
     * 完成采购
     *
     * @param doneVo 完成采购Vo
     */
    void done(PurchaseDoneVo doneVo);
}

