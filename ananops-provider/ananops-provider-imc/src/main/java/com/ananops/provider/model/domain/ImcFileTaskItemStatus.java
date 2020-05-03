package com.ananops.provider.model.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "an_imc_file_task_item_status")
@Alias(value = "imcFileTaskItemStatus")
public class ImcFileTaskItemStatus implements Serializable {
    private static final long serialVersionUID = 9174676210113767433L;
    /**
     * 巡检任务Id
     */
    @Column(name = "taskId")
    private Long taskId;

    /**
     * 巡检任务子项Id
     */
    @Column(name = "itemId")
    private Long itemId;

    /**
     * 附件Id
     */
    @Column(name = "attachmentId")
    private Long attachmentId;

    /**
     * 巡检任务子项的状态
     */
    @Column(name = "itemStatus")
    private Integer itemStatus;

    /**
     * 工单的流水号
     */
    @Column(name = "refNo")
    private String refNo;
}