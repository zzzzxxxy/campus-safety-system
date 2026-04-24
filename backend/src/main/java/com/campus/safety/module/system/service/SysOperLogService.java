package com.campus.safety.module.system.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysOperLogQueryDTO;
import com.campus.safety.module.system.entity.SysOperLog;

/**
 * 操作日志服务接口
 */
public interface SysOperLogService {

    /**
     * 分页查询操作日志
     */
    PageResult<SysOperLog> queryPage(SysOperLogQueryDTO queryDTO);

    /**
     * 清空操作日志
     */
    void clear();
}
