package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_mdmc_file_task_status")
public class MdmcFileTaskStatus extends BaseEntity {
    private static final long serialVersionUID = -6600369120374658539L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="status")
    private Integer status;

    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "task_id")
    private Long taskId;

}