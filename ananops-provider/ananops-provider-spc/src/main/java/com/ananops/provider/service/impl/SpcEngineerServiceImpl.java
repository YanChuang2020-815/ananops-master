package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.constant.GlobalConstant;
import com.ananops.base.dto.CheckValidDto;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exception.SpcBizException;
import com.ananops.provider.mapper.SpcCompanyMapper;
import com.ananops.provider.mapper.SpcEngineerCertificateMapper;
import com.ananops.provider.mapper.SpcEngineerMapper;
import com.ananops.provider.mapper.SpcEngineerPerformanceMapper;
import com.ananops.provider.model.domain.SpcCompany;
import com.ananops.provider.model.domain.SpcEngineer;
import com.ananops.provider.model.domain.SpcEngineerCertificate;
import com.ananops.provider.model.domain.SpcEngineerPerformance;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.attachment.OptUploadFileByteInfoReqDto;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.dto.user.IdStatusDto;
import com.ananops.provider.model.dto.user.UserIdsReqDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.service.UacGroupBindUserFeignApi;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.model.vo.EngineerSimpleVo;
import com.ananops.provider.model.vo.EngineerVo;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.service.PmcProjectEngineerFeignApi;
import com.ananops.provider.service.SpcEngineerService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.io.FileTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * 操作加盟服务商Engineer的Service接口实现类
 *
 * Created by bingyueduan on 2019/12/30.
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SpcEngineerServiceImpl extends BaseService<SpcEngineer> implements SpcEngineerService {

    @Resource
    private SpcEngineerMapper spcEngineerMapper;

    @Resource
    private SpcCompanyMapper spcCompanyMapper;

    @Resource
    private PmcProjectEngineerFeignApi pmcProjectEngineerFeignApi;

    @Resource
    private UacUserFeignApi uacUserFeignApi;

    @Resource
    private UacGroupFeignApi uacGroupFeignApi;

    @Resource
    private UacGroupBindUserFeignApi uacGroupBindUserFeignApi;

    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Resource
    private SpcEngineerCertificateMapper spcEngineerCertificateMapper;

    @Resource
    private SpcEngineerPerformanceMapper spcEngineerPerformanceMapper;

    @Override
    public List<EngineerDto> getEngineersByProjectId(Long projectId) {
        List<EngineerDto> result = new ArrayList<>();
        List<Long> engineersId = pmcProjectEngineerFeignApi.getEngineersIdByProjectId(projectId).getResult();
        for (Long engineerId : engineersId) {
            result.add(getEngineerDto(engineerId));
        }
        return result;
    }

    @Override
    public EngineerDto getEngineerById(Long engineerId) {
        return getEngineerDto(engineerId);
    }

    @Override
    public List<EngineerDto> getEngineersByBatchId(List<Long> engineerIdList) {
        List<EngineerDto> result = new ArrayList<>();
        for (Long engineerId : engineerIdList) {
            result.add(getEngineerDto(engineerId));
        }
        return result;
    }

    /**
     * 根据工程师Id查询工程师信息
     *
     * @param engineerId 工程师Id
     *
     * @return 返回工程对象
     */
    private EngineerDto getEngineerDto(Long engineerId) {
        EngineerDto engineerDto = new EngineerDto();
        if (StringUtils.isEmpty(engineerId))
            return engineerDto;
        SpcEngineer fx = new SpcEngineer();
        fx.setId(engineerId);
        SpcEngineer spcEngineer = spcEngineerMapper.selectOne(fx);
        if (spcEngineer != null) {
            Long userId = spcEngineer.getUserId();
            UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
            if (userInfoDto != null) {
                try {
                    BeanUtils.copyProperties(engineerDto, spcEngineer);
                    BeanUtils.copyProperties(engineerDto, userInfoDto);
                } catch (Exception e) {
                    logger.error("工程师Dto与用户Dto属性拷贝异常");
                    e.printStackTrace();
                }
            }
        }
        return engineerDto;
    }

    @Override
    public List<EngineerDto> queryAllEngineers(SpcEngineer spcEngineer, LoginAuthDto loginAuthDto) {
        List<EngineerDto> result = new ArrayList<>();
        SpcCompany query = new SpcCompany();
        query.setUserId(loginAuthDto.getUserId());
        SpcCompany spcCompany = spcCompanyMapper.selectOne(query);
        if (PublicUtil.isEmpty(spcCompany)||(spcCompany.getGroupId())==null) {
            return result;
        }
        List<Long> userIdList = uacGroupFeignApi.getUacUserIdListByGroupId(spcCompany.getGroupId()).getResult();
        // 查询所有工程信息
        for(Long userId : userIdList) {
            EngineerDto engineerDto = new EngineerDto();
            SpcEngineer queryCom = new SpcEngineer();
            queryCom.setUserId(userId);
            SpcEngineer queryResult = spcEngineerMapper.selectOne(queryCom);
            String position = spcEngineer.getPosition();
            if (queryResult != null && position != null && position.equals(queryResult.getPosition())) {
                UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
                if (userInfoDto != null) {
                    try {
                        BeanUtils.copyProperties(engineerDto, userInfoDto);
                        BeanUtils.copyProperties(engineerDto, queryResult);
                    } catch (Exception e) {
                        logger.error("工程师Dto与用户Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                }
                engineerDto.setUserId(userId);
                result.add(engineerDto);
            }
        }
        return result;
    }

    @Override
    public List<EngineerDto> queryListWithStatus(EngineerStatusDto engineerStatusDto, LoginAuthDto loginAuthDto) {
        List<EngineerDto> result = new ArrayList<>();
        String engineerStatus = engineerStatusDto.getStatus();
        SpcCompany query = new SpcCompany();
        query.setUserId(loginAuthDto.getUserId());
        SpcCompany spcCompany = spcCompanyMapper.selectOne(query);
        if (PublicUtil.isEmpty(spcCompany)) {
            return result;
        }
        List<Long> userIdList = uacGroupFeignApi.getUacUserIdListByGroupId(spcCompany.getGroupId()).getResult();
        for(Long userId : userIdList){
            EngineerDto engineerDto = new EngineerDto();
            SpcEngineer queryEng = new SpcEngineer();
            queryEng.setUserId(userId);
            SpcEngineer queryResult = spcEngineerMapper.selectOne(queryEng);
            if (queryResult != null) {
                UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
                if (userInfoDto != null && userInfoDto.getStatus().equals(engineerStatus)) {
                    try {
                        BeanUtils.copyProperties(engineerDto, userInfoDto);
                        BeanUtils.copyProperties(engineerDto, queryResult);
                    } catch (Exception e) {
                        logger.error("工程师Dto与用户Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                    engineerDto.setUserId(userId);
                    result.add(engineerDto);
                }
            }
        }
        return result;
    }


    @Override
    public void addSpcEngineer(EngineerRegisterDto engineerRegisterDto, LoginAuthDto loginAuthDto) {
        // 校验注册信息
        validateRegisterInfo(engineerRegisterDto);

        //校验登录名唯一性
        String loginName=engineerRegisterDto.getLoginName();
        CheckValidDto checkValidDto =new CheckValidDto();
        checkValidDto.setType("loginName");
        checkValidDto.setValidValue(loginName);
        String message= uacUserFeignApi.checkValid(checkValidDto).getMessage();
        logger.info(message);
        Preconditions.checkArgument(message==null, "登录名已存在,请更换登录名:"+loginName);


        UserInfoDto userInfoDto = new UserInfoDto();
        SpcEngineer spcEngineer = new SpcEngineer();
        try {
            BeanUtils.copyProperties(userInfoDto, engineerRegisterDto);
            BeanUtils.copyProperties(spcEngineer, engineerRegisterDto);
        } catch (Exception e) {
            logger.error("addSpcEngineer 工程师Dto与用户Dto属性拷贝异常");
            e.printStackTrace();
        }
        //新建的工程师和创建人属于同一个group
        userInfoDto.setGroupId(loginAuthDto.getGroupId());
        userInfoDto.setGroupName(loginAuthDto.getGroupName());
        // 使用UserId将当前的操作用户Id带过去
        userInfoDto.setUserId(String.valueOf(loginAuthDto.getUserId()));
        userInfoDto.setType("engineer");
        userInfoDto.setRemark("工程师");
        Long newUserId = uacUserFeignApi.addUser(userInfoDto).getResult();

        Long engineerId = super.generateId();
        spcEngineer.setId(engineerId);
        spcEngineer.setUserId(newUserId);
        spcEngineer.setPosition("engineer");
        spcEngineer.setUpdateInfo(loginAuthDto);
        spcEngineerMapper.insertSelective(spcEngineer);
    }

    private void validateRegisterInfo(EngineerRegisterDto engineerRegisterDto) {
        String mobileNo = engineerRegisterDto.getMobileNo();

        Preconditions.checkArgument(!StringUtils.isEmpty(engineerRegisterDto.getLoginName()), ErrorCodeEnum.UAC10011007.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(engineerRegisterDto.getUserName()), ErrorCodeEnum.SPC100850017.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(mobileNo), "工程师手机号不能为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(engineerRegisterDto.getEmail()), ErrorCodeEnum.UAC10011018.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(engineerRegisterDto.getIdentityNumber()), ErrorCodeEnum.SPC100850018.msg());

    }

    public static List<EngineerRegisterDto> readExcel(InputStream inputStream ,String fileName) throws Exception{
        InputStream is = inputStream;
        Workbook hssfWorkbook = null;
        if (fileName.endsWith("xlsx")){
            hssfWorkbook = new XSSFWorkbook(is);//Excel 2007
        }else if (fileName.endsWith("xls")){
            hssfWorkbook = new HSSFWorkbook(is);//Excel 2003
        }
        //先支持1个sheet
        List<EngineerRegisterDto> list0 = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
            //HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                //HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                EngineerRegisterDto engineerRegisterDto;
                Row hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    engineerRegisterDto = new EngineerRegisterDto();
                    //HSSFCell name = hssfRow.getCell(0);
                    //HSSFCell pwd = hssfRow.getCell(1);
//                    Cell loginName = hssfRow.getCell(0);
////                    Cell userName = hssfRow.getCell(1);
////                    Cell mobileNum = hssfRow.getCell(2);
////                    Cell email = hssfRow.getCell(3);
////                    Cell identityNumber = hssfRow.getCell(4);
////                    Cell userCode = hssfRow.getCell(5);
////                    Cell titleCeNumber = hssfRow.getCell(6);
////                    //这里是自己的逻辑
////                    engineerRegisterDto.setLoginName(loginName.getStringCellValue());
////                    engineerRegisterDto.setUserName(userName.getStringCellValue());
////                    engineerRegisterDto.setMobileNo(mobileNum.getStringCellValue());
////                    engineerRegisterDto.setEmail(email.getStringCellValue());
////                    engineerRegisterDto.setIdentityNumber(identityNumber.getStringCellValue());
////                    engineerRegisterDto.setUserCode(userCode.getStringCellValue());
////                    engineerRegisterDto.setTitleCeNumber(titleCeNumber.getStringCellValue());
////                    list0.add(engineerRegisterDto);
                    if (hssfRow.getCell(0) != null) {
                        hssfRow.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setLoginName(hssfRow.getCell(0).getStringCellValue());
                    }
                    if (hssfRow.getCell(1) != null) {
                        hssfRow.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setUserName(hssfRow.getCell(1).getStringCellValue());
                    }
                    if (hssfRow.getCell(2) != null) {
                        hssfRow.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setMobileNo(hssfRow.getCell(2).getStringCellValue());
                    }
                    if (hssfRow.getCell(3) != null) {
                        hssfRow.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setEmail(hssfRow.getCell(3).getStringCellValue());
                    }
                    if (hssfRow.getCell(4) != null) {
                        hssfRow.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setIdentityNumber(hssfRow.getCell(4).getStringCellValue());
                    }
                    if (hssfRow.getCell(5) != null) {
                        hssfRow.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setUserCode(hssfRow.getCell(5).getStringCellValue());
                    }
                    if (hssfRow.getCell(6) != null) {
                        hssfRow.getCell(6).setCellType(Cell.CELL_TYPE_STRING);
                        engineerRegisterDto.setTitleCeNumber(hssfRow.getCell(6).getStringCellValue());
                    }
                    list0.add(engineerRegisterDto);
                }
            }
        }
        return list0;
    }

    @Override
    public EngineerVo queryByEngineerId(Long userId) {
        logger.info("queryByEngineerId - 根据工程师Id查询工程师信息接口. userId={}", userId);
        EngineerVo engineerVo = new EngineerVo();
        SpcEngineer queryE = new SpcEngineer();
        queryE.setUserId(userId);
        SpcEngineer spcEngineer = spcEngineerMapper.selectOne(queryE);
        if (!StringUtils.isEmpty(userId)) {
            UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
            try {
                BeanUtils.copyProperties(engineerVo, spcEngineer);
                BeanUtils.copyProperties(engineerVo, userInfoDto);
            } catch (Exception e) {
                logger.error("queryByEngineerId 工程师Dto与用户组Dto属性拷贝异常");
                e.printStackTrace();
            }
        }
        return engineerVo;
    }

    @Override
    public List<OptUploadFileRespDto> uploadEngineerFile(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto) {
        // 这里的filePath来区分照片类型，有以下两种:
        // 工程师身份证照片：identityPhoto
        // 职称证书照片：titleCePhoto

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
                optUploadFileReqDto.setFilePath("ananops/spc/engineer/" + userId + "/" + filePath + "/");
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
    public List<ElementImgUrlDto> getEngineerFile(Long id) {
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(id.toString());
        optBatchGetUrlRequest.setEncrypt(true);
        optBatchGetUrlRequest.setExpires(null);
        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }

    @Override
    public int modifyEngineerStatusById(ModifyEngineerStatusDto modifyEngineerStatusDto) {
        Long engineerId = modifyEngineerStatusDto.getEngineerId();
        String status = modifyEngineerStatusDto.getStatus();
        if (!StringUtils.isEmpty(engineerId) && !StringUtils.isEmpty(status)) {
            Long uacUserId = spcEngineerMapper.selectByPrimaryKey(engineerId).getUserId();
            IdStatusDto modifyUserStatusDto = new IdStatusDto();
            modifyUserStatusDto.setId(uacUserId);
            modifyUserStatusDto.setStatus(Integer.valueOf(status));
            return (int)uacUserFeignApi.modifyUserStatus(modifyUserStatusDto).getResult();
        }
        return 0;
    }

    @Override
    public void saveSpcEngineer(EngineerVo engineerVo, LoginAuthDto loginAuthDto) {
        Long engineerId = engineerVo.getId();
        if (engineerId == null) {
            return;
        }
        SpcEngineer queryResutl = spcEngineerMapper.selectByPrimaryKey(engineerId);
        Long userId = queryResutl.getUserId();
        // 校验保存信息
        validateCompanyVo(engineerVo);

        // 获取该工程师所属组织id
        Long groupId = uacGroupBindUserFeignApi.getGroupIdByUserId(userId).getResult();

        UserInfoDto userInfoDto = new UserInfoDto();
        try {
            BeanUtils.copyProperties(userInfoDto, engineerVo);
        } catch (Exception e) {
            logger.error("服务商Dto与用户组Dto属性拷贝异常");
            e.printStackTrace();
        }
        userInfoDto.setId(userId);
        if (groupId != null) {
            userInfoDto.setGroupId(groupId);
        }
        Long uacUserId = uacUserFeignApi.userSave(userInfoDto).getResult();

        SpcEngineer spcEngineer = new SpcEngineer();
        if (!StringUtils.isEmpty(engineerId) && !StringUtils.isEmpty(uacUserId)) {
            Date row = new Date();
            // 封装更新工程师的信息
            try {
                BeanUtils.copyProperties(spcEngineer, engineerVo);
            } catch (Exception e) {
                logger.error("工程师Dto与用户Dto属性拷贝异常");
                e.printStackTrace();
            }
            spcEngineer.setId(engineerId);
            spcEngineer.setUserId(userId);
            spcEngineer.setUpdateInfo(loginAuthDto);
              //这个地方应该是完善工程师的信息，而不是注册
            logger.info("完善工程师信息. spcEngineer={}", spcEngineer);
            spcEngineerMapper.updateByPrimaryKeySelective(spcEngineer);
        }

        // 更新附件信息
        List<Long> attachmentIds = new ArrayList<>();
        if (spcEngineer.getIdentityPhoto() != null) {
            String legalPersonidPhotoIds = spcEngineer.getIdentityPhoto();
            if (legalPersonidPhotoIds.contains(",")) {
                String[] legalIds = legalPersonidPhotoIds.split(",");
                for (String id : legalIds) {
                    attachmentIds.add(Long.parseLong(id));
                }
            } else {
                attachmentIds.add(Long.parseLong(legalPersonidPhotoIds));
            }
        }
        if (spcEngineer.getTitleCePhoto() != null) {
            attachmentIds.add(Long.parseLong(spcEngineer.getTitleCePhoto()));
        }
        if(PublicUtil.isNotEmpty(attachmentIds)) {
            OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
            optAttachmentUpdateReqDto.setAttachmentIds(attachmentIds);
            optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
            optAttachmentUpdateReqDto.setRefNo(spcEngineer.getId().toString());
            opcOssFeignApi.batchUpdateAttachment(optAttachmentUpdateReqDto);
        }
    }

    @Override
    public List<EngineerSimpleVo> getEngineersListByProjectId(Long projectId) {
        List<Long> userIdList = pmcProjectEngineerFeignApi.getEngineersIdByProjectId(projectId).getResult();
        logger.info("getEngineersIdByProjectId - 根据项目Id查询UserId列表. userIdList={}", userIdList);
        List<EngineerSimpleVo> engineerSimpleVos = new ArrayList<>();
        // 筛选工程师的UserId
        List<Long> engineerUserIdList = new ArrayList<>();
        if (userIdList != null) {
            for (Long userId : userIdList) {
                SpcEngineer queryEng = new SpcEngineer();
                queryEng.setUserId(userId);
                int count = spcEngineerMapper.selectCount(queryEng);
                if (count > 0) {
                    engineerUserIdList.add(userId);
                }
            }
        }
        // 填充工程师姓名
        if (!engineerUserIdList.isEmpty()) {
            UserIdsReqDto userIdsReqDto = new UserIdsReqDto();
            userIdsReqDto.setUserIds(engineerUserIdList);
            List<UserInfoDto> userInfoDtos = uacUserFeignApi.getUserListByUserIds(userIdsReqDto).getResult();
            if (userInfoDtos != null) {
                for (UserInfoDto userInfoDto : userInfoDtos) {
                    EngineerSimpleVo engineerSimpleVo = new EngineerSimpleVo();
                    engineerSimpleVo.setId(userInfoDto.getId());
                    engineerSimpleVo.setName(userInfoDto.getUserName());
                    engineerSimpleVos.add(engineerSimpleVo);
                }
            }
        }
        return engineerSimpleVos;
    }

    @Override
    public List<EngineerDto> queryListByGroupId(EngineerQueryDto engineerQueryDto, LoginAuthDto loginAuthDto) {
        List<EngineerDto> result = new ArrayList<>();
        Long groupId = loginAuthDto.getGroupId();
        // 获取公司的GroupId
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        if (PublicUtil.isEmpty(companyDto)||(companyDto.getId())==null) {
            return result;
        }
        List<Long> userIdList = uacGroupFeignApi.getUacUserIdListByGroupId(companyDto.getId()).getResult();
        // 查询所有工程信息
        for(Long userId : userIdList) {
            EngineerDto engineerDto = new EngineerDto();
            SpcEngineer queryCom = new SpcEngineer();
            queryCom.setUserId(userId);
            SpcEngineer queryResult = spcEngineerMapper.selectOne(queryCom);
            String position = engineerQueryDto.getPosition();
            if (queryResult != null && position != null && position.equals(queryResult.getPosition())) {
                UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
                if (userInfoDto != null) {
                    try {
                        BeanUtils.copyProperties(engineerDto, userInfoDto);
                        BeanUtils.copyProperties(engineerDto, queryResult);
                    } catch (Exception e) {
                        logger.error("工程师Dto与用户Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                }
                engineerDto.setUserId(userId);
                result.add(engineerDto);
            }
        }
        return result;
    }

    @Transactional
    @Override
    public int deleteEngineerById(Long engineerId) {
        if (engineerId == null) {
            throw new SpcBizException(ErrorCodeEnum.SPC100850022);
        }
        // 删除工程师账号在UAC中的记录
        SpcEngineer spcEngineer = spcEngineerMapper.selectByPrimaryKey(engineerId);
        if (spcEngineer == null) {
            throw new SpcBizException(ErrorCodeEnum.SPC100850021, engineerId);
        }
        uacUserFeignApi.deleteUserById(spcEngineer.getUserId());
        // 删除工程师证书记录
        SpcEngineerCertificate deleteSpcEngineerCertificate = new SpcEngineerCertificate();
        deleteSpcEngineerCertificate.setEngineerId(engineerId);
        spcEngineerCertificateMapper.delete(deleteSpcEngineerCertificate);
        // 删除工程师业绩记录
        SpcEngineerPerformance deleteSpcEngineerPerformance = new SpcEngineerPerformance();
        deleteSpcEngineerPerformance.setEngineerId(engineerId);
        spcEngineerPerformanceMapper.delete(deleteSpcEngineerPerformance);
        // 删除工程师在SPC中的记录
        spcEngineerMapper.deleteByPrimaryKey(engineerId);
        return 1;
    }

    private void validateCompanyVo(EngineerVo engineerVo) {
        String mobileNo = engineerVo.getMobileNo();

        Preconditions.checkArgument(!StringUtils.isEmpty(engineerVo.getLoginName()), ErrorCodeEnum.UAC10011007.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(engineerVo.getUserName()), ErrorCodeEnum.SPC100850017.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(mobileNo), "手机号不能为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(engineerVo.getIdentityNumber()), ErrorCodeEnum.SPC100850018.msg());
    }
}
