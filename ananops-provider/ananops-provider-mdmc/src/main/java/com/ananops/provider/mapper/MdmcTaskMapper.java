package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.MdmcFileTaskStatus;
import com.ananops.provider.model.domain.MdmcTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MdmcTaskMapper extends MyMapper<MdmcTask> {
    @Select("select * from task left join task_item on task.`id`=task_item.task_id where task_item.maintainer_id = #{maintainer_id} and task.`status`=#{status}")
    List<MdmcTask> selectByMaintainerIdAndStatus(@Param("maintainer_id") Long maintainer_id, @Param("status") Integer status);

    @Select("select * from task where task.`user_id`=#{user_id}")
    List<MdmcTask> selectByUserId(@Param("user_id") Long user_id);

    @Select("select * from task where `user_id`=#{id} or `facilitator_id`=#{id} or `maintainer_id`=#{id} or principal_id=#{id} order by created_time desc")
    List<MdmcTask> selectBySomeoneId(@Param("id")Long id);

    @Select("select * from task where `status`=#{status} and (`user_id`=#{id} or `facilitator_id`=#{id} or `maintainer_id`=#{id} or principal_id=#{id}) order by created_time desc")
    List<MdmcTask> selectBySomeoneIdAndStatus(@Param("id")Long userId, @Param("status")Integer status);

    @Select("select * from task where `status`>2 and `facilitator_id`=#{id} order by created_time desc")
    List<MdmcTask> selectByFacId(@Param("id")Long userId);

    @Select("select * from task where `status`>4 and `status`<>14 and `maintainer_id`=#{id} order by created_time desc")
    List<MdmcTask> selectByMantainerId(@Param("id")Long userId);

    @Select("select * from an_mdmc_file_task_status where `status`=#{status} and `task_id`=#{id}")
    List<MdmcFileTaskStatus> selectByTaskIdAndStatus(@Param("id")Long taskId, @Param("status")Integer status);

}