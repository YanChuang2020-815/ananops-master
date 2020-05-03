package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.MdcSysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@Mapper
@Component
public interface MdcSysDictMapper  extends MyMapper<MdcSysDict> {
    @Select("select * from an_mdc_sys_dict where `group_id`=-1 and `dr`=0")
    List<MdcSysDict> selectByDefault();

    @Select("select * from an_mdc_sys_dict where `group_id`=#{id} and `dr`=0")
    List<MdcSysDict> selectBygroupId(@Param("id")Long groupId);
}
