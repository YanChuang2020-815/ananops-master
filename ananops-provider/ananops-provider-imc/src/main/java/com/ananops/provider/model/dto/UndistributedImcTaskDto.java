package com.ananops.provider.model.dto;

import com.ananops.provider.model.domain.ImcInspectionTask;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * Created by rongshuai on 2020/3/26 10:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class UndistributedImcTaskDto extends ImcInspectionTask {
    private static final long serialVersionUID = -6193139125257347336L;

    @ApiModelProperty(value = "任务截止日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "任务剩余时间（天）")
    private Integer remainDays;
}
