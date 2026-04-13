package com.campus.safety.module.asset.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.DeviceDTO;
import com.campus.safety.module.asset.dto.DeviceQueryDTO;
import com.campus.safety.module.asset.entity.DeviceInfo;

import java.util.Map;

/**
 * 设备信息服务接口
 */
public interface DeviceInfoService {

    /**
     * 分页查询设备
     */
    PageResult<DeviceInfo> queryPage(DeviceQueryDTO queryDTO);

    /**
     * 根据ID查询设备
     */
    DeviceInfo getById(Long id);

    /**
     * 新增设备
     */
    void add(DeviceDTO dto);

    /**
     * 编辑设备
     */
    void update(DeviceDTO dto);

    /**
     * 删除设备
     */
    void delete(Long id);

    /**
     * 按类型统计
     */
    Map<String, Long> statsByType();

    /**
     * 按状态统计
     */
    Map<String, Long> statsByStatus();
}
