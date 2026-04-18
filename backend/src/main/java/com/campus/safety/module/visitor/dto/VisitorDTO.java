package com.campus.safety.module.visitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访客新增/编辑DTO
 */
@Data
public class VisitorDTO implements Serializable {

    private Long id;

    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;

    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "来访事由不能为空")
    private String visitReason;

    @NotBlank(message = "被访部门不能为空")
    private String visitDepartment;

    @NotBlank(message = "被访人不能为空")
    private String visitPerson;

    // 可选：车牌号（有车牌时校验格式）
    @Pattern(
            regexp = "^$|^[\\u4e00-\\u9fa5][A-Z][A-Z0-9]{5,6}$",
            message = "车牌号格式不正确"
    )
    private String vehicleNumber;

    @NotNull(message = "预计到访时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitTime;

    @NotNull(message = "预计离开时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveTime;

    private String remark;
}
