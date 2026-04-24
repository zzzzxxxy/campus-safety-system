package com.campus.safety.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysOperLogQueryDTO;
import com.campus.safety.module.system.entity.SysOperLog;
import com.campus.safety.module.system.mapper.SysOperLogMapper;
import com.campus.safety.module.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<SysOperLog> queryPage(SysOperLogQueryDTO queryDTO) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getModule()), SysOperLog::getModule, queryDTO.getModule());
        wrapper.like(StringUtils.hasText(queryDTO.getOperUser()), SysOperLog::getOperUser, queryDTO.getOperUser());
        wrapper.eq(StringUtils.hasText(queryDTO.getRequestMethod()), SysOperLog::getRequestMethod, queryDTO.getRequestMethod());
        wrapper.eq(queryDTO.getStatus() != null, SysOperLog::getStatus, queryDTO.getStatus());
        if (StringUtils.hasText(queryDTO.getStartTime())) {
            wrapper.ge(SysOperLog::getOperTime, LocalDateTime.parse(queryDTO.getStartTime(), DATE_TIME_FORMATTER));
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            wrapper.le(SysOperLog::getOperTime, LocalDateTime.parse(queryDTO.getEndTime(), DATE_TIME_FORMATTER));
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<SysOperLog> result = sysOperLogMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public void clear() {
        sysOperLogMapper.delete(null);
    }
}
