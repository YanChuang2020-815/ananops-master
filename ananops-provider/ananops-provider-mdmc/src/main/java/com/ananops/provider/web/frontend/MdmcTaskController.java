package com.ananops.provider.web.frontend;


import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.BaseController;

import com.ananops.provider.model.domain.MdmcTask;
import com.ananops.provider.model.domain.MdmcTaskLog;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.service.MdmcTaskLogService;
import com.ananops.provider.service.MdmcTaskService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/mdmcTask",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - MdmcTask",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MdmcTaskController extends BaseController {
    @Resource
    MdmcTaskService taskService;

    @Resource
    MdmcTaskLogService taskLogService;

    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST",value = "创建维修任务记录")
    public Wrapper<MdmcAddTaskDto> saveTask(@ApiParam(name = "saveTask",value = "添加维修任务记录")@RequestBody MdmcAddTaskDto mdmcAddTaskDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(taskService.saveTask(mdmcAddTaskDto,loginAuthDto));
    }

    @PostMapping(value = "/modify")
    @ApiOperation(httpMethod = "POST",value = "修改维修任务记录")
    public Wrapper<MdmcTaskDto> modifyTask(@ApiParam(name = "modifyTask",value = "修改维修任务记录")@RequestBody MdmcTaskDto mdmcTaskDto){
        return WrapMapper.ok(taskService.modifyTask(mdmcTaskDto));
    }

    @GetMapping(value = "/getTaskLogs/{taskId}")
    @ApiOperation(httpMethod = "GET",value = "根据任务ID查询任务的日志")
    public Wrapper<List<MdmcTaskLog>> getTaskLogs(@PathVariable("taskId") Long taskId){
        List<MdmcTaskLog> taskLogList=taskLogService.getTaskLogsByTaskId(taskId);
        return WrapMapper.ok(taskLogList);
    }

    @PostMapping(value = "/modifyTaskStatusByTaskId/{taskId}")
    @ApiOperation(httpMethod = "POST",value = "更改任务的状态")
    public Wrapper<MdmcChangeStatusDto> modifyTaskStatusByTaskId(@ApiParam(name = "modifyTaskStatus",value = "根据任务的ID修改任务的状态")@RequestBody MdmcChangeStatusDto changeStatusDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        taskService.modifyTaskStatus(changeStatusDto,loginAuthDto);
        return WrapMapper.ok(changeStatusDto);
    }

    @PostMapping(value = "/getTaskByTaskId")
    @ApiOperation(httpMethod = "POST",value = "根据任务的状态，获取工单列表")
    public Wrapper<List<MdmcTask>> getTaskByStatus(@RequestBody MdmcStatusDto statusDto){
        List<MdmcTask> taskList = taskService.getTaskListByStatus(statusDto);
        return WrapMapper.ok(taskList);
    }

    @GetMapping(value = "/getTaskByTaskId/{taskId}")
    @ApiOperation(httpMethod = "GET",value = "根据任务的ID，获取当前的任务详情")
    public Wrapper<MdmcTask> getTaskByTaskId(@PathVariable("taskId") Long taskId){
        MdmcTask task = taskService.getTaskByTaskId(taskId);
        return WrapMapper.ok(task);
    }

    @GetMapping(value = "/getTaskDetailByTaskId")
    @ApiOperation(httpMethod = "GET",value = "根据工单ID，获取工单详情")
    public Wrapper<MdmcTaskDetailDto> getTaskDetailByTaskId(@RequestParam("taskId")Long taskId){
        return WrapMapper.ok(taskService.getTaskDetail(taskId));
    }
    
    @GetMapping(value = "/getTaskByTaskId")
    @ApiOperation(httpMethod = "GET",value = "根据任务的ID，获取当前的任务详情(param)")
    public Wrapper<MdmcTask> getTaskByTaskId2(@RequestParam("taskId") Long taskId){
        MdmcTask task = taskService.getTaskByTaskId(taskId);
        return WrapMapper.ok(task);
    }
    @GetMapping(value = "/getUserList")
    @ApiOperation(httpMethod = "GET",value = "根据用户负责人id，获取值机员id和姓名列表")
    public Wrapper<List<MdmcUserWatcherDto>> getUserList(@RequestParam("userId") Long userId){

        return WrapMapper.ok(taskService.getUserWatcherList(userId));
    }

//    @PostMapping(value = "/getTaskListByUserId")
//    @ApiOperation(httpMethod = "POST",value = "根据用户ID查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByUserId(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByUserId(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//
//    @PostMapping(value = "/getTaskListByFacilitatorId")
//    @ApiOperation(httpMethod = "POST",value = "根据服务商ID查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByFacilitatorId(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByFacilitatorId(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//
//    @PostMapping(value = "/getTaskListByPrincipalId")
//    @ApiOperation(httpMethod = "POST",value = "根据甲方ID查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByPrincipalId(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByPrincipalId(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//    @PostMapping(value = "/getTaskListByMaintainerId")
//    @ApiOperation(httpMethod = "POST",value = "根据维修工ID查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByMaintainerId(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByMaintainerId(statusDto);
//        return WrapMapper.ok(taskList);
//    }
    @PostMapping(value = "/getAllTaskList")
    @ApiOperation(httpMethod = "POST",value = "返回全部工单列表")
    public Wrapper<List<MdmcTask>> getTaskList(@RequestBody MdmcStatusDto statusDto){
        List<MdmcTask> taskList=taskService.getTaskList(statusDto);
        return WrapMapper.ok(taskList);
    }

//    @PostMapping(value = "/getTaskByProjectId")
//    @ApiOperation(httpMethod = "POST",value = "根据项目id返回工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByProjectId(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByProjectId(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//    @PostMapping(value = "/getTaskListByUserIdAndStatus")
//    @ApiOperation(httpMethod = "POST",value = "根据用户id和状态查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByUserIdAndStatus(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByUserIdAndStatus(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//
//    @PostMapping(value = "/getTaskListByMaintainerIdAndStatus")
//    @ApiOperation(httpMethod = "POST",value = "根据维修工id和状态查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByMaintainerIdAndStatus(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByMaintainerIdAndStatus(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//
//    @PostMapping(value = "/getTaskListByFacilitatorIdAndStatus")
//    @ApiOperation(httpMethod = "POST",value = "根据服务商id和状态查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByFacilitatorIdAndStatus(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByFacilitatorIdAndStatus(statusDto);
//        return WrapMapper.ok(taskList);
//    }
//
//    @PostMapping(value = "/getTaskListByPrincipalIdAndStatus")
//    @ApiOperation(httpMethod = "POST",value = "根据甲方id和状态查询工单列表")
//    public Wrapper<List<MdmcTask>> getTaskListByPrincipalIdAndStatus(@RequestBody MdmcStatusDto statusDto){
//        List<MdmcTask> taskList=taskService.getTaskListByPrincipalIdAndStatus(statusDto);
//        return WrapMapper.ok(taskList);
//    }

    @PostMapping(value = "/getTaskListByIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据id和状态查询列表")
    public Wrapper<List<MdmcTask>> getTaskListByIdAndStatus(@RequestBody MdmcQueryDto queryDto){
        List<MdmcTask> taskList=taskService.getTaskListByIdAndStatus(queryDto);
        return WrapMapper.ok(taskList);
    }

    @PostMapping(value = "/getTaskListByIdAndStatusArrary")
    @ApiOperation(httpMethod = "POST",value = "根据id和状态数组查询列表")
    public Wrapper<List<MdmcListDto>> getTaskListByIdAndStatusArrary(@RequestBody MdmcStatusArrayDto statusArrayDto){
        List<MdmcListDto> listDtoList=taskService.getTaskListByIdAndStatusArrary(statusArrayDto);
        return WrapMapper.ok(listDtoList);
    }

    @PostMapping(value = "/getTaskList")
    @ApiOperation(httpMethod = "POST",value = "分页查询列表")
    public Wrapper<PageInfo> getTaskList(@RequestBody MdmcQueryDto queryDto){
        return WrapMapper.ok(taskService.getTaskListByPage(queryDto));
    }

    @PostMapping(value = "/refuseTaskByFacilitator")
    @ApiOperation(httpMethod = "POST",value = "服务商拒单")
    public Wrapper<MdmcChangeStatusDto> refuseTaskByFacilitator(@RequestBody RefuseMdmcTaskDto refuseMdmcTaskDto){
        return WrapMapper.ok(taskService.refuseTaskByFacilitator(refuseMdmcTaskDto));
    }

    @PostMapping(value = "/refuseTaskByMaintainer")
    @ApiOperation(httpMethod = "POST",value = "工程师拒单")
    public Wrapper<MdmcChangeStatusDto> refuseTaskByMaintainer(@RequestBody RefuseMdmcTaskDto refuseMdmcTaskDto){
        return WrapMapper.ok(taskService.refuseTaskByMaintainer(refuseMdmcTaskDto));
    }
    /**
     * 查询工单不同状态的图片
     *
     *
     * @param mdmcFileReqDto  HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(value = "/getPictureByTaskIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据任务的ID和状态，查看附件信息")
    public Wrapper<List<ElementImgUrlDto>> getPictureByTaskIdAndStatus(@RequestBody MdmcFileReqDto mdmcFileReqDto){

        return WrapMapper.ok(taskService.getFileByTaskIdAndStatus(mdmcFileReqDto));
    }

    /**
     * 根据工单id查询不同状态的图片
     *
     *
     * @param taskId  HTTP请求参数
     *
     * @return 返回
     */
    @GetMapping(value = "/getPictureByTaskId")
    @ApiOperation(httpMethod = "GET",value = "根据任务的ID，查看不同状态的附件信息")
    public Wrapper<List<MdmcFileUrlDto>> getPictureByTaskId(@RequestParam("taskId") Long taskId){

        return WrapMapper.ok(taskService.getFileByTaskId(taskId));
    }

    /**
     * 上传工单不同状态的图片
     *
     * @param request HTTP请求
     *
     * @param optUploadFileReqDto HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(consumes = "multipart/form-data", value = "/uploadTaskPicture")
    @ApiOperation(httpMethod = "POST", value = "上传文件")
    public List<OptUploadFileRespDto> uploadTaskPicture(HttpServletRequest request, OptUploadFileReqDto optUploadFileReqDto) {
        logger.info("uploadTaskPicture - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);

        String fileType =optUploadFileReqDto.getFileType();
        String bucketName =optUploadFileReqDto.getBucketName();
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        return taskService.uploadTaskFile(multipartRequest, optUploadFileReqDto, getLoginAuthDto());
    }

    /**
     * 组织录入故障类型和故障位置
     *
     * @param addTroubleInfoDto HTTP请求参数
     *
     * @return 返回
     */
    @PostMapping(value = "/saveTroubleTypeAndAddress")
    @ApiOperation(httpMethod = "POST",value = "组织录入故障类型和故障位置")
    public Wrapper<MdmcAddTroubleInfoDto> saveTroubleTypeAndAddress(@ApiParam(name = "saveTroubleTypeAndAddress",value = "组织录入故障类型列表和故障位置列表")@RequestBody MdmcAddTroubleInfoDto addTroubleInfoDto) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(taskService.saveTroubleList(addTroubleInfoDto, loginAuthDto));
    }

    /**
     * 填写工单时根据不同客户返回故障类型列表和故障位置列表
     *
     * @param userId HTTP请求参数
     *
     * @return 返回
     */
    @GetMapping(value = "/getTroubleTypeListAndAddressList")
    @ApiOperation(httpMethod = "GET",value = "填写工单时根据不同客户返回故障类型列表和故障位置列表")
    public Wrapper<MdmcAddTroubleInfoDto> getTroubleTypeListAndAddressList(@ApiParam(name = "userId",value = "用户id")@RequestParam("userId") Long userId) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(taskService.getTroubleList(userId, loginAuthDto));
    }

    /**
     * 根据用户id获取工单数目
     *
     * @param userId HTTP请求参数
     *
     * @return 返回
     */
    @GetMapping(value = "/getTaskCountByUserId")
    @ApiOperation(httpMethod = "GET",value = "根据用户id获取工单数目")
    public Wrapper<Integer> getTaskCountByUserId(@ApiParam(name = "userId",value = "用户id")@RequestParam("userId") Long userId) {

        return WrapMapper.ok(taskService.getTaskCount(userId));
    }



}
