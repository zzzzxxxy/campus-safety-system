package com.campus.safety.module.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户新增/编辑DTO
 */
@Data
public class SysUserDTO implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private Integer userType;

    private Integer status;

    private List<Long> roleIds;
}
