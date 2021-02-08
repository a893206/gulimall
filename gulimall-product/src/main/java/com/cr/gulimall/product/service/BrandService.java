package com.cr.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

}

