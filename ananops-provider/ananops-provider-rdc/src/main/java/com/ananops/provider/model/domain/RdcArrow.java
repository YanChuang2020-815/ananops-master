package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_rdc_arrow")
public class RdcArrow extends BaseEntity {
    /**
     * 标记所在的场景ID
     */
    @Column(name = "cur_scene_id")
    private Long curSceneId;

    /**
     * 标记指向的场景ID
     */
    @Column(name = "next_scene_id")
    private Long nextSceneId;

    /**
     * 箭头在全景图中的经度
     */
    private BigDecimal longitude;

    /**
     * 箭头在全景图中的纬度
     */
    private BigDecimal latitude;

}