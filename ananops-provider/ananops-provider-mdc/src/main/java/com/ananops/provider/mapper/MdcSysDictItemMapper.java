package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.MdcSysDictItem;
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
public interface MdcSysDictItemMapper extends MyMapper<MdcSysDictItem> {

    @Select("select * from an_mdc_sys_dict_item where `dict_id`=#{id} and `group_id`=#{groupId} and `dr`=0 order by sort")
    List<MdcSysDictItem> selectBygDictId(@Param("id") Long dictId, @Param("groupId") Long groupId);
}
