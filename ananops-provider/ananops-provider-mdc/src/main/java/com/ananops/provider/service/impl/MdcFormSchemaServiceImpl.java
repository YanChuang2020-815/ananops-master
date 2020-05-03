package com.ananops.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.MdcFormSchemaMapper;
import com.ananops.provider.model.domain.MdcFormSchema;
import com.ananops.provider.model.dto.MdcFormSchemaDto;
import com.ananops.provider.service.MdcFormSchemaService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/20 上午10:12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MdcFormSchemaServiceImpl extends BaseService<MdcFormSchema> implements MdcFormSchemaService {

    @Resource
    private MdcFormSchemaMapper mdcFormSchemaMapper;

    @Override
    public List<MdcFormSchemaDto> getInspcFormSchema(LoginAuthDto loginAuthDto) {
        // 这里默认只能管理员登录，管理员账号直接挂在公司组织下的，所以用户组织就是公司组织
        Long groupId = loginAuthDto.getGroupId();
        List<MdcFormSchema> res = new ArrayList<>();
        List<MdcFormSchema> formSchemasList= mdcFormSchemaMapper.selectByDefault();
        if (formSchemasList.size()>0){
            res.addAll(formSchemasList);
        }
        if (groupId!=null){
            List<MdcFormSchema> formSchemasList1 = mdcFormSchemaMapper.selectBygroupId(groupId);
            if(formSchemasList1.size()>0){
                res.addAll(formSchemasList1);
            }
        }
        return translateJSON(res);
    }

    // 将存在数据库中的JSON字符串转换为JSON对象
    @SuppressWarnings("unchecked")
    private List<MdcFormSchemaDto> translateJSON(List<MdcFormSchema> mdcFormSchemas) {
        List<MdcFormSchemaDto> res = new ArrayList<>();
        if (mdcFormSchemas != null) {
            for (MdcFormSchema mdcFormSchema : mdcFormSchemas) {
                MdcFormSchemaDto mdcFormSchemaDto = new MdcFormSchemaDto();
                BeanUtils.copyProperties(mdcFormSchema, mdcFormSchemaDto);
                // 这里为了保证序列化之后的JSON顺序与字符串字段的存储顺序一致
                LinkedHashMap<String, Object> propsSchema = JSON.parseObject(mdcFormSchema.getPropsSchema(),LinkedHashMap.class, Feature.OrderedField);
                JSONObject propsSchemaJsonObject = new JSONObject(true);
                propsSchemaJsonObject.putAll(propsSchema);
                mdcFormSchemaDto.setPropsSchema(propsSchemaJsonObject);
                LinkedHashMap<String, Object> uiSchema = JSON.parseObject(mdcFormSchema.getUiSchema(),LinkedHashMap.class, Feature.OrderedField);
                JSONObject uiSchemaJsonObject=new JSONObject(true);
                uiSchemaJsonObject.putAll(uiSchema);
                mdcFormSchemaDto.setUiSchema(uiSchemaJsonObject);
                res.add(mdcFormSchemaDto);
            }
        }
        return res;
    }
}
