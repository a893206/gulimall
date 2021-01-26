package com.cr.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:37:22
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

