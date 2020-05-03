package com.ananops.provider.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created By ChengHao On 2020/2/29
 */
@Data
public class AmcSendAlarmOccurDto implements Serializable {
    private static final long serialVersionUID = -4213070861598386861L;

    @ApiModelProperty(value = "需要推送的用户Id")
    private Long userId;

    @ApiModelProperty(value = "报警id")
    private Long alarmId;

    @ApiModelProperty(value = "告警等级，1-紧急，2-可疑，3-提醒")
    private Integer alarmLevel;


    @ApiModelProperty(value = "报警位置")
    private String alarmLocation;

    @ApiModelProperty(value = "最近发生时间",example = "2019-12-01 12:18:48")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastOccurTime;
}
