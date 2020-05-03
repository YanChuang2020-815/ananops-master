package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdmc_troubletype_group")
public class MdmcTroubleType extends BaseEntity {
    private static final long serialVersionUID = -4338279757873928988L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trouble_type")
    private String troubleType;

    @Column(name = "group_id")
    private Long groupId;

}