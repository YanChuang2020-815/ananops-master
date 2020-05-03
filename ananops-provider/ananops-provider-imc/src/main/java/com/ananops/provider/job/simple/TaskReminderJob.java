package com.ananops.provider.job.simple;

import com.ananops.elastic.lite.annotation.ElasticJobConfig;
import com.ananops.provider.manager.ImcTaskManager;
import com.ananops.provider.mapper.ImcInspectionItemMapper;
import com.ananops.provider.mapper.ImcInspectionTaskMapper;
import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.domain.MqMessageData;
import com.ananops.provider.model.dto.UndistributedImcTaskDto;
import com.ananops.provider.model.enums.ItemStatusEnum;
import com.ananops.provider.mq.producer.TaskMsgProducer;
import com.ananops.provider.service.ImcInspectionItemService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by rongshuai on 2019/12/17 18:43
 */
@Slf4j
@ElasticJobConfig(cron = "0 0 10 * * ?")//每天上午10点触发，进行数据库查询
public class TaskReminderJob implements SimpleJob {

    @Resource
    ImcTaskManager imcTaskManager;

    @Resource
    TaskMsgProducer taskMsgProducer;

    @Resource
    ImcInspectionTaskMapper imcInspectionTaskMapper;


    @Override
    public void execute(final ShardingContext shardingContext){
        log.info("定时任务>>>>>>>>>>:获取全部未分配工程师，且距离截止日期<=3天的全部巡检任务" );
        List<UndistributedImcTaskDto> undistributedImcTaskDtoList = imcInspectionTaskMapper.queryAllUndistributedTaskWithTime();
        log.info("任务列表：");
        undistributedImcTaskDtoList.forEach(item->{
            log.info("taskName={},taskId={},ScheduledStartTime={},endDate={},remainDays={}",item.getTaskName(),item.getId(),item.getScheduledStartTime(),item.getEndDate(),item.getRemainDays());
            MqMessageData mqMessageData = taskMsgProducer.sendNotifyMsgToFacilitator(item);
            imcTaskManager.notifyFacilitator(mqMessageData);
        });

//        Example example = new Example(ImcInspectionItem.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("status",0);
//        //获取所有当前状态为待巡检的巡检任务子项
//        List<ImcInspectionItem> imcInspectionItemList = imcInspectionItemMapper.selectByExample(example);
//        imcInspectionItemList.forEach(item->{
//            Date scheduledStartTime = item.getScheduledStartTime();
//            Long startTime = scheduledStartTime.getTime();//计划巡检开始时间
//            Long currentTime = System.currentTimeMillis();
//            Integer frequency = item.getFrequency();//巡检频率
//            Integer days = item.getDays();//巡检时长
//            if(currentTime>=startTime){//如果已经到了巡检任务子项的开始时间
//                Integer passed = (int)(currentTime - startTime)/(1000*60*60*24*frequency);//获取从巡检任务子项开始到现在已经经历了多久
//                Integer count = item.getCount();//获取任务已经执行的次数
//                System.out.println("days:" + days +  "\nfrequency:" + frequency +  "\npassed:" + passed + "\ncount:" + count);
//                if(passed>count && passed <days){//如果任务已经执行的次数<任务目前应该执行的次数，并且任务还在执行期限中
//                    //执行该定时任务
//                    System.out.println("定时任务：" + item.getItemName());
//                    //更新巡检任务子项的执行次数
//                    item.setCount(passed);
//                    //将巡检任务子项状态设置为等待巡检
//                    item.setStatus(ItemStatusEnum.WAITING_FOR_MAINTAINER.getStatusNum());
//                    imcInspectionItemMapper.updateByPrimaryKeySelective(item);
//                }
//            }
//        });
    }
}
