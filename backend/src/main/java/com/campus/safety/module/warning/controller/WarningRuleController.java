package com.campus.safety.module.warning.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.warning.dto.WarningRuleDTO;
import com.campus.safety.module.warning.dto.WarningRuleQueryDTO;
import com.campus.safety.module.warning.entity.WarningRule;
import com.campus.safety.module.warning.service.WarningRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 预警规则控制器
 */
@RestController
@RequestMapping("/warning/rule")
@RequiredArgsConstructor
public class WarningRuleController {

    private final WarningRuleService warningRuleService;

    /**
     * 分页查询预警规则
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('warning:rule:list')")
    @Log(module = "预警规则管理", description = "分页查询预警规则")
    public R<PageResult<WarningRule>> page(WarningRuleQueryDTO queryDTO) {
        return R.ok(warningRuleService.queryPage(queryDTO));
    }

    /**
     * 获取预警规则详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warning:rule:list')")
    @Log(module = "预警规则管理", description = "查询预警规则详情")
    public R<WarningRule> getById(@PathVariable Long id) {
        return R.ok(warningRuleService.getById(id));
    }

    /**
     * 新增预警规则
     */
    @PostMapping
    @PreAuthorize("hasAuthority('warning:rule:add')")
    @Log(module = "预警规则管理", description = "新增预警规则")
    public R<Void> add(@RequestBody @Valid WarningRuleDTO dto) {
        warningRuleService.add(dto);
        return R.ok();
    }

    /**
     * 编辑预警规则
     */
    @PutMapping
    @PreAuthorize("hasAuthority('warning:rule:edit')")
    @Log(module = "预警规则管理", description = "编辑预警规则")
    public R<Void> update(@RequestBody @Valid WarningRuleDTO dto) {
        warningRuleService.update(dto);
        return R.ok();
    }

    /**
     * 删除预警规则
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warning:rule:delete')")
    @Log(module = "预警规则管理", description = "删除预警规则")
    public R<Void> delete(@PathVariable Long id) {
        warningRuleService.delete(id);
        return R.ok();
    }

    /**
     * 启用/禁用预警规则
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('warning:rule:edit')")
    @Log(module = "预警规则管理", description = "启用/禁用预警规则")
    public R<Void> changeStatus(@PathVariable Long id, @RequestParam Integer enabled) {
        warningRuleService.changeStatus(id, enabled);
        return R.ok();
    }
}
