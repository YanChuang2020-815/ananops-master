/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：UacUserTokenMapper.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.UacUserToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The interface Uac user token mapper.
 *
 * @author ananops.com @gmail.com
 */
@Mapper
@Component
public interface UacUserTokenMapper extends MyMapper<UacUserToken> {
	/**
	 * Select token list list.
	 *
	 * @param userToken the user token
	 *
	 * @return the list
	 */
	List<UacUserToken> selectTokenList(UacUserToken userToken);

	/**
	 * 超时token更新为离线.
	 *
	 * @param map the map
	 *
	 * @return the int
	 */
	int batchUpdateTokenOffLine(Map<String, Object> map);

	/**
	 * 查询超时token Id 集合.
	 *
	 * @return the list
	 */
	List<Long> listOffLineTokenId();

	/**
	 * 查找给定的userId集合中当前状态下的Token
	 *
	 * @param userToken 用户Token
	 *
	 * @param userIds 用户Id集合
	 *
	 * @return 用户Token集合
	 */
	List<UacUserToken> selectTokenListInUserIds(@Param("userToken") UacUserToken userToken, @Param("userIds") List<Long> userIds);
}