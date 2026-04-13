package com.campus.safety.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.safety.module.system.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
