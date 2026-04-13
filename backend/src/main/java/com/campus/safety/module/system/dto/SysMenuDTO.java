package com.campus.safety.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统菜单新增/编辑DTO
 */
@Data
public class SysMenuDTO implements Serializable {

    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String menuType;

    private String perms;

    private String icon;

    private Integer visible;

    private Integer status;
}
