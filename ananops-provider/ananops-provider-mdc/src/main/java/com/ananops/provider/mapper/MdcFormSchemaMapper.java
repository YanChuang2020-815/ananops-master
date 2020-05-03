package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.MdcFormSchema;
import com.ananops.provider.model.domain.MdcSysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 表单Schema结构
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@Mapper
@Component
public interface MdcFormSchemaMapper extends MyMapper<MdcFormSchema> {

    @Select("select * from an_mdc_form_schema where `group_id`=-1 and `dr`=0")
    List<MdcFormSchema> selectByDefault();

    @Select("select * from an_mdc_form_schema where `group_id`=#{id} and `dr`=0")
    List<MdcFormSchema> selectBygroupId(@Param("id")Long groupId);
}