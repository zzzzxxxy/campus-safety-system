package com.campus.safety.module.warning.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.warning.dto.WarningRuleDTO;
import com.campus.safety.module.warning.dto.WarningRuleQueryDTO;
import com.campus.safety.module.warning.entity.WarningRule;

/**
 * 预警规则服务接口
 */
public interface WarningRuleService {

    /**
     * 分页查询预警规则
     */
    PageResult<WarningRule> queryPage(WarningRuleQueryDTO queryDTO);

    /**
     * 根据ID查询预警规则
     */
    WarningRule getById(Long id);

    /**
     * 新增预警规则
     */
    void add(WarningRuleDTO dto);

    /**
     * 编辑预警规则
     */
    void update(WarningRuleDTO dto);

    /**
     * 删除预警规则
     */
    void delete(Long id);

    /**
     * 启用/禁用预警规则
     */
    void changeStatus(Long id, Integer enabled);
}
