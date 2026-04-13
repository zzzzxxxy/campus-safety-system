package com.campus.safety.module.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.AssetDTO;
import com.campus.safety.module.asset.dto.AssetQueryDTO;
import com.campus.safety.module.asset.entity.AssetInfo;
import com.campus.safety.module.asset.mapper.AssetInfoMapper;
import com.campus.safety.module.asset.service.AssetInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 资产信息服务实现
 */
@Service
@RequiredArgsConstructor
public class AssetInfoServiceImpl implements AssetInfoService {

    private final AssetInfoMapper assetInfoMapper;

    @Override
    public PageResult<AssetInfo> queryPage(AssetQueryDTO queryDTO) {
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getAssetName()),
                AssetInfo::getAssetName, queryDTO.getAssetName());
        wrapper.eq(StringUtils.hasText(queryDTO.getAssetCode()),
                AssetInfo::getAssetCode, queryDTO.getAssetCode());
        wrapper.eq(StringUtils.hasText(queryDTO.getAssetType()),
                AssetInfo::getAssetType, queryDTO.getAssetType());
        wrapper.eq(queryDTO.getStatus() != null,
                AssetInfo::getStatus, queryDTO.getStatus());
        wrapper.orderByDesc(AssetInfo::getCreateTime);

        Page<AssetInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<AssetInfo> result = assetInfoMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public AssetInfo getById(Long id) {
        AssetInfo asset = assetInfoMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产信息不存在");
        }
        return asset;
    }

    @Override
    public void add(AssetDTO dto) {
        AssetInfo asset = new AssetInfo();
        BeanUtils.copyProperties(dto, asset);
        // 处理DTO与Entity字段名不一致的映射
        asset.setResponsible(dto.getCustodian());
        asset.setAssetValue(dto.getPurchasePrice());
        assetInfoMapper.insert(asset);
    }

    @Override
    public void update(AssetDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("资产ID不能为空");
        }
        AssetInfo existing = assetInfoMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("资产信息不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        // 处理DTO与Entity字段名不一致的映射
        existing.setResponsible(dto.getCustodian());
        existing.setAssetValue(dto.getPurchasePrice());
        assetInfoMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        AssetInfo asset = assetInfoMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产信息不存在");
        }
        assetInfoMapper.deleteById(id);
    }

    @Override
    public Map<String, Long> statsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        // 在用(0)
        stats.put("inUse", assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 0)));
        // 闲置(1)
        stats.put("idle", assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 1)));
        // 报废(2)
        stats.put("scrap", assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 2)));
        // 维修(3)
        stats.put("repair", assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 3)));
        return stats;
    }
}
