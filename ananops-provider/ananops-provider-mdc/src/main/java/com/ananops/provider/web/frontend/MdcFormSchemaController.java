package com.ananops.provider.web.frontend;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.MdcFormSchema;
import com.ananops.provider.model.dto.MdcFormSchemaDto;
import com.ananops.provider.service.MdcFormSchemaService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/20 上午9:45
 */
@RestController
@RequestMapping(value = "/formSchema", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - MdcFormSchemaController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdcFormSchemaController extends BaseController {

    @Resource
    private MdcFormSchemaService mdcFormSchemaService;

    /**
     * 获取巡检表单的表结构
     *
     * @return 返回
     */
    @GetMapping(value = "/getInspcFormSchema")
    @ApiOperation(httpMethod = "GET",value = "获取巡检表单的表结构")
    public Wrapper<List<MdcFormSchemaDto>> getInspcFormSchema() {
        logger.info("获取巡检表单的表结构");
        LoginAuthDto loginAuthDto = super.getLoginAuthDto();
        return WrapMapper.ok(mdcFormSchemaService.getInspcFormSchema(loginAuthDto));
    }
}
