package com.campus.safety.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统角色新增/编辑DTO
 */
@Data
public class SysRoleDTO implements Serializable {

    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    private Integer sort;

    private Integer status;

    private String remark;

    private List<Long> menuIds;
}
