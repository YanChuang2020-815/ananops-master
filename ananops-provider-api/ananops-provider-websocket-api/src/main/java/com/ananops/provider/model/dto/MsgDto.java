package com.ananops.provider.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/5/21 19:18
 */
@Data
public class MsgDto implements Serializable {
    private static final long serialVersionUID = -2121351983295993313L;

    private String content;
}
