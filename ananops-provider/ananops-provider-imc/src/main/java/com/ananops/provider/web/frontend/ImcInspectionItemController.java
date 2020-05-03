package com.ananops.provider.web.frontend;


import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseController;
import com.ananops.provider.core.annotation.AnanLogAnnotation;
import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.model.enums.ItemStatusEnum;
import com.ananops.provider.model.enums.TaskStatusEnum;
import com.ananops.provider.model.vo.ItemLogVo;
import com.ananops.provider.service.*;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by rongshuai on 2019/11/28 10:10
 */
@RestController
@RequestMapping(value = "/inspectionItem",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WEB - ImcInspectionItemService",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ImcInspectionItemController extends BaseController {
    @Resource
    ImcInspectionItemService imcInspectionItemService;

    @Resource
    ImcInspectionItemLogService imcInspectionItemLogService;

    @Resource
    ImcInspectionTaskService imcInspectionTaskService;

    @Resource
    ImcItemFeignApi imcItemQueryFeignApi;

    @Resource
    MdmcTaskFeignApi mdmcTaskFeignApi;

    @PostMapping(value = "/save")
    @ApiOperation(httpMethod = "POST",value = "编辑巡检任务子项记录")
    @AnanLogAnnotation
    public Wrapper<ImcAddInspectionItemDto> saveInspectionItem(@ApiParam(name = "saveInspectionItem",value = "新增一条巡检任务子项记录")@RequestBody ImcAddInspectionItemDto imcAddInspectionItemDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(imcInspectionItemService.saveInspectionItem(imcAddInspectionItemDto,loginAuthDto));
    }

    @PostMapping(value = "/getAllItemByTaskId")
    @ApiOperation(httpMethod = "POST",value = "根据巡检任务ID，获取其对应的全部任务子项")
    public Wrapper<List<ImcInspectionItem>> getAllItemByTaskId(@ApiParam(name = "getAllItemByTaskId",value = "根据巡检任务ID，获取其对应的全部任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAllItemByTaskId(itemQueryDto));
    }

    @PostMapping(value = "/getAllItemListByTaskId")
    @ApiOperation(httpMethod = "POST",value = "根据巡检任务ID，获取其对应的全部任务子项（可返回总数total）")
    public Wrapper<PageInfo> getAllItemListByTaskId(@ApiParam(name = "getAllItemByTaskId",value = "根据巡检任务ID，获取其对应的全部任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAllItemByTaskIdAndPage(itemQueryDto));
    }

    @PostMapping(value = "/deleteItemByItemId/{itemId}")
    @ApiOperation(httpMethod = "POST",value = "删除指定的巡检任务子项")
    public Wrapper deleteItemByItemId(@PathVariable Long itemId){
        imcInspectionItemService.deleteItemByItemId(itemId);
        return WrapMapper.ok();
    }

    @PostMapping(value = "/getAllItemByTaskIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据巡检任务ID，获取其对应的指定状态的全部任务子项")
    public Wrapper<List<ImcInspectionItem>> getAllItemByTaskIdAndStatus(@ApiParam(name = "getAllItemByTaskIdAndStatus",value = "根据巡检任务ID，获取其对应的指定状态的全部任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAllItemByTaskIdAndStatus(itemQueryDto));
    }

    @PostMapping(value = "/getAllItemListByTaskIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据巡检任务ID，获取其对应的指定状态的全部任务子项（可返回总数total）")
    public Wrapper<PageInfo> getAllItemListByTaskIdAndStatus(@ApiParam(name = "getAllItemByTaskIdAndStatus",value = "根据巡检任务ID，获取其对应的指定状态的全部任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAllItemByTaskIdAndStatusAndPage(itemQueryDto));
    }

    @GetMapping(value = "/getItemByItemId/{itemId}")
    @ApiOperation(httpMethod = "GET",value = "根据巡检任务子项的ID，获取对应的巡检任务子项")
    public Wrapper<ImcInspectionItemDto> getItemByItemId(@PathVariable Long itemId){
        return WrapMapper.ok(imcInspectionItemService.getItemDtoByItemId(itemId));
    }

    @PostMapping(value = "/modifyItemStatusByItemId")
    @ApiOperation(httpMethod = "POST",value = "更改巡检任务子项的状态")
    @AnanLogAnnotation
    public Wrapper<ImcItemChangeStatusDto> modifyItemStatusByItemId(@ApiParam(name = "modifyItemStatus",value = "根据巡检任务子项ID，更改子项的状态")@RequestBody ImcItemChangeStatusDto imcItemChangeStatusDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        imcItemChangeStatusDto.setLoginAuthDto(loginAuthDto);
        return WrapMapper.ok(imcInspectionItemService.modifyImcItemStatusByItemId(imcItemChangeStatusDto));
    }

    @PostMapping(value = "/putResultByItemId")
    @ApiOperation(httpMethod = "POST",value = "提交巡检结果相关信息")
    @AnanLogAnnotation
    public Wrapper<ImcItemChangeStatusDto> putResultByItemId(@ApiParam(name = "itemResultDto",value = "巡检任务子项结果相关资料提交")@RequestBody ItemResultDto itemResultDto){
        logger.info("提交巡检结果相关信息,itemResultDto={}",itemResultDto);
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        return WrapMapper.ok(imcInspectionItemService.putResultByItemId(itemResultDto, loginAuthDto));
    }

    @PostMapping(value = "/getItemLogs")
    @ApiOperation(httpMethod = "POST",value = "根据巡检任务子项的ID查询对应的日志")
    public Wrapper<List<ItemLogVo>> getItemLogs(@ApiParam(name = "getItemLogs",value = "根据巡检任务子项的ID查询对应的日志")@RequestBody ItemLogQueryDto itemLogQueryDto){
        return WrapMapper.ok(imcInspectionItemLogService.getItemLogs(itemLogQueryDto));
    }

    @PostMapping(value = "/getItemByItemStatusAndTaskId")
    @ApiOperation(httpMethod = "POST",value = "根据任务子项对应的任务Id和状态查询任务子项")
    public Wrapper<List<ImcInspectionItem>> getItemByItemStatusAndTaskId(@ApiParam(name = "getItemByItemStatusAndTaskId",value = "根据任务子项对应的任务Id和状态查询任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByItemStatusAndTaskId(itemQueryDto));
    }

    @PostMapping(value = "/getItemByUserId")
    @ApiOperation(httpMethod = "POST",value = "根据甲方用户的id查询对应的巡检任务子项")
    public Wrapper<List<ImcInspectionItem>> getItemByUserId(@ApiParam(name = "getItemByUserId",value = "根据甲方用户的ID查询巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByUserId(itemQueryDto));
    }

    @PostMapping(value = "/getItemListByUserId")
    @ApiOperation(httpMethod = "POST",value = "根据甲方用户的id查询对应的巡检任务子项（可返回总数total）")
    public Wrapper<PageInfo> getItemListByUserId(@ApiParam(name = "getItemByUserId",value = "根据甲方用户的ID查询巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByUserIdAndPage(itemQueryDto));
    }

    @PostMapping(value = "/getItemByUserIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据甲方用户id查询指定状态的巡检任务子项")
    public Wrapper<List<ImcInspectionItem>> getItemByUserIdAndStatus(@ApiParam(name = "getItemByUserIdAndStatus",value = "根据甲方用户id查询指定状态的巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByUserIdAndStatus(itemQueryDto));
    }

    @PostMapping(value = "/getItemListByUserIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "根据甲方用户id查询指定状态的巡检任务子项（可返回总数total）")
    public Wrapper<PageInfo> getItemListByUserIdAndStatus(@ApiParam(name = "getItemByUserIdAndStatus",value = "根据甲方用户id查询指定状态的巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByUserIdAndStatusAndPage(itemQueryDto));
    }

    @PostMapping(value = "/getItemByMaintainerId")
    @ApiOperation(httpMethod = "POST",value = "查询工程师下的全部巡检任务子项")
    public Wrapper<List<ImcInspectionItem>> getItemBymaintainerId(@ApiParam(name = "getItemByMaintainerId",value = "查询工程师下的全部巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByMaintainerId(itemQueryDto));
    }

    @PostMapping(value = "/getItemListByMaintainerId")
    @ApiOperation(httpMethod = "POST",value = "查询工程师下的全部巡检任务子项（可返回总数total）")
    public Wrapper<PageInfo> getItemListBymaintainerId(@ApiParam(name = "getItemByMaintainerId",value = "查询工程师下的全部巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByMaintainerIdAndPage(itemQueryDto));
    }

    @PostMapping(value = "/getItemByMaintainerIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "查询工程师下指定状态的全部巡检任务子项")
    public Wrapper<List<ImcInspectionItem>> getItemBymaintainerIdAndStatus(@ApiParam(name = "getItemByMaintainerIdAndStatus",value = "查询工程师下指定状态的全部巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByMaintainerIdAndStatus(itemQueryDto));
    }

    @PostMapping(value = "/getItemListByMaintainerIdAndStatus")
    @ApiOperation(httpMethod = "POST",value = "查询工程师下指定状态的全部巡检任务子项（可返回总数total）")
    public Wrapper<PageInfo> getItemListBymaintainerIdAndStatus(@ApiParam(name = "getItemByMaintainerIdAndStatus",value = "查询工程师下指定状态的全部巡检任务子项")@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getItemByMaintainerIdAndStatusAndPage(itemQueryDto));
    }

    @PostMapping(value = "/modifyMaintainerIdByItemId")
    @ApiOperation(httpMethod = "POST",value = "修改巡检任务子项对应的工程师")
    @AnanLogAnnotation
    public Wrapper<ItemChangeMaintainerDto> modifyMaintainerByItemId(@ApiParam(name = "modifyMaintainerByItemId",value = "修改巡检任务子项对应的工程师ID")@RequestBody ItemChangeMaintainerDto itemChangeMaintainerDto) {
        return WrapMapper.ok(imcInspectionItemService.modifyMaintainerIdByItemId(itemChangeMaintainerDto));
    }

    @PostMapping(value = "/sendMdmcRequest")
    @ApiOperation(httpMethod = "POST",value = "向mdmc发出维修维护申请")
    public Wrapper<Integer> sendMdmcRequest(@ApiParam(name = "sendMdmcRequest",value = "向mdmc发出维修维护申请")@RequestBody Integer integer){
        return null;
    }

    @PostMapping(value = "/createMdmcTask")
    @ApiOperation(httpMethod = "POST",value = "创建一条维修维护任务申请")
    public Wrapper createMdmcTask(@RequestBody MdmcFeignTaskDto mdmcFeignTaskDto){
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        mdmcFeignTaskDto.setLoginAuthDto(loginAuthDto);
        return WrapMapper.ok(mdmcTaskFeignApi.saveTask(mdmcFeignTaskDto));
    }

    @PostMapping(value = "/refuseItemByMaintainer")
    @ApiOperation(httpMethod = "POST",value = "工程师拒单")
    @AnanLogAnnotation
    public Wrapper<ImcItemChangeStatusDto> refuseItemByMaintainer(@RequestBody ConfirmImcItemDto confirmImcItemDto){
        confirmImcItemDto.setLoginAuthDto(getLoginAuthDto());
        return WrapMapper.ok(imcInspectionItemService.refuseImcItemByItemId(confirmImcItemDto));
    }

    @PostMapping(value = "/acceptItemByMaintainer")
    @ApiOperation(httpMethod = "POST",value = "工程师接单")
    @AnanLogAnnotation
    public Wrapper<ImcItemChangeStatusDto> acceptItemByMaintainer(@RequestBody ConfirmImcItemDto confirmImcItemDto){
        confirmImcItemDto.setLoginAuthDto(getLoginAuthDto());
        return WrapMapper.ok(imcInspectionItemService.acceptImcItemByItemId(confirmImcItemDto));
    }

    @PostMapping(value = "/getAllAcceptedItemByMaintainer")
    @ApiOperation(httpMethod = "POST",value = "获取工程师下面的全部已接单但是未完成的巡检任务子项")
    public Wrapper<List<ImcInspectionItem>> getAllAcceptedItemByMaintainer(@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAcceptedItemOfMaintainer(itemQueryDto));
    }

    @PostMapping(value = "/getAllAcceptedItemListByMaintainer")
    @ApiOperation(httpMethod = "POST",value = "获取工程师下面的全部已接单但是未完成的巡检任务子项（可返回总数total）")
    public Wrapper<PageInfo> getAllAcceptedItemListByMaintainer(@RequestBody ItemQueryDto itemQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAcceptedItemOfMaintainerAndPage(itemQueryDto));
    }

    @PostMapping(consumes = "multipart/form-data", value = "/uploadImcItemPicture")
    @ApiOperation(httpMethod = "POST", value = "巡检任务子项上传文件")
    public List<OptUploadFileRespDto> uploadImcItemPicture(HttpServletRequest request, OptUploadFileReqDto optUploadFileReqDto) {
        logger.info("uploadCompanyPicture - 上传文件. optUploadFileReqDto={}", optUploadFileReqDto);
        String fileType = optUploadFileReqDto.getFileType();
        String bucketName = optUploadFileReqDto.getBucketName();
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileType), "文件类型为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(bucketName), "存储地址为空");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return imcInspectionItemService.uploadImcItemFile(multipartRequest, optUploadFileReqDto, getLoginAuthDto());
    }

    @PostMapping(value = "/getImcPicListByTaskAndItemAndStatus")
    @ApiOperation(httpMethod = "POST", value = "巡检任务子项查询文件")
    public Wrapper<List<ElementImgUrlDto>> getImcPicListByTaskAndItemAndStatus(@RequestBody ImcPicQueryDto imcPicQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getImcItemFileList(imcPicQueryDto));
    }

    @PostMapping(value = "/getAllImcItemPics")
    @ApiOperation(httpMethod = "POST", value = "巡检任务子项查询全部子项状态下的文件")
    public Wrapper<List<ImcItemUrlDto>> getAllImcItemPics(@RequestBody ImcPicQueryDto imcPicQueryDto){
        return WrapMapper.ok(imcInspectionItemService.getAllImcItemPicList(imcPicQueryDto));
    }

    @PostMapping(value = "/getImcItemNumberByTaskId/{taskId}")
    @ApiOperation(httpMethod = "POST",value = "根据任务Id查询对应的子项的数目")
    public Wrapper<Integer> getImcItemNumberByTaskId(@PathVariable Long taskId){
        logger.info("根据任务Id查询对应的子项的数目 taskId={}",taskId);
        return WrapMapper.ok(imcInspectionItemService.getImcItemNumberByTaskId(taskId));
    }

    @PostMapping(value = "/getAllFinishedImcItemByMaintainerId")
    @ApiOperation(httpMethod = "POST",value = "根据维修工id查全部维修工已完成的任务")
    public Wrapper<PageInfo> getAllFinishedImcItemByMaintainerId(@RequestBody ItemQueryDto itemQueryDto){
        logger.info("根据维修工id查全部维修工已完成的任务 itemQueryDto={}",itemQueryDto);
        return WrapMapper.ok(imcInspectionItemService.getAllFinishedItemByMaintainerId(itemQueryDto));
    }
}
