package com.ananops.provider.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 巡检表单反馈确认部分
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/20 下午3:23
 */
@Data
@ApiModel
public class InspcFeedBack implements Serializable {

    private static final long serialVersionUID = 1483466439244098379L;

    /**
     * 巡检结论
     */
    @ApiModelProperty(value = "巡检结论")
    private String inspcResult;

    /**
     * 巡检日期
     */
    @ApiModelProperty(value = "巡检日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date inspcDate;

    /**
     * 用户确认
     */
    @ApiModelProperty(value = "用户确认")
    private String userConfirm;

    /**
     * 工程师ID
     */
    @ApiModelProperty(value = "工程师ID")
    private Long engineerId;

    /**
     * 工程师名称
     */
    @ApiModelProperty(value = "工程师名称")
    private String engineer;
}
