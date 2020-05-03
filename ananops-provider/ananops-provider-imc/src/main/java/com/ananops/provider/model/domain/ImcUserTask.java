package com.ananops.provider.model.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "an_imc_user_task")
@Alias(value = "imcUserTask")
public class ImcUserTask implements Serializable {
    private static final long serialVersionUID = 2215981204149295877L;
    /**
     * 甲方用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 巡检任务id
     */
    @Column(name = "task_id")
    private Long taskId;

}