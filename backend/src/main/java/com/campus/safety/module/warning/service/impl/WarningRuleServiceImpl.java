package com.campus.safety.module.warning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.warning.dto.WarningRuleDTO;
import com.campus.safety.module.warning.dto.WarningRuleQueryDTO;
import com.campus.safety.module.warning.entity.WarningRule;
import com.campus.safety.module.warning.mapper.WarningRuleMapper;
import com.campus.safety.module.warning.service.WarningRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 预警规则服务实现
 */
@Service
@RequiredArgsConstructor
public class WarningRuleServiceImpl implements WarningRuleService {

    private final WarningRuleMapper warningRuleMapper;

    @Override
    public PageResult<WarningRule> queryPage(WarningRuleQueryDTO queryDTO) {
        LambdaQueryWrapper<WarningRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRuleName()),
                WarningRule::getRuleName, queryDTO.getRuleName());
        wrapper.eq(queryDTO.getWarningType() != null,
                WarningRule::getWarningType, queryDTO.getWarningType());
        wrapper.eq(queryDTO.getWarningLevel() != null,
                WarningRule::getWarningLevel, queryDTO.getWarningLevel());
        wrapper.eq(queryDTO.getEnabled() != null,
                WarningRule::getEnabled, queryDTO.getEnabled());
        wrapper.orderByDesc(WarningRule::getCreateTime);

        Page<WarningRule> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<WarningRule> result = warningRuleMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public WarningRule getById(Long id) {
        WarningRule rule = warningRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("预警规则不存在");
        }
        return rule;
    }

    @Override
    public void add(WarningRuleDTO dto) {
        WarningRule rule = new WarningRule();
        BeanUtils.copyProperties(dto, rule);
        warningRuleMapper.insert(rule);
    }

    @Override
    public void update(WarningRuleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("预警规则ID不能为空");
        }
        WarningRule existing = warningRuleMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("预警规则不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        warningRuleMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        WarningRule rule = warningRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("预警规则不存在");
        }
        warningRuleMapper.deleteById(id);
    }

    @Override
    public void changeStatus(Long id, Integer enabled) {
        WarningRule rule = warningRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("预警规则不存在");
        }
        rule.setEnabled(enabled);
        warningRuleMapper.updateById(rule);
    }
}
