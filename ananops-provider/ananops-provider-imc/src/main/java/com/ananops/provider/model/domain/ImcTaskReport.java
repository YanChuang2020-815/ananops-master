package com.ananops.provider.model.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "an_imc_task_report")
public class ImcTaskReport implements Serializable {
    private static final long serialVersionUID = 1602393920503487965L;
    /**
     * 巡检任务Id
     */
    @Id
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 附件Id
     */
    @Column(name = "attachment_id")
    private Long attachmentId;

    /**
     * 流水号
     */
    @Column(name = "ref_no")
    private String refNo;

}