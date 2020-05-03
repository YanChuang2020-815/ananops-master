package com.ananops.provider.web.frontend;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.PmcContract;
import com.ananops.provider.model.dto.PmcUploadContractReqDto;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.service.PmcContractService;
import com.ananops.provider.service.PmcFileService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created By ChengHao On 2019/12/9
 */
@RestController
@RequestMapping(value = "/file", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB -PmcFileController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PmcFileController extends BaseController {
    @Autowired
    PmcFileService pmcFileService;
    @Autowired
    PmcContractService pmcContractService;
    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

//    @PostMapping("/upload")
//    @ApiOperation(httpMethod = "POST", value = "上传合同文件")
//    public Wrapper upload(MultipartFile uploadFile, HttpServletRequest req, @RequestBody PmcContract pmcContract) {
//        LoginAuthDto loginAuthDto = getLoginAuthDto();
//        String realPath = req.getSession().getServletContext().getRealPath("/uploadFile/");
//        logger.info("realPath: " + realPath);
//        String format = sdf.format(new Date());
//        File folder = new File(realPath + format);
//        if (!folder.isDirectory()) {
//            folder.mkdirs();
//        }
//        String oldName = uploadFile.getOriginalFilename();
//        //给文件重命名
//        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
//        try {
//            uploadFile.transferTo(new File(folder, newName));  //保存操作
//            String filePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/uploadFile/" + format + newName;
//            pmcContract.setFilePath(filePath);
//            pmcContractService.saveContract(pmcContract, loginAuthDto);
//            return WrapMapper.ok(filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return WrapMapper.ok("上次文件失败");
//    }
//
//
//    @PostMapping(consumes = "multipart/form-data", value = "/uploadPmcContract")
//    @ApiOperation(httpMethod = "POST", value = "上传合同文件")
//    public Map<String, Object> uploadPmcContract(javax.servlet.http.HttpServletRequest request,
//                                                 PmcUploadContractReqDto pmcUploadContractReqDto) {
//        OptUploadFileReqDto optUploadFileReqDto = pmcUploadContractReqDto.getOptUploadFileReqDto();
//        logger.info("uploadPmcContract - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);
//        Long contractId = pmcUploadContractReqDto.getContractId();
//        String fileType = optUploadFileReqDto.getFileType();
//        String bucketName = optUploadFileReqDto.getBucketName();
//        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
//        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        List<OptUploadFileRespDto> optUploadFileRespDtos =
//                pmcFileService.uploadPmcContract(multipartRequest, pmcUploadContractReqDto, getLoginAuthDto());
//
//        List<String> contractUrlList = Lists.newArrayList();
//        List<Long> attachmentIds = Lists.newArrayList();
//        for (final OptUploadFileRespDto fileRespDto : optUploadFileRespDtos) {
//            contractUrlList.add(fileRespDto.getAttachmentUrl());
//            attachmentIds.add(fileRespDto.getAttachmentId());
//        }
//
//        //保存合同附件路径（目前默认合同附件为单个上传）
//        PmcContract pmcContract = new PmcContract();
//        pmcContract.setId(contractId);
//        pmcContract.setFilePath(contractUrlList.get(0));
//        pmcContractService.saveContract(pmcContract, getLoginAuthDto());
//
//        //关联业务单号
//        OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
//        optAttachmentUpdateReqDto.setId(attachmentIds.get(0));
//        optAttachmentUpdateReqDto.setRefNo(contractId.toString());
//        optAttachmentUpdateReqDto.setLoginAuthDto(getLoginAuthDto());
//        opcOssFeignApi.updateAttachmentInfo(optAttachmentUpdateReqDto);
//
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("errno", 0);
//        map.put("data", contractUrlList.toArray());
//        map.put("attachmentIds",attachmentIds.toArray());
//        return map;
//    }

    /**
     * 上传合同附件
     *
     * @param request
     * @param optUploadFileReqDto
     * @return
     */
    @PostMapping(consumes = "multipart/form-data", value = "/uploadContractAttachment")
    @ApiOperation(httpMethod = "POST", value = "上传文件")
    public List<OptUploadFileRespDto> uploadContractAttachment(HttpServletRequest request, OptUploadFileReqDto optUploadFileReqDto) {
        logger.info("uploadContractAttachment - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);

        String fileType = optUploadFileReqDto.getFileType();
        String bucketName = optUploadFileReqDto.getBucketName();
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return pmcFileService.uploadContractAttachment(multipartRequest, optUploadFileReqDto, getLoginAuthDto());
    }

    /**
     * 根据合同id下载合同附件
     * @param id
     * @return
     */
    @PostMapping(value = "/getContractAttachment/{id}")
    @ApiOperation(httpMethod = "POST", value = "合同附件下载")
    public Wrapper<List<ElementImgUrlDto>> getContractAttachment(@PathVariable Long id) {
        List<ElementImgUrlDto> elementImgUrlDtoList = pmcFileService.getContractAttachment(id);
        logger.info("elementImgUrlDtoList："+elementImgUrlDtoList);
        return WrapMapper.ok(elementImgUrlDtoList);
    }
}
