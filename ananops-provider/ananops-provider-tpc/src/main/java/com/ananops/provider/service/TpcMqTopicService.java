/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：TpcMqTopicService.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.TpcMqTag;
import com.ananops.provider.model.domain.TpcMqTopic;
import com.ananops.provider.model.dto.AddTopicDto;
import com.ananops.provider.model.dto.TopicBindTagDto;
import com.ananops.provider.model.vo.TpcMqTopicVo;

import java.util.List;

/**
 * The interface Tpc mq topic service.
 *
 * @author ananops.com @gmail.com
 */
public interface TpcMqTopicService extends IService<TpcMqTopic> {
	/**
	 * 添加主题
	 * @param addTopicDto
	 * @param loginAuthDto
	 * @return
	 */
	TpcMqTopic addMqTopic(AddTopicDto addTopicDto, LoginAuthDto loginAuthDto);

	/**
	 * 为主题绑定标签
	 * @param topicBindTagDto
	 * @param loginAuthDto
	 * @return
	 */
	TpcMqTag topicBindTag(TopicBindTagDto topicBindTagDto, LoginAuthDto loginAuthDto);
	/**
	 * 查询MQ topic列表.
	 *
	 * @param mdcMqTopic the mdc mq topic
	 *
	 * @return the list
	 */
	List<TpcMqTopicVo> listWithPage(TpcMqTopic mdcMqTopic);

}
