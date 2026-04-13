package com.campus.safety.module.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_info")
public class DeviceInfo extends BaseEntity {

    /** 设备名称 */
    private String deviceName;

    /** 设备编号 */
    private String deviceCode;

    /** 设备类型 */
    private String deviceType;

    /** 设备品牌 */
    private String brand;

    /** 设备型号 */
    private String model;

    /** 安装位置 */
    private String location;

    /** IP地址 */
    private String ipAddress;

    /** 设备状态(0-正常 1-故障 2-维修 3-报废) */
    private Integer status;

    /** 是否在线(0-离线 1-在线) */
    private Integer online;

    /** 备注 */
    private String remark;
}
