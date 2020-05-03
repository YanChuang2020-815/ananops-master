/*
 * Copyright (c) 2019. ananops.net All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：RedisValidateCodeRepository.java
 * 创建人：ananops
 * 联系方式：ananops.net@gmail.com


 *  * 平台官网: http://ananops.com
 */

package com.ananops.security.core.code.impl;

import com.ananops.security.core.code.ValidateCode;
import com.ananops.security.core.code.ValidateCodeException;
import com.ananops.security.core.code.ValidateCodeRepository;
import com.ananops.security.core.code.ValidateCodeType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的验证码存取器，避免由于没有session导致无法存取验证码的问题
 *
 * @author ananops.net @gmail.com
 */
@Component
public class RedisValidateCodeRepository implements ValidateCodeRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * Instantiates a new Redis validate code repository.
	 *
	 * @param redisTemplate the redis template
	 */
	@Autowired
	public RedisValidateCodeRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * Save.
	 *
	 * @param request the request
	 * @param code    the code
	 * @param type    the type
	 */
	@Override
	public void save(ServletWebRequest request, ValidateCode code, ValidateCodeType type) {
		String key = buildKey(request, type);
		redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
	}

	/**
	 * Get validate code.
	 *
	 * @param request the request
	 * @param type    the type
	 *
	 * @return the validate code
	 */
	@Override
	public ValidateCode get(ServletWebRequest request, ValidateCodeType type) {
		Object value = redisTemplate.opsForValue().get(buildKey(request, type));
		if (value == null) {
			return null;
		}
		return (ValidateCode) value;
	}

	/**
	 * Remove.
	 *
	 * @param request the request
	 * @param type    the type
	 */
	@Override
	public void remove(ServletWebRequest request, ValidateCodeType type) {
		redisTemplate.delete(buildKey(request, type));
	}

	private String buildKey(ServletWebRequest request, ValidateCodeType type) {
		String deviceId = request.getHeader("deviceId");
		if (StringUtils.isBlank(deviceId)) {
			throw new ValidateCodeException("请在请求头中携带deviceId参数");
		}
		return "code:" + type.toString().toLowerCase() + ":" + deviceId;
	}

}
