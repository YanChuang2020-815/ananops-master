package com.ananops.provider.service;


import com.ananops.base.dto.LoginAuthDto;
import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.ImcInspectionItem;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptUploadFileReqDto;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * Created by rongshuai on 2019/11/28 10:13
 */
public interface ImcInspectionItemService extends IService<ImcInspectionItem> {
    ImcAddInspectionItemDto saveInspectionItem(ImcAddInspectionItemDto imcAddInspectionItemDto, LoginAuthDto loginAuthDto);//新增一条巡检子项记录

    List<ImcInspectionItem> getAllItemByTaskId(ItemQueryDto itemQueryDto);//根据巡检任务ID，获取当前任务下的所有巡检任务子项

    void deleteItemByItemId(Long itemId);

    List<ImcInspectionItem> getAllItemByTaskIdAndStatus(ItemQueryDto itemQueryDto);

    ImcInspectionItem getItemByItemId(Long itemId);//根据巡检任务子项ID，获取对应的巡检任务子项

    List<ImcInspectionItem> getItemByItemStatusAndTaskId(ItemQueryDto itemQueryDto);//根据任务子项对应的任务ID以及任务状态查询对应的任务

    List<ImcInspectionItem> getItemByUserId(ItemQueryDto itemQueryDto);//根据甲方用户Id查询巡检任务子项

    List<ImcInspectionItem> getItemByUserIdAndStatus(ItemQueryDto itemQueryDto);//根据甲方用户Id查询指定状态的巡检任务子项

    List<ImcInspectionItem> getItemByMaintainerId(ItemQueryDto itemQueryDto);//根据工程师的Id查询对应的巡检任务

    List<ImcInspectionItem> getItemByMaintainerIdAndStatus(ItemQueryDto itemQueryDto);//根据工程师的Id查询指定状态的巡检任务

    ItemChangeMaintainerDto modifyMaintainerIdByItemId(ItemChangeMaintainerDto itemChangeMaintainerDto);//修改巡检任务子项对应的维修工ID

    ImcItemChangeStatusDto refuseImcItemByItemId(ConfirmImcItemDto confirmImcItemDto);//工程师拒单

    ImcItemChangeStatusDto modifyImcItemStatusByItemId(ImcItemChangeStatusDto imcItemChangeStatusDto);//修改任务子项的状态

    ImcItemChangeStatusDto acceptImcItemByItemId(ConfirmImcItemDto confirmImcItemDto);//工程师接单

    List<ImcInspectionItem> getAcceptedItemOfMaintainer(ItemQueryDto itemQueryDto);//获取工程下的全部已接单且未完成巡检任务子项

    List<OptUploadFileRespDto> uploadImcItemFile(MultipartHttpServletRequest multipartRequest, OptUploadFileReqDto optUploadFileReqDto, LoginAuthDto loginAuthDto);

    List<ElementImgUrlDto> getImcItemFileList(ImcPicQueryDto imcPicQueryDto);

    List<ImcItemUrlDto> getAllImcItemPicList(ImcPicQueryDto imcPicQueryDto);

    Integer getImcItemNumberByTaskId(Long taskId);

    PageInfo getAllItemByTaskIdAndPage(ItemQueryDto itemQueryDto);

    PageInfo getAllItemByTaskIdAndStatusAndPage(ItemQueryDto itemQueryDto);

    PageInfo getItemByUserIdAndPage(ItemQueryDto itemQueryDto);

    PageInfo getItemByUserIdAndStatusAndPage(ItemQueryDto itemQueryDto);

    PageInfo getItemByMaintainerIdAndPage(ItemQueryDto itemQueryDto);

    PageInfo getItemByMaintainerIdAndStatusAndPage(ItemQueryDto itemQueryDto);

    PageInfo getAcceptedItemOfMaintainerAndPage(ItemQueryDto itemQueryDto);

    PageInfo getAllFinishedItemByMaintainerId(ItemQueryDto itemQueryDto);

    /**
     * 与getItemByItemId接口逻辑一致，只是封装了工程师名称
     *
     * @param itemId 子项Id
     *
     * @return 包装对象
     */
    ImcInspectionItemDto getItemDtoByItemId(Long itemId);//根据巡检任务子项ID，获取对应的巡检任务子项(封装工程师名称返回)

    /**
     * 处理单据相关关联信息
     *
     * @param itemChangeMaintainerDto
     */
    void handleInvoice(ItemChangeMaintainerDto itemChangeMaintainerDto);

    /**
     * 提交巡检结果相关信息
     *
     * @param itemResultDto
     *
     * @param loginAuthDto
     *
     * @return
     */
    ImcItemChangeStatusDto putResultByItemId(ItemResultDto itemResultDto, LoginAuthDto loginAuthDto);
}
