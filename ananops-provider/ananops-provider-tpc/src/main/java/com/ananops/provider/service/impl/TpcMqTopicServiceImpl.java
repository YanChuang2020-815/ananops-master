/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：TpcMqTopicServiceImpl.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service.impl;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exceptions.TpcBizException;
import com.ananops.provider.mapper.TpcMqTagMapper;
import com.ananops.provider.mapper.TpcMqTopicMapper;
import com.ananops.provider.model.domain.TpcMqTag;
import com.ananops.provider.model.domain.TpcMqTopic;
import com.ananops.provider.model.dto.AddTopicDto;
import com.ananops.provider.model.dto.TopicBindTagDto;
import com.ananops.provider.model.vo.TpcMqTopicVo;
import com.ananops.provider.service.TpcMqTagService;
import com.ananops.provider.service.TpcMqTopicService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The class Tpc mq topic service.
 *
 * @author ananops.com @gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TpcMqTopicServiceImpl extends BaseService<TpcMqTopic> implements TpcMqTopicService {
	@Resource
	private TpcMqTopicMapper mdcMqTopicMapper;

	@Resource
	private TpcMqTagMapper tpcMqTagMapper;

	/**
	 * 新增主题
	 * @param addTopicDto
	 * @param loginAuthDto
	 * @return
	 */
	@Override
	public TpcMqTopic addMqTopic(AddTopicDto addTopicDto, LoginAuthDto loginAuthDto){
		TpcMqTopic tpcMqTopic = new TpcMqTopic();
		try {
			BeanUtils.copyProperties(tpcMqTopic,addTopicDto);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		tpcMqTopic.setUpdateInfo(loginAuthDto);
		int result = mdcMqTopicMapper.insert(tpcMqTopic);
		if(result==1){
			return tpcMqTopic;
		}else{
			throw new TpcBizException(ErrorCodeEnum.TPC100500018);
		}
	}

	/**
	 * 为主题绑定标签
	 * @param topicBindTagDto
	 * @param loginAuthDto
	 * @return
	 */
	@Override
	public TpcMqTag topicBindTag(TopicBindTagDto topicBindTagDto, LoginAuthDto loginAuthDto){
		TpcMqTag tpcMqTag = new TpcMqTag();
		try {
			BeanUtils.copyProperties(tpcMqTag,topicBindTagDto);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		tpcMqTag.setUpdateInfo(loginAuthDto);
		int result = tpcMqTagMapper.insert(tpcMqTag);
		if(result==1){
			return tpcMqTag;
		}else{
			throw new TpcBizException(ErrorCodeEnum.TPC100500019);
		}
	}

	@Override
	public List<TpcMqTopicVo> listWithPage(TpcMqTopic mdcMqTopic) {
		return mdcMqTopicMapper.listTpcMqTopicVoWithPage(mdcMqTopic);
	}
}
