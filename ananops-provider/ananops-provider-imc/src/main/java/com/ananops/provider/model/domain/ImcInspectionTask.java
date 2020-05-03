package com.ananops.provider.model.domain;

import com.ananops.core.mybatis.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "an_imc_inspection_task")
public class ImcInspectionTask extends BaseEntity {
    private static final long serialVersionUID = 3695350625553345594L;
    /**
     * 项目负责人ID
     */
    @Column(name = "principal_id")
    private Long principalId;

    /**
     * 服务商ID
     */
    @Column(name = "facilitator_id")
    private Long facilitatorId;

    /**
     * 项目ID
     */
    @Column(name = "project_id")
    private Long projectId;

    /**
     * 巡检位置信息
     */
    private String location;

    /**
     * 当前巡检任务进度
     */
    private Integer status;

    /**
     * 本次巡检总花费
     */
    @Column(name = "total_cost")
    private BigDecimal totalCost;

    /**
     * 巡检产生的维修维护费用
     */
    @Column(name = "maintenance_cost")
    private BigDecimal maintenanceCost;

    /**
     * 计划起始时间
     */
    @Column(name = "scheduled_start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date scheduledStartTime;

    /**
     * 实际完成时间
     */
    @Column(name = "actual_finish_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualFinishTime;

    /**
     * 计划完成天数
     */
    private Integer days;

    /**
     * 巡检类型（1.按合同产生的巡检，2.甲方负责人主动发出的巡检）
     */
    @Column(name = "inspection_type")
    private Integer inspectionType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 巡检任务名称
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 巡检周期（月）
     */
    private Integer frequency;

    /**
     * 巡检任务内容
     */
    private String content;

    /**
     * 本次巡检任务所需巡检点位总数
     */
    @Column(name = "point_sum")
    private Integer pointSum;
}