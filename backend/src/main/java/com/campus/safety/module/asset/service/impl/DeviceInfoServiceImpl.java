package com.campus.safety.module.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.asset.dto.DeviceDTO;
import com.campus.safety.module.asset.dto.DeviceQueryDTO;
import com.campus.safety.module.asset.entity.DeviceInfo;
import com.campus.safety.module.asset.mapper.DeviceInfoMapper;
import com.campus.safety.module.asset.service.DeviceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备信息服务实现
 */
@Service
@RequiredArgsConstructor
public class DeviceInfoServiceImpl implements DeviceInfoService {

    private final DeviceInfoMapper deviceInfoMapper;

    @Override
    public PageResult<DeviceInfo> queryPage(DeviceQueryDTO queryDTO) {
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getDeviceName()),
                DeviceInfo::getDeviceName, queryDTO.getDeviceName());
        wrapper.eq(StringUtils.hasText(queryDTO.getDeviceCode()),
                DeviceInfo::getDeviceCode, queryDTO.getDeviceCode());
        wrapper.eq(queryDTO.getDeviceType() != null,
                DeviceInfo::getDeviceType, queryDTO.getDeviceType());
        wrapper.eq(queryDTO.getStatus() != null,
                DeviceInfo::getStatus, queryDTO.getStatus());
        wrapper.eq(queryDTO.getOnline() != null,
                DeviceInfo::getOnline, queryDTO.getOnline());
        wrapper.orderByDesc(DeviceInfo::getCreateTime);

        Page<DeviceInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<DeviceInfo> result = deviceInfoMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public DeviceInfo getById(Long id) {
        DeviceInfo device = deviceInfoMapper.selectById(id);
        if (device == null) {
            throw new BusinessException("设备信息不存在");
        }
        return device;
    }

    @Override
    public void add(DeviceDTO dto) {
        DeviceInfo device = new DeviceInfo();
        BeanUtils.copyProperties(dto, device);
        deviceInfoMapper.insert(device);
    }

    @Override
    public void update(DeviceDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("设备ID不能为空");
        }
        DeviceInfo existing = deviceInfoMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("设备信息不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        deviceInfoMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        DeviceInfo device = deviceInfoMapper.selectById(id);
        if (device == null) {
            throw new BusinessException("设备信息不存在");
        }
        deviceInfoMapper.deleteById(id);
    }

    @Override
    public Map<String, Long> statsByType() {
        List<DeviceInfo> devices = deviceInfoMapper.selectList(
                new LambdaQueryWrapper<DeviceInfo>().select(DeviceInfo::getDeviceType)
        );
        return devices.stream()
                .filter(d -> d.getDeviceType() != null)
                .collect(Collectors.groupingBy(DeviceInfo::getDeviceType, Collectors.counting()));
    }

    @Override
    public Map<String, Long> statsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        // 正常(0)
        stats.put("normal", deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 0)));
        // 故障(1)
        stats.put("fault", deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 1)));
        // 维修(2)
        stats.put("repair", deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 2)));
        // 报废(3)
        stats.put("scrap", deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 3)));
        return stats;
    }
}
