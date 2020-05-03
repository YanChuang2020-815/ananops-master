package com.ananops.provider.web.frontend;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.annotation.LogAnnotation;
import com.ananops.core.support.BaseController;
import com.ananops.provider.model.domain.SpcEngineer;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.vo.EngineerSimpleVo;
import com.ananops.provider.model.vo.EngineerVo;
import com.ananops.provider.service.PmcProjectEngineerFeignApi;
import com.ananops.provider.service.SpcEngineerService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.ananops.provider.service.impl.SpcEngineerServiceImpl.readExcel;

/**
 * 服务商模块对外提供操作Engineer(工程师)的Restful接口
 *
 * Created by bingyueduan on 2020/1/2.
 */
@RestController
@RequestMapping(value = "/engineer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - SpcEngineerController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SpcEngineerController extends BaseController {

    @Resource
    private SpcEngineerService spcEngineerService;

    /**
     * 分页查询服务商下工程师.
     *
     * @param spcEngineer 传入的查询参数
     *
     * @return the wrapper
     */
    @PostMapping(value = "/queryAllEngineers")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "分页查询服务商下工程师")
    public Wrapper<PageInfo<EngineerDto>> queryAllEngineers(@ApiParam(name = "spcEngineer", value = "分页查询参数") @RequestBody SpcEngineer spcEngineer) {
        logger.info(" 分页查询参数 spcEngineer={}", spcEngineer);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        PageHelper.startPage(spcEngineer.getPageNum(), spcEngineer.getPageSize());
        spcEngineer.setOrderBy("update_time desc");
        List<EngineerDto> engineersVoList = spcEngineerService.queryAllEngineers(spcEngineer, loginAuthDto);
        return WrapMapper.ok(new PageInfo<>(engineersVoList));
    }

    /**
     * 根据公司GroupId查询服务商下工程师.
     *
     * @param engineerQueryDto 传入的查询参数
     *
     * @return the wrapper
     */
    @PostMapping(value = "/queryListByGroupId")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "根据公司GroupId分页查询服务商下工程师")
    public Wrapper<PageInfo<EngineerDto>> queryAllEngineers(@ApiParam(name = "engineerQueryDto", value = "分页查询参数") @RequestBody EngineerQueryDto engineerQueryDto) {
        logger.info(" 分页查询参数 engineerQueryDto={}", engineerQueryDto);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        PageHelper.startPage(engineerQueryDto.getPageNum(), engineerQueryDto.getPageSize());
        engineerQueryDto.setOrderBy("update_time desc");
        List<EngineerDto> engineersVoList = spcEngineerService.queryListByGroupId(engineerQueryDto, loginAuthDto);
        return WrapMapper.ok(new PageInfo<>(engineersVoList));
    }

    /**
     * 按工程师状态查询工程师列表.
     *
     * @param engineerStatusDto 传入的查询参数
     *
     * @return the wrapper
     */
    @PostMapping(value = "/queryListWithStatus")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "分页查询服务商下工程师")
    public Wrapper<PageInfo<EngineerDto>> queryListWithStatus(@ApiParam(name = "engineerStatusDto", value = "按工程师状态查询参数") @RequestBody EngineerStatusDto engineerStatusDto) {
        logger.info(" 按工程师状态查询参数 engineerStatusDto={}", engineerStatusDto);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        PageHelper.startPage(engineerStatusDto.getPageNum(), engineerStatusDto.getPageSize());
        engineerStatusDto.setOrderBy("update_time desc");
        List<EngineerDto> engineersVoList = spcEngineerService.queryListWithStatus(engineerStatusDto, loginAuthDto);
        return WrapMapper.ok(new PageInfo<>(engineersVoList));
    }

    /**
     * 添加单个工程师.
     *
     * @param engineerRegisterDto 传入的工程师对象信息
     *
     * @return the wrapper
     */
    @PostMapping(value = "/add")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "添加工程师")
    public Wrapper<Integer> addEngineer(@ApiParam(name = "engineerRegisterDto", value = "按工程师状态查询参数") @RequestBody EngineerRegisterDto engineerRegisterDto) {
        logger.info(" 添加工程师 engineerRegisterDto={}", engineerRegisterDto);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        spcEngineerService.addSpcEngineer(engineerRegisterDto, loginAuthDto);
        return WrapMapper.ok();
    }

    /**
     * 通过上传工程师信息Excel表格,批量创建工程师.
     *
     * @param request             请求
     *
     * @return the wrapper
     */
    @PostMapping(value = "/uploadEngineerExcelFile")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "上传文件")
    public Wrapper<String> uploadEngineerExcelFile(MultipartFile file,  HttpServletRequest request) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        logger.info("uploadEngineerExcelFile - 上传文件.");
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();

            try {
                logger.info("开始读取文件内容...filename = {}", fileName);
                InputStream inputStream = file.getInputStream();
                List<EngineerRegisterDto> engineerRegisterDtos = readExcel(inputStream, fileName);
                for (EngineerRegisterDto engineerRegisterDto : engineerRegisterDtos) {
                    spcEngineerService.addSpcEngineer(engineerRegisterDto, loginAuthDto);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return WrapMapper.ok("上传文件success");
        } else {
            return WrapMapper.ok("上传文件failure");
        }
    }

    /**
     * 通过工程师在UAC中的UserId查询工程师信息
     *
     * @param userId 工程师ID
     *
     * @return 返回工程师信息
     */
    @PostMapping(value = "/getSpcEngineerById/{userId}")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "工程师在UAC中的UserId查询工程师信息")
    public Wrapper<EngineerVo> getSpcEngineerById(@ApiParam(name = "userId", value = "工程师在UAC中的UserID") @PathVariable Long userId) {
        logger.info("getSpcEngineerById - 根据工程师在UAC中的UserId查询工程师信息. userId={}", userId);
        EngineerVo engineerVo = spcEngineerService.queryByEngineerId(userId);
        logger.info("getSpcEngineerById - 根据工程师在UAC中的UserId查询工程师信息. [OK] engineerVo={}", engineerVo);
        return WrapMapper.ok(engineerVo);
    }

    /**
     * 根据Id修改工程师状态.
     *
     * @param modifyEngineerStatusDto the modify Engineer status dto
     *
     * @return the wrapper
     */
    @PostMapping(value = "/modifyEngineerStatusById")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "根据Id修改工程师状态")
    public Wrapper<Integer> modifyEngineerStatusById(@ApiParam(name = "modifyEngineerStatusById", value = "工程师禁用/激活Dto") @RequestBody ModifyEngineerStatusDto modifyEngineerStatusDto) {
        logger.info(" 根据Id修改工程师状态 modifyEngineerStatusDto={}", modifyEngineerStatusDto);
        int result = spcEngineerService.modifyEngineerStatusById(modifyEngineerStatusDto);
        return handleResult(result);
    }

    /**
     * 根据Id删除工程师.
     *
     * @param engineerId 工程师Id
     *
     * @return the wrapper
     */
    @PostMapping(value = "/deleteEngineerById/{engineerId}")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "根据工程师Id（不是UserId）删除工程师账号")
    public Wrapper<Integer> deleteEngineerById(@ApiParam(name = "engineerId", value = "工程师禁用/激活Dto") @PathVariable Long engineerId) {
        logger.info(" 根据工程师Id（不是UserId）删除工程师账号 engineerId={}", engineerId);
        int result = spcEngineerService.deleteEngineerById(engineerId);
        return handleResult(result);
    }

    /**
     * 根据工程师Id保存工程师信息.
     *
     * @param engineerVo 编辑之后的对象
     *
     * @return the spc engineer by id
     */
    @PostMapping(value = "/save")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "根据工程师Id保存工程师信息")
    public Wrapper<Integer> saveSpcEngineer(@ApiParam(name = "engineerVo", value = "工程师ID及详细信息") @RequestBody EngineerVo engineerVo) {
        logger.info(" 保存工程师信息 engineerVo={}", engineerVo);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        spcEngineerService.saveSpcEngineer(engineerVo, loginAuthDto);
        return WrapMapper.ok();
    }

    /**
     * 根据项目Id查询项目下的全部工程师简单信息列表
     *
     * @param projectId 项目Id
     *
     * @return 返回工程师信息列表
     */
    @PostMapping(value = "/getEngineerIdListByProjectId/{projectId}")
    @LogAnnotation
    @ApiOperation(httpMethod = "POST", value = "根据项目Id获取项目下的全部工程师简单信息列表")
    public Wrapper<List<EngineerSimpleVo>> getEngineerIdListByProjectId(@ApiParam(name = "projectId",value = "项目Id") @PathVariable Long projectId){
        logger.info(" 根据项目ID查询项目对应的工程师ID的列表 projectId={}", projectId);
        return WrapMapper.ok(spcEngineerService.getEngineersListByProjectId(projectId));
    }

    /**
     * 上传工程师相关的图片
     *
     * @param request HTTP请求
     *
     * @param optUploadFileReqDto HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(consumes = "multipart/form-data", value = "/uploadEngineerPicture")
    @ApiOperation(httpMethod = "POST", value = "上传文件")
    public List<OptUploadFileRespDto> uploadEngineerPicture(HttpServletRequest request, OptUploadFileReqDto optUploadFileReqDto) {
        logger.info("uploadEngineerPicture - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);

        String fileType = optUploadFileReqDto.getFileType();
        String bucketName = optUploadFileReqDto.getBucketName();
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return spcEngineerService.uploadEngineerFile(multipartRequest, optUploadFileReqDto, getLoginAuthDto());
    }

    /**
     * 根据工程师id下载工程师文件
     * @param id
     * @return
     */
    @PostMapping(value = "/getEngineerFile/{id}")
    @ApiOperation(httpMethod = "POST", value = "工程师文件下载")
    public Wrapper<List<ElementImgUrlDto>> getEngineerFile(@PathVariable Long id) {
        List<ElementImgUrlDto> elementImgUrlDtoList = spcEngineerService.getEngineerFile(id);
        logger.info("elementImgUrlDtoList："+elementImgUrlDtoList);
        return WrapMapper.ok(elementImgUrlDtoList);
    }

}
