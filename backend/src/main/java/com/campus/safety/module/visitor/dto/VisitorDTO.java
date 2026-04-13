package com.campus.safety.module.visitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 访客新增/编辑DTO
 */
@Data
public class VisitorDTO implements Serializable {

    private Long id;

    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;

    private String idCard;

    private String phone;

    private String visitReason;

    private String visitDepartment;

    private String visitPerson;

    private String vehicleNumber;

    private String remark;
}
