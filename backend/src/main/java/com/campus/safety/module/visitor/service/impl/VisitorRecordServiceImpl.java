package com.campus.safety.module.visitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.visitor.dto.VisitorAuditDTO;
import com.campus.safety.module.visitor.dto.VisitorDTO;
import com.campus.safety.module.visitor.dto.VisitorQueryDTO;
import com.campus.safety.module.visitor.entity.VisitorRecord;
import com.campus.safety.module.visitor.mapper.VisitorRecordMapper;
import com.campus.safety.module.visitor.service.VisitorRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 访客记录服务实现
 */
@Service
@RequiredArgsConstructor
public class VisitorRecordServiceImpl implements VisitorRecordService {

    private final VisitorRecordMapper visitorRecordMapper;

    @Override
    public PageResult<VisitorRecord> queryPage(VisitorQueryDTO queryDTO) {
        LambdaQueryWrapper<VisitorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getVisitorName()),
                VisitorRecord::getVisitorName, queryDTO.getVisitorName());
        wrapper.eq(StringUtils.hasText(queryDTO.getPhone()),
                VisitorRecord::getVisitorPhone, queryDTO.getPhone());
        wrapper.eq(queryDTO.getStatus() != null,
                VisitorRecord::getStatus, queryDTO.getStatus());
        wrapper.ge(queryDTO.getStartTime() != null,
                VisitorRecord::getVisitTime, queryDTO.getStartTime());
        wrapper.le(queryDTO.getEndTime() != null,
                VisitorRecord::getVisitTime, queryDTO.getEndTime());
        wrapper.orderByDesc(VisitorRecord::getCreateTime);

        Page<VisitorRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<VisitorRecord> result = visitorRecordMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public VisitorRecord getById(Long id) {
        VisitorRecord record = visitorRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        return record;
    }

    @Override
    public void add(VisitorDTO dto) {
        VisitorRecord record = new VisitorRecord();
        BeanUtils.copyProperties(dto, record);
        // 处理DTO与Entity字段名不一致的映射
        record.setVisitorPhone(dto.getPhone());
        record.setReason(dto.getVisitReason());
        record.setDepartment(dto.getVisitDepartment());
        record.setVisitee(dto.getVisitPerson());
        record.setVisitTime(dto.getVisitTime());
        record.setLeaveTime(dto.getLeaveTime());
        record.setStatus(0); // 默认待审批

        if (record.getLeaveTime() != null && record.getVisitTime() != null
                && record.getLeaveTime().isBefore(record.getVisitTime())) {
            throw new BusinessException("预计离开时间不能早于预计到访时间");
        }

        visitorRecordMapper.insert(record);
    }

    @Override
    public void update(VisitorDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("访客记录ID不能为空");
        }
        VisitorRecord existing = visitorRecordMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("访客记录不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        // 处理DTO与Entity字段名不一致的映射
        existing.setVisitorPhone(dto.getPhone());
        existing.setReason(dto.getVisitReason());
        existing.setDepartment(dto.getVisitDepartment());
        existing.setVisitee(dto.getVisitPerson());
        existing.setVisitTime(dto.getVisitTime());
        existing.setLeaveTime(dto.getLeaveTime());

        if (existing.getLeaveTime() != null && existing.getVisitTime() != null
                && existing.getLeaveTime().isBefore(existing.getVisitTime())) {
            throw new BusinessException("预计离开时间不能早于预计到访时间");
        }

        visitorRecordMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        VisitorRecord record = visitorRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        visitorRecordMapper.deleteById(id);
    }

    @Override
    public void audit(VisitorAuditDTO dto) {
        VisitorRecord record = visitorRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException("该访客记录已审核，不可重复操作");
        }
        record.setStatus(dto.getStatus());
        record.setRemark(dto.getRemark());
        visitorRecordMapper.updateById(record);
    }

    @Override
    public void checkIn(Long id) {
        VisitorRecord record = visitorRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        if (record.getStatus() != 1) {
            throw new BusinessException("该访客未通过审批，无法签到");
        }
        if (record.getActualVisitTime() != null) {
            throw new BusinessException("该访客已签到，不可重复签到");
        }
        record.setActualVisitTime(LocalDateTime.now());
        visitorRecordMapper.updateById(record);
    }

    @Override
    public void checkOut(Long id) {
        VisitorRecord record = visitorRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        if (record.getActualVisitTime() == null) {
            throw new BusinessException("该访客未签到，无法签退");
        }
        if (record.getActualLeaveTime() != null) {
            throw new BusinessException("该访客已签退，不可重复签退");
        }
        record.setActualLeaveTime(LocalDateTime.now());
        visitorRecordMapper.updateById(record);
    }

    @Override
    public Map<String, Object> todayStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        LambdaQueryWrapper<VisitorRecord> baseWrapper = new LambdaQueryWrapper<VisitorRecord>()
                .ge(VisitorRecord::getCreateTime, todayStart)
                .le(VisitorRecord::getCreateTime, todayEnd);

        // 今日总数
        Long total = visitorRecordMapper.selectCount(baseWrapper);

        // 待审批数
        Long pending = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .ge(VisitorRecord::getCreateTime, todayStart)
                        .le(VisitorRecord::getCreateTime, todayEnd)
                        .eq(VisitorRecord::getStatus, 0)
        );

        // 已通过数
        Long approved = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .ge(VisitorRecord::getCreateTime, todayStart)
                        .le(VisitorRecord::getCreateTime, todayEnd)
                        .eq(VisitorRecord::getStatus, 1)
        );

        // 已入校数（有实际到访时间）
        Long checkedIn = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .ge(VisitorRecord::getCreateTime, todayStart)
                        .le(VisitorRecord::getCreateTime, todayEnd)
                        .isNotNull(VisitorRecord::getActualVisitTime)
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("checkedIn", checkedIn);
        return stats;
    }

    @Override
    public List<VisitorRecord> exportList(VisitorQueryDTO queryDTO) {
        LambdaQueryWrapper<VisitorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getVisitorName()),
                VisitorRecord::getVisitorName, queryDTO.getVisitorName());
        wrapper.eq(StringUtils.hasText(queryDTO.getPhone()),
                VisitorRecord::getVisitorPhone, queryDTO.getPhone());
        wrapper.eq(queryDTO.getStatus() != null,
                VisitorRecord::getStatus, queryDTO.getStatus());
        wrapper.ge(queryDTO.getStartTime() != null,
                VisitorRecord::getVisitTime, queryDTO.getStartTime());
        wrapper.le(queryDTO.getEndTime() != null,
                VisitorRecord::getVisitTime, queryDTO.getEndTime());
        wrapper.orderByDesc(VisitorRecord::getCreateTime);
        return visitorRecordMapper.selectList(wrapper);
    }
}

