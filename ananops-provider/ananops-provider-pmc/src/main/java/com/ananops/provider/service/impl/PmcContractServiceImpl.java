package com.ananops.provider.service.impl;

import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exception.PmcBizException;
import com.ananops.provider.mapper.PmcContractMapper;
import com.ananops.provider.mapper.PmcContractUserMapper;
import com.ananops.provider.mapper.PmcProjectMapper;
import com.ananops.provider.model.domain.PmcContract;
import com.ananops.provider.model.domain.PmcContractUser;
import com.ananops.provider.model.domain.PmcProject;
import com.ananops.provider.model.dto.attachment.OptAttachmentUpdateReqDto;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.GroupZtreeVo;
import com.ananops.provider.service.OpcOssFeignApi;
import com.ananops.provider.service.PmcContractService;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By ChengHao On 2019/12/5
 */
@Slf4j
@Service
public class PmcContractServiceImpl extends BaseService<PmcContract> implements PmcContractService {

    @Resource
    private PmcContractMapper pmcContractMapper;

    @Resource
    private PmcProjectMapper pmcProjectMapper;

    @Resource
    private PmcContractUserMapper pmcContractUserMapper;

    @Resource
    private OpcOssFeignApi opcOssFeignApi;

    @Resource
    private UacGroupFeignApi uacGroupFeignApi;

    @Override
    public void saveContract(PmcContract pmcContract, LoginAuthDto loginAuthDto) {
        pmcContract.setUpdateInfo(loginAuthDto);
        if (pmcContract.isNew()) {  //新增合同信息
            pmcContract.setId(super.generateId());
            pmcContractMapper.insertSelective(pmcContract);
            //添加进关系表
            PmcContractUser pmcContractUser = new PmcContractUser();
            pmcContractUser.setContractId(pmcContract.getId());
            pmcContractUser.setUserId(loginAuthDto.getUserId());
            pmcContractUserMapper.insertSelective(pmcContractUser);
        }else {                   //更新合同信息
            Integer result = pmcContractMapper.updateByPrimaryKeySelective(pmcContract);
            if (result<1){
                throw new PmcBizException(ErrorCodeEnum.PMC10081011,pmcContract.getId());
            }
        }
        // 更新附件信息
        List<Long> attachmentIds = new ArrayList<>();
        if (pmcContract.getFilePath()!=null) {
            String contractFileIds = pmcContract.getFilePath();
            if (contractFileIds.contains(",")) {
                String[] legalIds = contractFileIds.split(",");
                for (String id : legalIds) {
                    attachmentIds.add(Long.parseLong(id));
                }
            }else {
                attachmentIds.add(Long.parseLong(contractFileIds));
            }
        }
        OptAttachmentUpdateReqDto optAttachmentUpdateReqDto = new OptAttachmentUpdateReqDto();
        optAttachmentUpdateReqDto.setAttachmentIds(attachmentIds);
        optAttachmentUpdateReqDto.setLoginAuthDto(loginAuthDto);
        optAttachmentUpdateReqDto.setRefNo(pmcContract.getId().toString());
        opcOssFeignApi.batchUpdateAttachment(optAttachmentUpdateReqDto);
    }

    @Override
    public PmcContract getContractById(Long id) {
        return pmcContractMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteContractById(Long id) {
        List<PmcProject> pmcProjects = pmcProjectMapper.getProjectByContractId(id);
        if (pmcProjects.size() > 0) {
            throw new PmcBizException(ErrorCodeEnum.PMC10081027, id);
        }
        Integer result = pmcContractMapper.deleteByPrimaryKey(id);
        if (result<1){
            throw new PmcBizException(ErrorCodeEnum.PMC10081012,id);
        }
    }

    @Override
    public List<PmcContract> getContactListByGroupId(Long groupId) {
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        //公司ID
        groupId = companyDto.getId();
        Example example = new Example(PmcContract.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andEqualTo("partyAId",groupId);
        Example.Criteria criteria2= example.createCriteria();
        criteria2.andEqualTo("partyBId",groupId);
        example.or(criteria2);
        example.setOrderByClause("created_time desc");
        List<PmcContract> pmcContractList = pmcContractMapper.selectByExample(example);
        return pmcContractList;
    }

    @Override
    public List<PmcContract> getContractListByLikePartyAName(String partyAName){
        partyAName = "%" + partyAName + "%";
        Example example = new Example(PmcContract.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andLike("partyAName",partyAName);
        List<PmcContract> pmcContractList = pmcContractMapper.selectByExample(example);
        return pmcContractList;
    }

    @Override
    public List<PmcContract> getContractListByLikePartyBName(String partyBName){
        partyBName = "%" + partyBName + "%";
        Example example = new Example(PmcContract.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andLike("partyBName",partyBName);
        List<PmcContract> pmcContractList = pmcContractMapper.selectByExample(example);
        return pmcContractList;
    }

    @Override
    public PageInfo getContractListWithPage(BaseQuery baseQuery) {
        //1. 设置分页
        PageHelper.startPage(baseQuery.getPageNum(),baseQuery.getPageSize());
        //2. 查询
        List<PmcContract> pmcContractList = pmcContractMapper.selectAll();
        //3. 返回
        return new PageInfo<>(pmcContractList);
    }

    @Override
    public List<PmcContract> getContactByAB(Long partyAId, Long partyBId) {
        Example example = new Example(PmcContract.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andEqualTo("partyAId",partyAId);
        criteria.andEqualTo("partyBId",partyBId);
        return pmcContractMapper.selectByExample(example);
    }

    @Override
    public int getContractCount(Long groupId) {
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        groupId = companyDto.getId();
        Example example = new Example(PmcContract.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andEqualTo("partyAId",groupId);
        Example.Criteria criteria2= example.createCriteria();
        criteria2.andEqualTo("partyBId",groupId);
        example.or(criteria2);
        return pmcContractMapper.selectCountByExample(example);
    }


}
