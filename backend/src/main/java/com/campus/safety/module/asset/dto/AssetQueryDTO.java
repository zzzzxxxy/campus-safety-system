package com.campus.safety.module.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资产查询DTO
 */
@Data
public class AssetQueryDTO implements Serializable {

    private String assetName;

    private String assetCode;

    private String assetType;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
