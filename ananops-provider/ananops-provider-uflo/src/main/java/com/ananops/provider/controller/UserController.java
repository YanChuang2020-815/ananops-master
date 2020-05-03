package com.ananops.provider.controller;

import com.alibaba.fastjson.JSONObject;
import com.ananops.provider.pojo.User;
import com.ananops.provider.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
@Api(value = "Web-UserController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	@ApiOperation(httpMethod = "GET", value = "测试接口")
	public String helloWorld() {
		return "hello World";
	}
	/**
	 * 测试地址  http://localhost:8080/getUser?name=zhao
	 * @param name
	 * @return
	 */
	@RequestMapping("/getUser")
	@ApiOperation(httpMethod = "GET", value = "测试接口")
	@ResponseBody
	public String user(String name) {
		User user = userRepository.findUser(name);
		return JSONObject.toJSONString(user);
	}

}
