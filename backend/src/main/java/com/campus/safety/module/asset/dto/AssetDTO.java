package com.campus.safety.module.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产新增/编辑DTO
 */
@Data
public class AssetDTO implements Serializable {

    private Long id;

    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    private String assetCode;

    private String assetType;

    private String location;

    private String custodian;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private Integer status;

    private String remark;
}
