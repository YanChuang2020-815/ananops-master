package com.ananops.provider.mapper;

import com.ananops.core.mybatis.MyMapper;
import com.ananops.provider.model.domain.WebsocketUserMessageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WebsocketUserMessageInfoMapper extends MyMapper<WebsocketUserMessageInfo> {
    List<WebsocketUserMessageInfo> getMsgInfo(@Param(value = "userId")Long userId,
                                              @Param(value = "status")Integer status,
                                              @Param(value = "messageTopic")String messageTopic,
                                              @Param(value = "messageTag")String messageTag);
}