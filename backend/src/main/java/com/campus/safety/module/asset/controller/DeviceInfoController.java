package com.campus.safety.module.asset.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.DeviceDTO;
import com.campus.safety.module.asset.dto.DeviceQueryDTO;
import com.campus.safety.module.asset.entity.DeviceInfo;
import com.campus.safety.module.asset.service.DeviceInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备信息控制器
 */
@RestController
@RequestMapping("/asset/device")
@RequiredArgsConstructor
public class DeviceInfoController {

    private final DeviceInfoService deviceInfoService;

    /**
     * 分页查询设备
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('asset:device:list')")
    @Log(module = "设备管理", description = "分页查询设备")
    public R<PageResult<DeviceInfo>> page(DeviceQueryDTO queryDTO) {
        return R.ok(deviceInfoService.queryPage(queryDTO));
    }

    /**
     * 获取设备详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:device:list')")
    @Log(module = "设备管理", description = "查询设备详情")
    public R<DeviceInfo> getById(@PathVariable Long id) {
        return R.ok(deviceInfoService.getById(id));
    }

    /**
     * 新增设备
     */
    @PostMapping
    @PreAuthorize("hasAuthority('asset:device:add')")
    @Log(module = "设备管理", description = "新增设备")
    public R<Void> add(@RequestBody @Valid DeviceDTO dto) {
        deviceInfoService.add(dto);
        return R.ok();
    }

    /**
     * 编辑设备
     */
    @PutMapping
    @PreAuthorize("hasAuthority('asset:device:edit')")
    @Log(module = "设备管理", description = "编辑设备")
    public R<Void> update(@RequestBody @Valid DeviceDTO dto) {
        deviceInfoService.update(dto);
        return R.ok();
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:device:delete')")
    @Log(module = "设备管理", description = "删除设备")
    public R<Void> delete(@PathVariable Long id) {
        deviceInfoService.delete(id);
        return R.ok();
    }

    /**
     * 按类型统计
     */
    @GetMapping("/stats/type")
    @PreAuthorize("hasAuthority('asset:device:list')")
    @Log(module = "设备管理", description = "按类型统计设备")
    public R<Map<String, Long>> statsByType() {
        return R.ok(deviceInfoService.statsByType());
    }

    /**
     * 按状态统计
     */
    @GetMapping("/stats/status")
    @PreAuthorize("hasAuthority('asset:device:list')")
    @Log(module = "设备管理", description = "按状态统计设备")
    public R<Map<String, Long>> statsByStatus() {
        return R.ok(deviceInfoService.statsByStatus());
    }
}
