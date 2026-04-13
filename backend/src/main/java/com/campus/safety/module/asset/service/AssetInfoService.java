package com.campus.safety.module.asset.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.AssetDTO;
import com.campus.safety.module.asset.dto.AssetQueryDTO;
import com.campus.safety.module.asset.entity.AssetInfo;

import java.util.Map;

/**
 * 资产信息服务接口
 */
public interface AssetInfoService {

    /**
     * 分页查询资产
     */
    PageResult<AssetInfo> queryPage(AssetQueryDTO queryDTO);

    /**
     * 根据ID查询资产
     */
    AssetInfo getById(Long id);

    /**
     * 新增资产
     */
    void add(AssetDTO dto);

    /**
     * 编辑资产
     */
    void update(AssetDTO dto);

    /**
     * 删除资产
     */
    void delete(Long id);

    /**
     * 按状态统计
     */
    Map<String, Long> statsByStatus();
}
