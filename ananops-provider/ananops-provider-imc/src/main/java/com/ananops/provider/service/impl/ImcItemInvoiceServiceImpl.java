package com.ananops.provider.service.impl;

import com.ananops.PublicUtil;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.core.support.BaseService;
import com.ananops.provider.mapper.*;
import com.ananops.provider.model.domain.*;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.oss.ElementImgUrlDto;
import com.ananops.provider.model.dto.oss.OptBatchGetUrlRequest;
import com.ananops.provider.model.dto.oss.OptUploadFileRespDto;
import com.ananops.provider.service.ImcItemInvoiceService;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.utils.PdfUtil;
import com.ananops.provider.utils.WaterMark;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020/4/21 下午8:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImcItemInvoiceServiceImpl extends BaseService<ImcItemInvoice> implements ImcItemInvoiceService {

    @Resource
    private ImcItemInvoiceMapper imcItemInvoiceMapper;

    @Resource
    private ImcItemInvoiceDeviceMapper imcItemInvoiceDeviceMapper;

    @Resource
    private ImcItemInvoiceDescMapper imcItemInvoiceDescMapper;

    @Resource
    private ImcInspectionItemMapper imcInspectionItemMapper;

    @Resource
    private ImcInspectionTaskServiceImpl imcInspectionTaskService;

    @Resource
    private ImcTaskReportMapper imcTaskReportMapper;

    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Override
    public List<ImcItemInvoice> queryInvoiceList(ImcInvoiceQueryDto imcInvoiceQueryDto, LoginAuthDto loginAuthDto) {
        if (imcInvoiceQueryDto.getItemId() == null) {
            throw new BusinessException(ErrorCodeEnum.IMC10090002);
        }
        if (imcInvoiceQueryDto.getStatus() == null) {
            throw new BusinessException(ErrorCodeEnum.IMC10090009);
        }
        Example example = new Example(ImcItemInvoice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspcItemId",imcInvoiceQueryDto.getItemId());
        criteria.andEqualTo("status",imcInvoiceQueryDto.getStatus());
        return imcItemInvoiceMapper.selectByExample(example);
    }

    @Override
    public FormDataDto queryDetailsById(Long invoiceId) {
        FormDataDto formDataDto = new FormDataDto();
        ImcItemInvoice imcItemInvoice = imcItemInvoiceMapper.selectByPrimaryKey(invoiceId);
        if (imcItemInvoice == null) {
            throw new BusinessException(ErrorCodeEnum.IMC10090010, invoiceId);
        }
        BeanUtils.copyProperties(imcItemInvoice,formDataDto);
        InspcFeedBack inspcFeedBack = new InspcFeedBack();
        BeanUtils.copyProperties(imcItemInvoice,inspcFeedBack);
        formDataDto.setFeedback(inspcFeedBack);
        // 回显设备列表项
        Example example = new Example(ImcItemInvoiceDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("invoiceId",invoiceId);
        example.setOrderByClause("`sort` ASC");
        List<ImcItemInvoiceDevice> imcItemInvoiceDevices = imcItemInvoiceDeviceMapper.selectByExample(example);
        if (imcItemInvoiceDevices != null) {
            List<DeviceDesc> assetList = new ArrayList<>();
            for (ImcItemInvoiceDevice imcItemInvoiceDevice : imcItemInvoiceDevices) {
                DeviceDesc deviceDesc = new DeviceDesc();
                BeanUtils.copyProperties(imcItemInvoiceDevice,deviceDesc);
                assetList.add(deviceDesc);
            }
            formDataDto.setAssetList(assetList);
        }
        // 回显巡检内容列表项
        Example example1 = new Example(ImcItemInvoiceDesc.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("invoiceId",invoiceId);
        example1.setOrderByClause("`sort` ASC");
        List<ImcItemInvoiceDesc> imcItemInvoiceDescs = imcItemInvoiceDescMapper.selectByExample(example1);
        if (imcItemInvoiceDescs != null) {
            List<InspcDetail> inspcDetailList = new ArrayList<>();
            for (ImcItemInvoiceDesc imcItemInvoiceDesc : imcItemInvoiceDescs) {
                InspcDetail inspcDetail = new InspcDetail();
                BeanUtils.copyProperties(imcItemInvoiceDesc,inspcDetail);
                inspcDetailList.add(inspcDetail);
            }
            formDataDto.setInspcDetailList(inspcDetailList);
        }
        return formDataDto;
    }

    @Override
    public Integer saveData(FormDataDto formDataDto, LoginAuthDto loginAuthDto) {
        // 更新巡检单详情
        ImcItemInvoice imcItemInvoice = new ImcItemInvoice();
        BeanUtils.copyProperties(formDataDto.getFeedback(),imcItemInvoice);
        BeanUtils.copyProperties(formDataDto,imcItemInvoice);
        imcItemInvoice.setUpdateInfo(loginAuthDto);
        // 该巡检单已填写
        imcItemInvoice.setStatus("Y");
        imcItemInvoiceMapper.updateByPrimaryKeySelective(imcItemInvoice);
        // 更新资产列表详情
        List<DeviceDesc> assetList = formDataDto.getAssetList();
        if (assetList != null) {
            for (DeviceDesc deviceDesc : assetList) {
                ImcItemInvoiceDevice imcItemInvoiceDevice = new ImcItemInvoiceDevice();
                BeanUtils.copyProperties(deviceDesc,imcItemInvoiceDevice);
                imcItemInvoiceDevice.setUpdateInfo(loginAuthDto);
                imcItemInvoiceDeviceMapper.updateByPrimaryKeySelective(imcItemInvoiceDevice);
            }
        }
        // 更新巡检详情列表
        List<InspcDetail> inspcDetailList = formDataDto.getInspcDetailList();
        if (inspcDetailList != null) {
            for (InspcDetail inspcDetail : inspcDetailList) {
                ImcItemInvoiceDesc imcItemInvoiceDesc = new ImcItemInvoiceDesc();
                BeanUtils.copyProperties(inspcDetail,imcItemInvoiceDesc);
                imcItemInvoiceDesc.setUpdateInfo(loginAuthDto);
                if (PublicUtil.isEmpty(imcItemInvoiceDesc.getItemResult())) {
                    imcItemInvoiceDesc.setItemResult("--");
                }
                imcItemInvoiceDescMapper.updateByPrimaryKeySelective(imcItemInvoiceDesc);
            }
        }
        // 更新巡检子项中巡检单的结果
        ImcItemInvoice queryInvoice = imcItemInvoiceMapper.selectByPrimaryKey(formDataDto.getId());
        Long inspecItemId = queryInvoice.getInspcItemId();
        ImcItemInvoice query = new ImcItemInvoice();
        query.setInspcItemId(inspecItemId);
        query.setStatus("N");
        int count = imcItemInvoiceMapper.selectCount(query);
        ImcInspectionItem imcInspectionItem = new ImcInspectionItem();
        imcInspectionItem.setId(inspecItemId);
        if (count == 0) {
            imcInspectionItem.setResult("finish");
            // 生成巡检单预览文本。
            buildPreview(inspecItemId, loginAuthDto);
        } else {
            imcInspectionItem.setResult(String.valueOf(count));
        }
        imcInspectionItemMapper.updateByPrimaryKeySelective(imcInspectionItem);
        return null;
    }

    private OptUploadFileRespDto buildPreview(Long inspecItemId, LoginAuthDto loginAuthDto) {
        ImcInspectionItem inspectionItem = imcInspectionItemMapper.selectByPrimaryKey(inspecItemId);
        if (inspectionItem == null) {
            throw new BusinessException(ErrorCodeEnum.IMC10090011, inspecItemId);
        }
        ImcItemInvoice query = new ImcItemInvoice();
        query.setInspcItemId(inspecItemId);
        query.setStatus("Y");
        List<ImcItemInvoice> invoices = imcItemInvoiceMapper.select(query);

        List<ItemInvoiceAllInfo> invoiceInfoList = new ArrayList<>();
        for (ImcItemInvoice imcItemInvoice : invoices) {
            ItemInvoiceAllInfo invoiceAllInfo = new ItemInvoiceAllInfo();
            invoiceAllInfo.setInvoice(imcItemInvoice);
            invoiceAllInfo.setImcInspectionItem(inspectionItem);
            // 回显设备列表项
            Example example = new Example(ImcItemInvoiceDevice.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("invoiceId",imcItemInvoice.getId());
            example.setOrderByClause("`sort` ASC");
            List<ImcItemInvoiceDevice> imcItemInvoiceDevices = imcItemInvoiceDeviceMapper.selectByExample(example);
            if (imcItemInvoiceDevices != null) {
                invoiceAllInfo.setAssetList(imcItemInvoiceDevices);
            }
            // 回显巡检内容列表项
            Example example1 = new Example(ImcItemInvoiceDesc.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("invoiceId",imcItemInvoice.getId());
            example1.setOrderByClause("`sort` ASC");
            List<ImcItemInvoiceDesc> imcItemInvoiceDescs = imcItemInvoiceDescMapper.selectByExample(example1);
            if (imcItemInvoiceDescs != null) {
                invoiceAllInfo.setInspcDetailList(imcItemInvoiceDescs);
            }
            invoiceInfoList.add(invoiceAllInfo);
        }
        return itemInvoicePdf(invoiceInfoList, loginAuthDto);
    }

    private OptUploadFileRespDto itemInvoicePdf(List<ItemInvoiceAllInfo> invoiceList, LoginAuthDto loginAuthDto){
        if (PublicUtil.isEmpty(invoiceList)) {
            throw new BusinessException(ErrorCodeEnum.GL99990007);
        }
        OptUploadFileRespDto optUploadFileRespDto = new OptUploadFileRespDto();
        ItemInvoiceAllInfo oneInvoiceAllInfo = invoiceList.get(0);
        logger.info("invoiceList={}",invoiceList);
        //创建文档对象
        Document document = new Document(PageSize.A4);
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document,out);

            writer.setPageEvent(new WaterMark("安安运维"));// 水印

            document.open();
            document.addTitle("系统维护保养巡检记录");
            //基本文字格式
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titlefont = new Font(bfChinese, 16, Font.BOLD);
            Font headfont = new Font(bfChinese, 14, Font.BOLD);
            Font keyfont = new Font(bfChinese, 12, Font.BOLD);
            Font textfont = new Font(bfChinese, 12, Font.NORMAL);

            //日期转化工具
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 添加图片
            Image image = Image.getInstance("classpath:/static/Logo.png");
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(20); //依照比例缩放

            for (ItemInvoiceAllInfo invoiceAllInfo : invoiceList) {
                ImcItemInvoice invoice = invoiceAllInfo.getInvoice();
                // 添加公司Logo
                document.add(image);
                // 增添系统维护保养巡检记录表
                PdfPTable table = PdfUtil.createTable(4,10);

                table.addCell(PdfUtil.createCell("唯一编号："+invoice.getId(),headfont, Element.ALIGN_LEFT, 6, false));

                table.addCell(PdfUtil.createCell(invoiceAllInfo.getImcInspectionItem().getItemName() + "-维护保养巡检记录表",headfont, Element.ALIGN_CENTER, 4, 50f));

                table.addCell(PdfUtil.createCell("点位名称", keyfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell(invoice.getPointName(), textfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell("点位地址", keyfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell(invoice.getPointAddress(), textfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell("巡检单位", keyfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell(invoice.getInspcCompany(), textfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell("设备统计", keyfont, Element.ALIGN_CENTER, 1, invoiceAllInfo.getAssetList().size()));

                for (ImcItemInvoiceDevice imcItemInvoiceDevice : invoiceAllInfo.getAssetList()) {
                    table.addCell(PdfUtil.createCell(imcItemInvoiceDevice.getDevice(), textfont, Element.ALIGN_CENTER, 3));
                }

                table.addCell(PdfUtil.createCell("常规巡检内容", keyfont, Element.ALIGN_CENTER, 2));

                table.addCell(PdfUtil.createCell("本次巡检情况", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell("处理结果", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell("巡检详情", keyfont, Element.ALIGN_CENTER, 1, invoiceAllInfo.getInspcDetailList().size()));

                for (ImcItemInvoiceDesc imcItemInvoiceDesc : invoiceAllInfo.getInspcDetailList()) {
                    table.addCell(PdfUtil.createCell(imcItemInvoiceDesc.getItemContent(), textfont, Element.ALIGN_CENTER, 1));
                    table.addCell(PdfUtil.createCell("Y".equals(imcItemInvoiceDesc.getItemState()) ? "正常":"异常", textfont, Element.ALIGN_CENTER, 1));
                    table.addCell(PdfUtil.createCell(imcItemInvoiceDesc.getItemResult()==null ? "--":imcItemInvoiceDesc.getItemResult(), textfont, Element.ALIGN_CENTER, 1));
                }

                table.addCell(PdfUtil.createCell("巡检结论", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell(invoice.getInspcResult(), textfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell("巡检日期", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell(formatter.format(invoice.getInspcDate()), textfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell("用户确认", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell(invoice.getUserConfirm()==null ? "--":invoice.getUserConfirm(), textfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell("工程师", keyfont, Element.ALIGN_CENTER, 1));

                table.addCell(PdfUtil.createCell(invoice.getEngineer(), textfont, Element.ALIGN_CENTER, 1));

                table.setSpacingAfter(10f);
                document.add(table);
                document.newPage();
            }

            document.close();

            String filename = "维护保养巡检记录表-" + oneInvoiceAllInfo.getImcInspectionItem().getId() + ".pdf";

            // 这里用itemId复用了TaskId
            optUploadFileRespDto = imcInspectionTaskService.uploadReportPdf(out,filename,"pdf",loginAuthDto,oneInvoiceAllInfo.getImcInspectionItem().getId());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            document.close();
        }
        return optUploadFileRespDto;
    }

    @Override
    public List<ElementImgUrlDto> getInvoicePreview(Long itemId, LoginAuthDto loginAuthDto) {
        ImcTaskReport imcTaskReport = imcTaskReportMapper.selectByPrimaryKey(itemId);
        if(null == imcTaskReport){
            buildPreview(itemId, loginAuthDto);
            imcTaskReport = imcTaskReportMapper.selectByPrimaryKey(itemId);
        }
        String refNo = imcTaskReport.getRefNo();
        OptBatchGetUrlRequest optBatchGetUrlRequest = new OptBatchGetUrlRequest();
        optBatchGetUrlRequest.setRefNo(refNo);
        optBatchGetUrlRequest.setEncrypt(true);
        return opcOssFeignApi.listFileUrl(optBatchGetUrlRequest).getResult();
    }

    @Override
    public void handleUserConfirm(Long itemId, LoginAuthDto loginAuthDto) {
        Example example = new Example(ImcItemInvoice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inspcItemId",itemId);
        ImcItemInvoice update = new ImcItemInvoice();
        update.setUserConfirm(loginAuthDto.getUserName());
        update.setLastOperatorId(loginAuthDto.getUserId());
        update.setLastOperator(loginAuthDto.getUserName() == null ? loginAuthDto.getLoginName() : loginAuthDto.getUserName());
        update.setUpdateTime(new Date());
        imcItemInvoiceMapper.updateByExampleSelective(update,example);
    }
}
