package com.cr.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.product.entity.AttrGroupEntity;
import com.cr.gulimall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-31 23:20:58
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catalogId);

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     *
     * @param catalogId
     * @return
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatalogId(Long catalogId);

}

