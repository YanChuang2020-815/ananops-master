package com.ananops.provider.config;

import com.ananops.core.config.PcObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

/**
 * Created by rongshuai on 2020/2/18 22:43
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration){
		registration.setInterceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					final String userId = accessor.getFirstNativeHeader("userId");
//					LoginAuthDto loginUser = (UserTokenDto) redisTemplate.opsForValue().get(RedisKeyUtil.getAccessTokenKey(jwtToken));
					if(userId!=null){//将userId和socket连接进行绑定
						System.out.println(userId);
//						String userId = String.valueOf(loginUser.getUserId());
						if(StringUtils.isNotEmpty(userId)){
							System.out.println(userId);
							Principal principal = () -> userId;
							accessor.setUser(principal);
						}
					}
				}
				return message;
			}
		});
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters){
		try{
			PcObjectMapper.buidMessageConverter(messageConverters);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				// bypasses spring web security
				.setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// prefix for subscribe
		config.enableSimpleBroker("/topic","/queue");
		// prefix for send
		config.setApplicationDestinationPrefixes("/app");
	}
}
