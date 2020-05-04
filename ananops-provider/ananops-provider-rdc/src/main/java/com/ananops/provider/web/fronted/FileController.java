package com.ananops.provider.web.fronted;

import com.ananops.core.support.BaseController;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.service.FileService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by rongshuai on 2020/5/4 7:37
 */
@RestController
@RequestMapping(value = "/rdcFile",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - RdcFile",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FileController extends BaseController {

    @Resource
    FileService fileService;


    @PostMapping(consumes = "multipart/form-data", value = "/uploadRdcPic")
    @ApiOperation(httpMethod = "POST", value = "巡检任务子项上传文件")
    public List<OptUploadFileRespDto> uploadRdcPic(HttpServletRequest request, OptUploadFileReqDto optUploadFileReqDto) {
        logger.info("uploadCompanyPicture - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);
        String fileType = optUploadFileReqDto.getFileType();
        String bucketName = optUploadFileReqDto.getBucketName();
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return fileService.uploadRdcScenePic(multipartRequest, optUploadFileReqDto, getLoginAuthDto());
    }
}
