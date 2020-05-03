/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：TpcMqProducerServiceImpl.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.service.impl;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exceptions.TpcBizException;
import com.ananops.provider.mapper.TpcMqProducerMapper;
import com.ananops.provider.model.domain.TpcMqProducer;
import com.ananops.provider.model.dto.AddMqProducerDto;
import com.ananops.provider.model.vo.TpcMqProducerVo;
import com.ananops.provider.model.vo.TpcMqPublishVo;
import com.ananops.provider.service.TpcMqProducerService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The class Tpc mq producer service.
 *
 * @author ananops.com @gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TpcMqProducerServiceImpl extends BaseService<TpcMqProducer> implements TpcMqProducerService {

	@Resource
	private TpcMqProducerMapper mdcMqProducerMapper;

	@Override
	public TpcMqProducer addProducer(AddMqProducerDto addMqProducerDto, LoginAuthDto loginAuthDto){
		TpcMqProducer tpcMqProducer = new TpcMqProducer();
		try {
			BeanUtils.copyProperties(tpcMqProducer,addMqProducerDto);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		tpcMqProducer.setUpdateInfo(loginAuthDto);
		int result = mdcMqProducerMapper.insert(tpcMqProducer);
		if(result==1){
			return tpcMqProducer;
		}else{
			throw new TpcBizException(ErrorCodeEnum.TPC100500017);
		}
	}

	@Override
	public List<TpcMqProducerVo> listProducerVoWithPage(TpcMqProducer mdcMqProducer) {
		return mdcMqProducerMapper.listTpcMqProducerVoWithPage(mdcMqProducer);
	}

	@Override
	public List<TpcMqPublishVo> listPublishVoWithPage(TpcMqProducer mdcMqProducer) {
		return mdcMqProducerMapper.listTpcMqPublishVoWithPage(mdcMqProducer);
	}

	@Override
	public int deleteProducerById(Long producerId) {
		// 删除consumer
		mdcMqProducerMapper.deleteByPrimaryKey(producerId);
		// 删除发布关系
		return mdcMqProducerMapper.deletePublishByProducerId(producerId);
	}

	@Override
	public void updateOnLineStatusByPid(final String producerGroup) {
		logger.info("更新生产者pid={}状态为在线", producerGroup);
		this.updateStatus(producerGroup, 10);

	}

	@Override
	public void updateOffLineStatusByPid(final String producerGroup) {
		logger.info("更新生产者pid={}状态为离线", producerGroup);
		this.updateStatus(producerGroup, 20);
	}

	private void updateStatus(final String producerGroup, final int status) {
		TpcMqProducer tpcMqProducer = mdcMqProducerMapper.getByPid(producerGroup);
		if (tpcMqProducer.getStatus() != null && tpcMqProducer.getStatus() != status) {
			TpcMqProducer update = new TpcMqProducer();
			update.setStatus(status);
			update.setId(tpcMqProducer.getId());
			mdcMqProducerMapper.updateByPrimaryKeySelective(update);
		}
	}
}
