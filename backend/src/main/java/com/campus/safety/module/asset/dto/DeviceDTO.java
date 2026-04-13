package com.campus.safety.module.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 设备新增/编辑DTO
 */
@Data
public class DeviceDTO implements Serializable {

    private Long id;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    private String deviceCode;

    private Integer deviceType;

    private String location;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastMaintenanceDate;

    private String responsiblePerson;

    private String remark;
}
