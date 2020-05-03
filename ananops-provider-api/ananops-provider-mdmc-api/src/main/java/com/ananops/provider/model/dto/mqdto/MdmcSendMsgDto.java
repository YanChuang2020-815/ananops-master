package com.ananops.provider.model.dto.mqdto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/2/24
 */
@Data
public class MdmcSendMsgDto implements Serializable {
    private static final long serialVersionUID = -4259935599812651229L;

    /**
     * 工单ID
     */
    private Long taskId;

    /**
     * 修改后的状态
     */
    private Integer status;

    /**
     * 工单状态修改后的状态描述
     */
    private String statusMsg;

    /**
     * 需要推送的用户的Id
     */
    private Long userId;
}
