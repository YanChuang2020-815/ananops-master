package com.ananops.provider.model.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-03-05 17:53
 */
@Data
@ApiModel(value = "批量用户Id集合请求Dto")
public class UserIdsReqDto implements Serializable {

    private static final long serialVersionUID = -6362427669581128086L;

    /**
     * 用户Id集合
     */
    private List<Long> userIds;
}
