package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_rdc_scene")
public class RdcScene extends BaseEntity {
    private static final long serialVersionUID = -7453380525844979659L;
    /**
     * 场景名称
     */
    @Column(name = "scene_name")
    private String sceneName;

    /**
     * 场景图片的流水号
     */
    @Column(name = "ref_no")
    private String refNo;

    /**
     * 场景所属的组织id
     */
    @Column(name = "group_id")
    private Long groupId;
}