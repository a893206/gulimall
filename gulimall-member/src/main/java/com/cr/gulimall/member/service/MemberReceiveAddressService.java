package com.cr.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:37:22
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

