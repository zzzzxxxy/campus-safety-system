package com.campus.safety.module.warning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.common.utils.SecurityUtils;
import com.campus.safety.module.warning.dto.WarningHandleDTO;
import com.campus.safety.module.warning.dto.WarningQueryDTO;
import com.campus.safety.module.warning.dto.WarningRecordDTO;
import com.campus.safety.module.warning.entity.WarningRecord;
import com.campus.safety.module.warning.mapper.WarningRecordMapper;
import com.campus.safety.module.warning.service.WarningRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警记录服务实现
 */
@Service
@RequiredArgsConstructor
public class WarningRecordServiceImpl implements WarningRecordService {

    private final WarningRecordMapper warningRecordMapper;

    @Override
    public PageResult<WarningRecord> queryPage(WarningQueryDTO queryDTO) {
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getWarningType() != null,
                WarningRecord::getWarningType, queryDTO.getWarningType());
        wrapper.eq(queryDTO.getWarningLevel() != null,
                WarningRecord::getWarningLevel, queryDTO.getWarningLevel());
        wrapper.like(StringUtils.hasText(queryDTO.getTitle()),
                WarningRecord::getTitle, queryDTO.getTitle());
        wrapper.eq(queryDTO.getStatus() != null,
                WarningRecord::getStatus, queryDTO.getStatus());
        wrapper.ge(queryDTO.getStartTime() != null,
                WarningRecord::getCreateTime, queryDTO.getStartTime());
        wrapper.le(queryDTO.getEndTime() != null,
                WarningRecord::getCreateTime, queryDTO.getEndTime());
        wrapper.orderByDesc(WarningRecord::getCreateTime);

        Page<WarningRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<WarningRecord> result = warningRecordMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public WarningRecord getById(Long id) {
        WarningRecord record = warningRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("预警记录不存在");
        }
        return record;
    }

    @Override
    public void add(WarningRecordDTO dto) {
        WarningRecord record = new WarningRecord();
        BeanUtils.copyProperties(dto, record);
        record.setStatus(0);
        warningRecordMapper.insert(record);
    }

    @Override
    public void update(WarningRecordDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("预警记录ID不能为空");
        }
        WarningRecord existing = warningRecordMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("预警记录不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        warningRecordMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        WarningRecord record = warningRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("预警记录不存在");
        }
        warningRecordMapper.deleteById(id);
    }

    @Override
    public void handle(WarningHandleDTO dto) {
        WarningRecord record = warningRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BusinessException("预警记录不存在");
        }
        record.setHandler(SecurityUtils.getUsername());
        record.setHandleTime(LocalDateTime.now());
        record.setStatus(dto.getStatus());
        record.setHandleResult(dto.getHandleResult());
        warningRecordMapper.updateById(record);
    }

    @Override
    public Map<String, Long> statsByType() {
        List<WarningRecord> records = warningRecordMapper.selectList(
                new LambdaQueryWrapper<WarningRecord>().select(WarningRecord::getWarningType)
        );
        return records.stream()
                .filter(r -> r.getWarningType() != null)
                .collect(Collectors.groupingBy(WarningRecord::getWarningType, Collectors.counting()));
    }

    @Override
    public Map<String, Long> statsByLevel() {
        List<WarningRecord> records = warningRecordMapper.selectList(
                new LambdaQueryWrapper<WarningRecord>().select(WarningRecord::getWarningLevel)
        );
        return records.stream()
                .filter(r -> r.getWarningLevel() != null)
                .collect(Collectors.groupingBy(WarningRecord::getWarningLevel, Collectors.counting()));
    }

    @Override
    public long unhandledCount() {
        return warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>().eq(WarningRecord::getStatus, 0)
        );
    }
}
