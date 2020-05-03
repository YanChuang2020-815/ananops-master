/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：TpcMqConsumerServiceImpl.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exceptions.TpcBizException;
import com.ananops.provider.mapper.TpcMqConsumerMapper;
import com.ananops.provider.mapper.TpcMqSubscribeMapper;
import com.ananops.provider.mapper.TpcMqTopicMapper;
import com.ananops.provider.model.domain.TpcMqConsumer;
import com.ananops.provider.model.domain.TpcMqSubscribe;
import com.ananops.provider.model.domain.TpcMqTopic;
import com.ananops.provider.model.dto.AddMqConsumerDto;
import com.ananops.provider.model.dto.ConsumerSubscribeTopicDto;
import com.ananops.provider.model.vo.TpcMqConsumerVo;
import com.ananops.provider.model.vo.TpcMqSubscribeVo;
import com.ananops.provider.service.TpcMqConsumerService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The class Tpc mq consumer service.
 *
 * @author ananops.com @gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TpcMqConsumerServiceImpl extends BaseService<TpcMqConsumer> implements TpcMqConsumerService {
	@Resource
	private TpcMqConsumerMapper tpcMqConsumerMapper;

	@Resource
	private TpcMqTopicMapper tpcMqTopicMapper;

	@Resource
	private TpcMqSubscribeMapper tpcMqSubscribeMapper;

	@Override
	public TpcMqSubscribe consumerSubcribeTopic(ConsumerSubscribeTopicDto consumerSubscribeTopicDto){
		String consumerCode = consumerSubscribeTopicDto.getConsumerCode();
		String topicCode = consumerSubscribeTopicDto.getTopicCode();
		Long consumerId = null;
		Long topicId = null;
		if(consumerCode==null||topicCode==null){
			throw new BusinessException(ErrorCodeEnum.GL99990100);
		}
		Example example = new Example(TpcMqConsumer.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("consumerCode",consumerCode);
		List<TpcMqConsumer> tpcMqConsumerList = tpcMqConsumerMapper.selectByExample(example);
		if(tpcMqConsumerList.size()==0){
			throw new TpcBizException(ErrorCodeEnum.TPC100500020);
		}
		consumerId = tpcMqConsumerList.get(0).getId();
		Example example2 = new Example(TpcMqTopic.class);
		Example.Criteria criteria2 = example2.createCriteria();
		criteria2.andEqualTo("topicCode",topicCode);
		List<TpcMqTopic> tpcMqTopicList = tpcMqTopicMapper.selectByExample(example2);
		if(tpcMqTopicList.size()==0){
			throw new TpcBizException(ErrorCodeEnum.TPC100500021);
		}
		topicId = tpcMqTopicList.get(0).getId();
		Long subscribeId = super.generateId();
		TpcMqSubscribe tpcMqSubscribe = new TpcMqSubscribe();
		tpcMqSubscribe.setId(subscribeId);
		tpcMqSubscribe.setConsumerCode(consumerCode);
		tpcMqSubscribe.setConsumerId(consumerId);
		tpcMqSubscribe.setTopicCode(topicCode);
		tpcMqSubscribe.setTopicId(topicId);
		tpcMqSubscribeMapper.insert(tpcMqSubscribe);
		return tpcMqSubscribe;
	}

	@Override
	public TpcMqConsumer addConsumer(AddMqConsumerDto addMqConsumerDto, LoginAuthDto loginAuthDto){
		TpcMqConsumer tpcMqConsumer = new TpcMqConsumer();
		try {
			BeanUtils.copyProperties(tpcMqConsumer,addMqConsumerDto);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		tpcMqConsumer.setUpdateInfo(loginAuthDto);
		int result = tpcMqConsumerMapper.insert(tpcMqConsumer);
		if(result==1){
			return tpcMqConsumer;
		}else{
			throw new TpcBizException(ErrorCodeEnum.TPC100500016);
		}
	}

	@Override
	public List<TpcMqConsumerVo> listConsumerVoWithPage(TpcMqConsumer tpcMqConsumer) {
		return tpcMqConsumerMapper.listTpcMqConsumerVoWithPage(tpcMqConsumer);
	}

	@Override
	public List<TpcMqSubscribeVo> listSubscribeVoWithPage(TpcMqConsumer tpcMqConsumer) {
		return tpcMqConsumerMapper.listTpcMqSubscribeVoWithPage(tpcMqConsumer);
	}

	@Override
	public int deleteSubscribeTagByTagId(Long tagId) {
		return tpcMqConsumerMapper.deleteSubscribeTagByTagId(tagId);
	}

	@Override
	public int deleteConsumerById(Long consumerId) {
		// 删除消费者
		tpcMqConsumerMapper.deleteByPrimaryKey(consumerId);
		// 删除订阅关系
		List<Long> subscribeIdList = tpcMqConsumerMapper.listSubscribeIdByConsumerId(consumerId);
		if (PublicUtil.isNotEmpty(subscribeIdList)) {
			tpcMqConsumerMapper.deleteSubscribeByConsumerId(consumerId);
			// 删除订阅tag
			tpcMqConsumerMapper.deleteSubscribeTagBySubscribeIdList(subscribeIdList);
		}
		return 1;
	}

	@Override
	public List<TpcMqSubscribeVo> listSubscribeVo(List<Long> subscribeIdList) {
		return tpcMqConsumerMapper.listSubscribeVo(subscribeIdList);
	}

	@Override
	public List<String> listConsumerGroupByTopic(final String topic) {
		return tpcMqConsumerMapper.listConsumerGroupByTopic(topic);
	}

	@Override
	public void updateOnLineStatusByCid(final String consumerGroup) {
		logger.info("更新消费者cid={}状态为在线", consumerGroup);
		this.updateStatus(consumerGroup, 10);

	}

	@Override
	public void updateOffLineStatusByCid(final String consumerGroup) {
		logger.info("更新消费者cid={}状态为离线", consumerGroup);
		this.updateStatus(consumerGroup, 20);
	}

	private void updateStatus(final String consumerGroup, final int status) {
		TpcMqConsumer tpcMqConsumer = tpcMqConsumerMapper.getByCid(consumerGroup);
		if (tpcMqConsumer.getStatus() != null && tpcMqConsumer.getStatus() != status) {
			TpcMqConsumer update = new TpcMqConsumer();
			update.setStatus(status);
			update.setId(tpcMqConsumer.getId());
			tpcMqConsumerMapper.updateByPrimaryKeySelective(update);
		}
	}
}
