package com.cr.gulimall.search.service;

import com.cr.gulimall.search.vo.SearchParam;

/**
 * @author cr
 * @date 2022/4/1 23:08
 */
public interface MallSearchService {
    /**
     * 检索
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果
     */
    Object search(SearchParam param);
}
