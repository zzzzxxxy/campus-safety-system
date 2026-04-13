package com.campus.safety.module.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统角色查询DTO
 */
@Data
public class SysRoleQueryDTO implements Serializable {

    private String roleName;

    private String roleKey;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
