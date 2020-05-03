package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.PmcContractMapper;
import com.ananops.provider.model.domain.PmcContract;
import com.ananops.provider.model.dto.PmcUploadContractReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.service.PmcFileService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created By ChengHao On 2019/12/17
 */
@Service
public class PmcFileServiceImpl extends BaseService<PmcContract> implements PmcFileService {
    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Override
    public List<OptUploadFileRespDto> uploadPmcContract(MultipartHttpServletRequest multipartRequest,
                                                        PmcUploadContractReqDto pmcUploadContractReqDto, LoginAuthDto loginAuthDto) {
        OptUploadFileReqDto optUploadFileReqDto = pmcUploadContractReqDto.getOptUploadFileReqDto();
        Long userId = loginAuthDto.getUserId();
        String userName = loginAuthDto.getUserName();
        Long contractId = pmcUploadContractReqDto.getContractId();

        List<OptUploadFileRespDto> result = Lists.newArrayList();
        try {
            List<MultipartFile> fileList = multipartRequest.getFiles("file");
            if (fileList.isEmpty()) {
                return result;
            }
            for (MultipartFile multipartFile : fileList) {
                String fileName = multipartFile.getOriginalFilename();
                if (PublicUtil.isEmpty(fileName)) {
                    continue;
                }
                Preconditions.checkArgument(multipartFile.getSize() <= GlobalConstant.FILE_MAX_SIZE, "上传文件不能大于5M");
                InputStream inputStream = multipartFile.getInputStream();

                String inputStreamFileType = FileTypeUtil.getType(inputStream);
                // 将上传文件的字节流封装到到Dto对象中
                OptUploadFileByteInfoReqDto optUploadFileByteInfoReqDto = new OptUploadFileByteInfoReqDto();
                optUploadFileByteInfoReqDto.setFileByteArray(multipartFile.getBytes());
                optUploadFileByteInfoReqDto.setFileName(fileName);
                optUploadFileByteInfoReqDto.setFileType(inputStreamFileType);
                optUploadFileReqDto.setUploadFileByteInfoReqDto(optUploadFileByteInfoReqDto);
                //通过userId和contractId设置文件路径
                optUploadFileReqDto.setFilePath("ananops/pmc/" + userId + "/" + contractId + "/");
                optUploadFileReqDto.setUserId(userId);
                optUploadFileReqDto.setUserName(userName);
                OptUploadFileRespDto optUploadFileRespDto = opcOssFeignApi.uploadFile(optUploadFileReqDto).getResult();
                result.add(optUploadFileRespDto);
            }
        } catch (IOException e) {
            logger.error("上传文件失败={}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<OptUploadFileRespDto> uploadContractAttachment(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto) {
        String filePath = optUploadFileReqDto.getFilePath();
        Long userId = loginAuthDto.getUserId();
        String userName = loginAuthDto.getUserName();
        List<OptUploadFileRespDto> result = Lists.newArrayList();
        try {
            List<MultipartFile> fileList = multipartRequest.getFiles("file");
            if (fileList.isEmpty()) {
                return result;
            }

            for (MultipartFile multipartFile : fileList) {
                String fileName = multipartFile.getOriginalFilename();
                if (PublicUtil.isEmpty(fileName)) {
                    continue;
                }
                Preconditions.checkArgument(multipartFile.getSize() <= GlobalConstant.FILE_MAX_SIZE, "上传文件不能大于5M");
                InputStream inputStream = multipartFile.getInputStream();

                String inputStreamFileType = FileTypeUtil.getType(inputStream);
                // 将上传文件的字节流封装到到Dto对象中
                OptUploadFileByteInfoReqDto optUploadFileByteInfoReqDto = new OptUploadFileByteInfoReqDto();
                optUploadFileByteInfoReqDto.setFileByteArray(multipartFile.getBytes());
                optUploadFileByteInfoReqDto.setFileName(fileName);
                optUploadFileByteInfoReqDto.setFileType(inputStreamFileType);
                optUploadFileReqDto.setUploadFileByteInfoReqDto(optUploadFileByteInfoReqDto);
                // 设置不同文件路径来区分图片
                optUploadFileReqDto.setFilePath("ananops/pmc/contract/" + userId + "/" + filePath + "/");
                optUploadFileReqDto.setUserId(userId);
                optUploadFileReqDto.setUserName(userName);
                OptUploadFileRespDto optUploadFileRespDto = opcOssFeignApi.uploadFile(optUploadFileReqDto).getResult();
                result.add(optUploadFileRespDto);
            }
        } catch (IOException e) {
            logger.error("上传文件失败={}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<ElementImgUrlDto> getContractAttachment(Long id) {
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(id.toString());
        optBatchGetUrlRequest.setEncrypt(true);
        optBatchGetUrlRequest.setExpires(null);
        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }
}
