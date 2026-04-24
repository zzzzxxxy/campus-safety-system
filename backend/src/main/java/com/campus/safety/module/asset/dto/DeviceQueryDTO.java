package com.campus.safety.module.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备查询DTO
 */
@Data
public class DeviceQueryDTO implements Serializable {

    private String deviceName;

    private String deviceCode;

    private Integer deviceType;

    private Integer status;

    /** 是否在线(0-离线 1-在线) */
    private Integer online;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
