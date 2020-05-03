package com.ananops.provider.service.impl;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.MdcSysDictItemMapper;
import com.ananops.provider.mapper.MdcSysDictMapper;
import com.ananops.provider.model.constant.SysDictConstant;
import com.ananops.provider.model.domain.MdcSysDict;
import com.ananops.provider.model.domain.MdcSysDictItem;
import com.ananops.provider.model.dto.DictItemDto;
import com.ananops.provider.model.dto.MdcAddDictItemDto;
import com.ananops.provider.model.dto.SysDictItemsDto;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.ananops.provider.service.MdcDictItemService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huqiaoqian on 2020/3/27
 */
@Slf4j
@Service
public class MdcDictItemServiceImpl extends BaseService<MdcSysDictItem> implements MdcDictItemService {
    @Resource
    MdcSysDictItemMapper dictItemMapper;

    @Resource
    MdcSysDictMapper dictMapper;

    @Resource
    UacGroupBindUserFeignApi uacGroupBindUserFeignApi;

    @Override
    public MdcAddDictItemDto saveItem(MdcAddDictItemDto addDictItemDto, LoginAuthDto loginAuthDto) {
        MdcSysDictItem dictItem=new MdcSysDictItem();
        // 校验填充的数据字段
        validateDictItem(addDictItemDto);
        BeanUtils.copyProperties(addDictItemDto,dictItem);
        dictItem.setUpdateInfo(loginAuthDto);
        Long dictId = dictItem.getDictId();
        if (dictId==null){
            throw new BusinessException(ErrorCodeEnum.MDC10021026);
        }
        Example example = new Example(MdcSysDict.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",dictId);
        List<MdcSysDict> dictList =dictMapper.selectByExample(example);
        if(dictList.size()==0){//如果没有此字典库
            throw new BusinessException(ErrorCodeEnum.MDC10021024,dictId);
        }
        if(addDictItemDto.getId()==null){//如果是新增一条字典项记录
            Long itemId = super.generateId();
            dictItem.setId(itemId);
            dictItem.setDr(String.valueOf(0));
            if (addDictItemDto.getGroupId() == null && loginAuthDto.getGroupId().equals(1L)) {
                dictItem.setGroupId(-1L);
            } else if(addDictItemDto.getGroupId() == null) {
                dictItem.setGroupId(loginAuthDto.getGroupId());
            }
            dictItemMapper.insert(dictItem);
            BeanUtils.copyProperties(dictItem,addDictItemDto);
        }else{//如果是更新已经存在的字典项
            Long itemId=addDictItemDto.getId();
            Example example1 = new Example(MdcSysDictItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("id",itemId);
            List<MdcSysDictItem> dictItemList =dictItemMapper.selectByExample(example1);
            if(dictItemList.size()==0){//如果没有此字典项
                throw new BusinessException(ErrorCodeEnum.MDC10021025,itemId);
            }

            if (!loginAuthDto.getGroupId().equals(1L) && dictItemList.get(0).getGroupId().equals(-1L)) {
                throw new BusinessException(ErrorCodeEnum.MDC10021032,dictId);
            }
            dictItemMapper.updateByPrimaryKeySelective(dictItem);
            BeanUtils.copyProperties(dictItem,addDictItemDto);
        }
        return addDictItemDto;
    }

    private void validateDictItem(MdcAddDictItemDto addDictItemDto) {
        Preconditions.checkArgument(!StringUtils.isEmpty(addDictItemDto.getName()), ErrorCodeEnum.MDC10021034.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(addDictItemDto.getCode()), ErrorCodeEnum.MDC10021035.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(addDictItemDto.getSort()), ErrorCodeEnum.MDC10021036.msg());
    }

    @Override
    public List<MdcSysDictItem> getDictItemListByDictId(Long dictId, Long userId) {

        List<MdcSysDictItem> res = new ArrayList<>();

        if (dictId==null){
            throw new BusinessException(ErrorCodeEnum.MDC10021026);
        }

        // 该用户所属的公司ID
        Long groupId = uacGroupBindUserFeignApi.getGroupIdByUserId(userId).getResult();
        // 查询默认的
        List<MdcSysDictItem> sysDictItems = dictItemMapper.selectBygDictId(dictId, -1L);
        if (sysDictItems != null) {
            res.addAll(sysDictItems);
        }
        // 只有当用户的公司组织Id不为空时，查询用户自有的字典项
        if (groupId != null) {
            List<MdcSysDictItem> userDictItems = dictItemMapper.selectBygDictId(dictId, groupId);
            res.addAll(userDictItems);
        }
        return res;
    }

    @Override
    public MdcSysDictItem deleteDictItemByItemId(Long itemId,LoginAuthDto loginAuthDto) {
        if(itemId==null){
            throw new BusinessException(ErrorCodeEnum.MDC10021027);
        }

        MdcSysDictItem dictItem=dictItemMapper.selectByPrimaryKey(itemId);
        if (dictItem==null){
            throw new BusinessException(ErrorCodeEnum.MDC10021025,itemId);
        }
        if (!loginAuthDto.getGroupId().equals(1L) && dictItem.getGroupId().equals(-1L)) {
            throw new BusinessException(ErrorCodeEnum.MDC10021030,itemId);
        }
        dictItem.setUpdateInfo(loginAuthDto);
        dictItem.setDr(String.valueOf(1));
        dictItemMapper.updateByPrimaryKeySelective(dictItem);
        return dictItem;
    }

    @Override
    public SysDictItemsDto getSysDictItems(Long userId) {

        SysDictItemsDto sysDictItemsDto = new SysDictItemsDto();
        // 该用户所属的公司ID
        Long groupId = uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(userId).getResult();

        // 装入故障类型数据子项
        Long troubleTypeDictId = SysDictConstant.SysDictEnum.TROUBLE_TYPE.getId();
        List<DictItemDto> troubleTypeDictItems = getDictItemList(troubleTypeDictId, groupId);
        if (!troubleTypeDictItems.isEmpty()) {
            sysDictItemsDto.setTroubleTypeList(troubleTypeDictItems);
        }
        // 装入故障位置数据子项
        Long troubleAddressDictId = SysDictConstant.SysDictEnum.TROUBLE_ADDRESS.getId();
        List<DictItemDto> troubleAddressDictItems = getDictItemList(troubleAddressDictId, groupId);
        if (!troubleAddressDictItems.isEmpty()) {
            sysDictItemsDto.setTroubleAddressList(troubleAddressDictItems);
        }
        // 装入设备类型数据子项
        Long deviceTypeDictId = SysDictConstant.SysDictEnum.DEVICE_TYPE.getId();
        List<DictItemDto> deviceTypeDictItems = getDictItemList(deviceTypeDictId, groupId);
        if (!deviceTypeDictItems.isEmpty()) {
            sysDictItemsDto.setDeviceTypeList(deviceTypeDictItems);
        }
        // 装入故障等级数据子项
        Long troubleLevelDictId = SysDictConstant.SysDictEnum.TROUBLE_LEVEL.getId();
        List<DictItemDto> troubleLevelDictItems = getDictItemList(troubleLevelDictId, groupId);
        if (!troubleLevelDictItems.isEmpty()) {
            sysDictItemsDto.setTroubleLevelList(troubleLevelDictItems);
        }
        // 装入紧急程度数据子项
        Long emergencyLevelDictId = SysDictConstant.SysDictEnum.EMERGENCY_LEVEL.getId();
        List<DictItemDto> emergencyLevelDictItems = getDictItemList(emergencyLevelDictId, groupId);
        if (!emergencyLevelDictItems.isEmpty()) {
            sysDictItemsDto.setEmergencyLevelList(emergencyLevelDictItems);
        }

        return sysDictItemsDto;
    }

    // 装入数据字典项
    private List<DictItemDto> getDictItemList(Long dictId, Long groupId) {
        List<MdcSysDictItem> troubleTypeDictItems = getDictItemListByDictIdAndGroupId(dictId, groupId);
        List<DictItemDto> dictItemDtos = new ArrayList<>();
        if (troubleTypeDictItems != null) {
            for (MdcSysDictItem mdcSysDictItem : troubleTypeDictItems) {
                DictItemDto dictItemDto = new DictItemDto();
                try {
                    BeanUtils.copyProperties(mdcSysDictItem, dictItemDto);
                } catch (Exception e) {
                    logger.error("字典项Dto与字典项属性拷贝异常");
                    e.printStackTrace();
                }
                dictItemDtos.add(dictItemDto);
            }
        }
        return dictItemDtos;
    }

    private List<MdcSysDictItem> getDictItemListByDictIdAndGroupId(Long dictId, Long groupId) {

        List<MdcSysDictItem> res = new ArrayList<>();

        if (dictId==null){
            throw new BusinessException(ErrorCodeEnum.MDC10021026);
        }

        // 查询默认的
        List<MdcSysDictItem> sysDictItems = dictItemMapper.selectBygDictId(dictId, -1L);
        if (sysDictItems != null) {
            res.addAll(sysDictItems);
        }
        // 只有当用户的公司组织Id不为空时，查询用户自有的字典项
        if (groupId != null) {
            List<MdcSysDictItem> userDictItems = dictItemMapper.selectBygDictId(dictId, groupId);
            res.addAll(userDictItems);
        }
        return res;
    }
}
