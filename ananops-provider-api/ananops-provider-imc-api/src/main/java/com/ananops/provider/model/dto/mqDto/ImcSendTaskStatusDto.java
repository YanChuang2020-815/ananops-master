package com.ananops.provider.model.dto.mqDto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/2/22 16:40
 */
@Data
public class ImcSendTaskStatusDto implements Serializable {
    private static final long serialVersionUID = 3821954164521208297L;

    /**
     * 巡检任务ID
     */
    private Long taskId;

    /**
     * 修改后的状态
     */
    private Integer status;

    /**
     * 巡检任务修改后的状态描述
     */
    private String statusMsg;

    /**
     * 需要推送的用户的Id
     */
    private Long userId;
}
