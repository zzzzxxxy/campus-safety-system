package com.campus.safety.module.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统用户查询DTO
 */
@Data
public class SysUserQueryDTO implements Serializable {

    private String username;

    private String nickname;

    private String phone;

    private Integer status;

    private Integer userType;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
