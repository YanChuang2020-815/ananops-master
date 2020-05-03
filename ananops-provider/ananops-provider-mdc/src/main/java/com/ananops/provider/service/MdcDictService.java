/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：MdcDictService.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.dto.UpdateStatusDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.MdcDict;
import com.ananops.provider.model.domain.MdcSysDict;
import com.ananops.provider.model.dto.MdcAddDictDto;
import com.ananops.provider.model.dto.MdcGetDictDto;
import com.ananops.provider.model.vo.MdcDictVo;

import java.util.List;

/**
 * The interface Mdc dict service.
 *
 * @author ananops.com @gmail.com
 */
public interface MdcDictService extends IService<MdcDict> {

	/**
	 * 获取字典树.
	 *
	 * @return the dict tree list
	 */
	List<MdcDictVo> getDictTreeList();

	/**
	 * 创建/编辑字典库
	 * @param addDictDto
	 * @param loginAuthDto
	 * @return
	 */
	MdcAddDictDto saveDict(MdcAddDictDto addDictDto, LoginAuthDto loginAuthDto);

	/**
	 * 根据用户id获取字典库列表
	 * @param userId
	 * @return
	 */
	List<MdcSysDict> getDictListByUserId(Long userId);

	/**
	 * 根据字典库id删除字典库及其所属字典项
	 * @param dictId
	 * @return
	 */
	MdcSysDict deleteDictByDictId(Long dictId, LoginAuthDto loginAuthDto);

	/**
	 * 根据字典库Id查询字典库信息
	 *
	 * @param id
	 *
	 * @return
	 */
	MdcSysDict getMdcDictById(Long dictId);

//	/**
//	 * 根据ID获取字典信息.
//	 *
//	 * @param dictId the dict id
//	 *
//	 * @return the mdc dict vo by id
//	 */
//	MdcDictVo getMdcDictVoById(Long dictId);
//
//	/**
//	 * 根据id修改字典信息.
//	 *
//	 * @param updateStatusDto the update status dto
//	 * @param loginAuthDto    the login auth dto
//	 *
//	 * @return the int
//	 */
//	void updateMdcDictStatusById(UpdateStatusDto updateStatusDto, LoginAuthDto loginAuthDto);
//
//	/**
//	 * 编辑字典.
//	 *
//	 * @param mdcDict      the mdc dict
//	 * @param loginAuthDto the login auth dto
//	 */
//	void saveMdcDict(MdcDict mdcDict, LoginAuthDto loginAuthDto);
//
//	/**
//	 * Check dict has child dict boolean.
//	 *
//	 * @param dictId the dict id
//	 *
//	 * @return the boolean
//	 */
//	boolean checkDictHasChildDict(Long dictId);
}
