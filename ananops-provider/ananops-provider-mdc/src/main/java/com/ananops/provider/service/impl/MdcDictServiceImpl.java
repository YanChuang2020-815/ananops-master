/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：MdcDictServiceImpl.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service.impl;

import com.ananops.base.exception.BusinessException;
import com.ananops.provider.mapper.MdcSysDictItemMapper;
import com.ananops.provider.mapper.MdcSysDictMapper;
import com.ananops.provider.model.domain.MdcSysDict;
import com.ananops.provider.model.domain.MdcSysDictItem;
import com.ananops.provider.model.dto.MdcAddDictDto;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.core.support.TreeUtils;
import com.ananops.provider.exceptions.MdcBizException;
import com.ananops.provider.mapper.MdcDictMapper;
import com.ananops.provider.model.domain.MdcDict;
import com.ananops.provider.model.enums.MdcDictStatusEnum;
import com.ananops.provider.model.vo.MdcDictVo;
import com.ananops.provider.service.MdcDictService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The class Mdc dict service.
 *
 * @author ananops.com @gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MdcDictServiceImpl extends BaseService<MdcDict> implements MdcDictService {
	@Resource
	private MdcDictMapper mdcDictMapper;

	@Resource
	MdcSysDictMapper dictMapper;

	@Resource
	MdcSysDictItemMapper dictItemMapper;

	@Resource
	UacGroupBindUserFeignApi uacGroupBindUserFeignApi;

	@Override
	public MdcAddDictDto saveDict(MdcAddDictDto addDictDto, LoginAuthDto loginAuthDto) {
		MdcSysDict dict = new MdcSysDict();
		copyPropertiesWithIgnoreNullProperties(addDictDto,dict);
		dict.setUpdateInfo(loginAuthDto);
		Preconditions.checkArgument(!StringUtils.isEmpty(addDictDto.getName()), ErrorCodeEnum.MDC10021033.msg());

		if(addDictDto.getId()==null){
			logger.info("创建一条字典库记录... CrateDictInfo = {}", addDictDto);

			Long dictId = super.generateId();
			dict.setId(dictId);
			dict.setDr(String.valueOf(0));
			if (addDictDto.getGroupId() == null && loginAuthDto.getGroupId().equals(1L)) {
				dict.setGroupId(-1L);
			} else if(addDictDto.getGroupId() == null) {
				dict.setGroupId(loginAuthDto.getGroupId());
			}
			dictMapper.insert(dict);
			BeanUtils.copyProperties(dict,addDictDto);
		} else {
			logger.info("编辑/修改字典库详情... UpdateInfo = {}", addDictDto);

			Long dictId = addDictDto.getId();
			MdcSysDict t =dictMapper.selectByPrimaryKey(dictId);
			if (t == null) {
				throw new BusinessException(ErrorCodeEnum.MDC10021024,dictId);
			}

			if (!loginAuthDto.getGroupId().equals(1L) && t.getGroupId().equals(-1L)) {
				throw new BusinessException(ErrorCodeEnum.MDC10021031,dictId);
			}

			// 更新字典库信息
			dictMapper.updateByPrimaryKeySelective(dict);



			// 更新返回结果
			BeanUtils.copyProperties(dict,addDictDto);

			logger.info("编辑/修改字典库成功[OK] Dict = {}", dict);

		}


		return addDictDto;
	}

	@Override
	public List<MdcSysDict> getDictListByUserId(Long userId) {
		Long groupId=null;
		if(uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(userId).getResult()!=null){
			groupId=uacGroupBindUserFeignApi.getCompanyGroupIdByUserId(userId).getResult();
		}
		List<MdcSysDict> res = new ArrayList<>();
		List<MdcSysDict> dictList=dictMapper.selectByDefault();
		if (dictList.size()>0){
			res.addAll(dictList);
		}
		if (groupId!=null){
			List<MdcSysDict> dictList1=dictMapper.selectBygroupId(groupId);
			if(dictList1.size()>0){
				res.addAll(dictList1);
			}
		}
		return res;
	}

	@Override
	public MdcSysDict deleteDictByDictId(Long dictId,LoginAuthDto loginAuthDto) {

		MdcSysDict dict=dictMapper.selectByPrimaryKey(dictId);

		if (dict==null){
			throw new BusinessException(ErrorCodeEnum.MDC10021024,dictId);
		}
		if (!loginAuthDto.getGroupId().equals(1L) && "system".equals(dict.getDictLevel())) {
			throw new BusinessException(ErrorCodeEnum.MDC10021029,dictId);
		}
		dict.setUpdateInfo(loginAuthDto);
		dict.setDr(String.valueOf(1));
		dictMapper.updateByPrimaryKeySelective(dict);
		List<MdcSysDictItem> dictItemList=dictItemMapper.selectBygDictId(dictId, loginAuthDto.getUserId());
		if (dictItemList.size()>0){
			for (MdcSysDictItem dictItem:dictItemList){
				dictItem.setUpdateInfo(loginAuthDto);
				dictItem.setDr(String.valueOf(1));
				dictItemMapper.updateByPrimaryKeySelective(dictItem);
			}
		}
		return dict;
	}

	@Override
	public MdcSysDict getMdcDictById(Long dictId) {
		if (dictId == null){
			throw new BusinessException(ErrorCodeEnum.MDC10021024,dictId);
		}
		return dictMapper.selectByPrimaryKey(dictId);
	}

	private void copyPropertiesWithIgnoreNullProperties(Object source, Object target){
		String[] ignore = getNullPropertyNames(source);
		BeanUtils.copyProperties(source, target, ignore);
	}

	private String[] getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<MdcDictVo> getDictTreeList() {
		List<MdcDictVo> list = mdcDictMapper.listDictVo();
		return new TreeUtils().getChildTreeObjects(list, 0L);
	}

//	@Override
//	@Transactional(readOnly = true, rollbackFor = Exception.class)
//	public MdcDictVo getMdcDictVoById(Long dictId) {
//		MdcDict dict = mdcDictMapper.selectByPrimaryKey(dictId);
//
//		if (dict == null) {
//			logger.error("找不到数据字典信息id={}", dictId);
//			throw new MdcBizException(ErrorCodeEnum.MDC10021018, dictId);
//		}
//
//		// 获取父级菜单信息
//		MdcDict parentDict = mdcDictMapper.selectByPrimaryKey(dict.getPid());
//
//		ModelMapper modelMapper = new ModelMapper();
//		MdcDictVo dictVo = modelMapper.map(dict, MdcDictVo.class);
//
//		if (parentDict != null) {
//			dictVo.setParentDictName(parentDict.getDictName());
//		}
//
//		return dictVo;
//	}
//
//	@Override
//	public void updateMdcDictStatusById(UpdateStatusDto updateStatusDto, LoginAuthDto loginAuthDto) {
//		Long id = updateStatusDto.getId();
//		Integer status = updateStatusDto.getStatus();
//		// 要处理的菜单集合
//		List<MdcDict> mdcDictList = Lists.newArrayList();
//
//		int result;
//		if (status.equals(MdcDictStatusEnum.DISABLE.getType())) {
//			// 获取菜单以及子菜单
//			mdcDictList = this.getAllDictFolder(id, MdcDictStatusEnum.ENABLE.getType());
//		} else {
//			// 获取菜单、其子菜单以及父菜单
//			MdcDict uacMenu = new MdcDict();
//			uacMenu.setPid(id);
//			result = this.selectCount(uacMenu);
//			// 此菜单含有子菜单
//			if (result > 0) {
//				mdcDictList = this.getAllDictFolder(id, MdcDictStatusEnum.DISABLE.getType());
//			}
//			List<MdcDict> dictListTemp = this.getAllParentDictFolderByMenuId(id);
//			for (MdcDict dict : dictListTemp) {
//				if (!mdcDictList.contains(dict)) {
//					mdcDictList.add(dict);
//				}
//			}
//		}
//
//		this.updateDictStatus(mdcDictList, loginAuthDto, status);
//	}

	private void updateDictStatus(List<MdcDict> mdcDictList, LoginAuthDto loginAuthDto, int status) {
		MdcDict update = new MdcDict();
		for (MdcDict dict : mdcDictList) {
			update.setId(dict.getId());
			update.setVersion(dict.getVersion() + 1);
			update.setStatus(status);
			update.setUpdateInfo(loginAuthDto);
			int result = mapper.updateByPrimaryKeySelective(update);
			if (result < 1) {
				throw new MdcBizException(ErrorCodeEnum.MDC10021019, dict.getId());
			}
		}
	}

	private List<MdcDict> getAllDictFolder(Long id, int dictStatus) {
		MdcDict mdcDict = new MdcDict();
		mdcDict.setId(id);
		mdcDict = mapper.selectOne(mdcDict);
		List<MdcDict> mdcDictList = Lists.newArrayList();
		mdcDictList = buildNode(mdcDictList, mdcDict, dictStatus);
		return mdcDictList;
	}

	private List<MdcDict> getAllParentDictFolderByMenuId(Long dictId) {
		MdcDict mdcDictQuery = new MdcDict();
		mdcDictQuery.setId(dictId);
		mdcDictQuery = mapper.selectOne(mdcDictQuery);
		List<MdcDict> mdcDictList = Lists.newArrayList();
		mdcDictList = buildParentNote(mdcDictList, mdcDictQuery);
		return mdcDictList;
	}

	/**
	 * 递归获取菜单的子节点
	 */
	private List<MdcDict> buildNode(List<MdcDict> mdcDictList, MdcDict uacMenu, int dictStatus) {
		List<MdcDict> uacMenuQueryList = mapper.select(uacMenu);
		MdcDict uacMenuQuery;
		for (MdcDict dict : uacMenuQueryList) {
			if (dictStatus == dict.getStatus()) {
				mdcDictList.add(dict);
			}
			uacMenuQuery = new MdcDict();
			uacMenuQuery.setPid(dict.getId());
			buildNode(mdcDictList, uacMenuQuery, dictStatus);
		}
		return mdcDictList;
	}

	/**
	 * 递归获取菜单的父菜单
	 */
	private List<MdcDict> buildParentNote(List<MdcDict> mdcDictList, MdcDict mdcDict) {
		List<MdcDict> mdcDictQueryList = mapper.select(mdcDict);
		MdcDict uacMenuQuery;
		for (MdcDict dict : mdcDictQueryList) {
			if (MdcDictStatusEnum.DISABLE.getType() == dict.getStatus()) {
				mdcDictList.add(dict);
			}
			uacMenuQuery = new MdcDict();
			uacMenuQuery.setId(dict.getPid());
			buildParentNote(mdcDictList, uacMenuQuery);
		}
		return mdcDictList;
	}

//	@Override
//	public void saveMdcDict(MdcDict mdcDict, LoginAuthDto loginAuthDto) {
//		Long pid = mdcDict.getPid();
//		mdcDict.setUpdateInfo(loginAuthDto);
//		MdcDict parentMenu = mapper.selectByPrimaryKey(pid);
//		if (PublicUtil.isEmpty(parentMenu)) {
//			throw new MdcBizException(ErrorCodeEnum.MDC10021020, pid);
//		}
//		if (mdcDict.isNew()) {
//			MdcDict updateMenu = new MdcDict();
//			updateMenu.setId(pid);
//			Long dictId = super.generateId();
//			mdcDict.setId(dictId);
//			mapper.insertSelective(mdcDict);
//		} else {
//			mapper.updateByPrimaryKeySelective(mdcDict);
//		}
//
//	}
//
//	@Override
//	@Transactional(readOnly = true, rollbackFor = Exception.class)
//	public boolean checkDictHasChildDict(Long dictId) {
//		logger.info("检查数据字典id={}是否存在生效节点", dictId);
//		MdcDict uacMenu = new MdcDict();
//		uacMenu.setStatus(MdcDictStatusEnum.ENABLE.getType());
//		uacMenu.setPid(dictId);
//
//		return mapper.selectCount(uacMenu) > 0;
//	}

}