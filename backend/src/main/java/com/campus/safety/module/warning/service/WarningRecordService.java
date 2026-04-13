package com.campus.safety.module.warning.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.warning.dto.WarningHandleDTO;
import com.campus.safety.module.warning.dto.WarningQueryDTO;
import com.campus.safety.module.warning.dto.WarningRecordDTO;
import com.campus.safety.module.warning.entity.WarningRecord;

import java.util.Map;

/**
 * 预警记录服务接口
 */
public interface WarningRecordService {

    /**
     * 分页查询预警记录
     */
    PageResult<WarningRecord> queryPage(WarningQueryDTO queryDTO);

    /**
     * 根据ID查询预警记录
     */
    WarningRecord getById(Long id);

    /**
     * 新增预警记录
     */
    void add(WarningRecordDTO dto);

    /**
     * 编辑预警记录
     */
    void update(WarningRecordDTO dto);

    /**
     * 删除预警记录
     */
    void delete(Long id);

    /**
     * 处理预警
     */
    void handle(WarningHandleDTO dto);

    /**
     * 按类型统计
     */
    Map<String, Long> statsByType();

    /**
     * 按级别统计
     */
    Map<String, Long> statsByLevel();

    /**
     * 未处理数量
     */
    long unhandledCount();
}
