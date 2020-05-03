package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdmc_troubleaddress_group")
public class MdmcTroubleAddress extends BaseEntity {
    private static final long serialVersionUID = -4975430143895699985L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trouble_address")
    private String troubleAddress;

    @Column(name = "trouble_latitude")
    private Long troubleLatitude;

    @Column(name = "trouble_longitude")
    private Long troubleLongitude;

    @Column(name = "group_id")
    private Long groupId;


}