package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_uac_api")
@Alias(value = "uacApi")
public class UacApi extends BaseEntity implements Serializable {

    /**
     * api路径
     */
    private String url;

    /**
     * api名称
     */
    @Column(name = "api_name")
    private String apiName;

    /**
     * api编码
     */
    @Column(name = "api_code")
    private String apiCode;

    /**
     * 状态
     */
    private String status;

    /**
     * 类型
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

}