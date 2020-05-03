package com.ananops.provider.model.dto.mqDto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/2/22 16:41
 */
@Data
public class ImcSendItemStatusDto implements Serializable {
    private static final long serialVersionUID = 1002994002488546159L;

    /**
     * 巡检任务子项的ID
     */
    private Long itemId;

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
