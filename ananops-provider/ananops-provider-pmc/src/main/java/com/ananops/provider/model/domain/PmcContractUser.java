package com.ananops.provider.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Table(name = "an_pmc_contract_user")
@Data
@ApiModel
public class PmcContractUser {

    @Column(name = "contract_id")
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @Column(name = "user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;


}