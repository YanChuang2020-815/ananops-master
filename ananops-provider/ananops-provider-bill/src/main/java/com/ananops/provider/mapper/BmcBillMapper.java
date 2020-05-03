package com.ananops.provider.mapper;

import com.ananops.provider.model.domain.BmcBill;
import com.ananops.core.mybatis.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface BmcBillMapper extends MyMapper<BmcBill> {
}