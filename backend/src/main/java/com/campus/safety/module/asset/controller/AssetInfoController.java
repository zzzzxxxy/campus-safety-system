package com.campus.safety.module.asset.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.AssetDTO;
import com.campus.safety.module.asset.dto.AssetQueryDTO;
import com.campus.safety.module.asset.entity.AssetInfo;
import com.campus.safety.module.asset.service.AssetInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产信息控制器
 */
@RestController
@RequestMapping("/asset/info")
@RequiredArgsConstructor
public class AssetInfoController {

    private final AssetInfoService assetInfoService;

    /**
     * 分页查询资产
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('asset:asset:list')")
    @Log(module = "资产管理", description = "分页查询资产")
    public R<PageResult<AssetInfo>> page(AssetQueryDTO queryDTO) {
        return R.ok(assetInfoService.queryPage(queryDTO));
    }

    /**
     * 获取资产详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:asset:list')")
    @Log(module = "资产管理", description = "查询资产详情")
    public R<AssetInfo> getById(@PathVariable Long id) {
        return R.ok(assetInfoService.getById(id));
    }

    /**
     * 新增资产
     */
    @PostMapping
    @PreAuthorize("hasAuthority('asset:asset:add')")
    @Log(module = "资产管理", description = "新增资产")
    public R<Void> add(@RequestBody @Valid AssetDTO dto) {
        assetInfoService.add(dto);
        return R.ok();
    }

    /**
     * 编辑资产
     */
    @PutMapping
    @PreAuthorize("hasAuthority('asset:asset:edit')")
    @Log(module = "资产管理", description = "编辑资产")
    public R<Void> update(@RequestBody @Valid AssetDTO dto) {
        assetInfoService.update(dto);
        return R.ok();
    }

    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:asset:delete')")
    @Log(module = "资产管理", description = "删除资产")
    public R<Void> delete(@PathVariable Long id) {
        assetInfoService.delete(id);
        return R.ok();
    }

    /**
     * 按状态统计
     */
    @GetMapping("/stats/status")
    @PreAuthorize("hasAuthority('asset:asset:list')")
    @Log(module = "资产管理", description = "按状态统计资产")
    public R<Map<String, Long>> statsByStatus() {
        return R.ok(assetInfoService.statsByStatus());
    }
}
