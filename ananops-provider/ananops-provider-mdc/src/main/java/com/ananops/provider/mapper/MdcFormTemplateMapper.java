package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.MdcFormSchema;
import com.ananops.provider.model.domain.MdcFormTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 表单模板
 *
 * @author Bingyue Duan
 *
 * @version 1.0
 *
 * @date 2020/4/19 下午5:14
 */
@Mapper
@Component
public interface MdcFormTemplateMapper extends MyMapper<MdcFormTemplate> {

    @Select("select * from an_mdc_form_template where `group_id`=-1 and `dr`=0")
    List<MdcFormTemplate> selectByDefault();

    @Select("select * from an_mdc_form_template where `group_id`=#{id} and `dr`=0")
    List<MdcFormTemplate> selectBygroupId(@Param("id")Long groupId);
}