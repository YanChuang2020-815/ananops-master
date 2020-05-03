package com.ananops.provider.service.impl;


import com.ananops.base.dto.BaseQuery;
import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseService;
import com.ananops.provider.exception.PmcBizException;
import com.ananops.provider.mapper.PmcContractMapper;
import com.ananops.provider.mapper.PmcProjectMapper;
import com.ananops.provider.mapper.PmcProjectUserMapper;
import com.ananops.provider.model.domain.PmcContract;
import com.ananops.provider.model.domain.PmcProject;
import com.ananops.provider.model.domain.PmcProjectUser;
import com.ananops.provider.model.dto.PmcProReqQueryDto;
import com.ananops.provider.model.dto.PmcProjectDto;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.vo.GroupZtreeVo;
import com.ananops.provider.service.PmcInspectTaskService;
import com.ananops.provider.service.PmcProjectService;
import com.ananops.wrapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By ChengHao On 2019/12/4
 */
@Service
@Transactional
public class PmcProjectServiceImpl extends BaseService<PmcProject> implements PmcProjectService {
    @Resource
    PmcProjectMapper pmcProjectMapper;
    @Resource
    PmcContractMapper pmcContractMapper;
    @Resource
    PmcProjectUserMapper pmcProjectUserMapper;
    @Resource
    PmcInspectTaskService pmcInspectTaskService;
    @Resource
    private UacGroupFeignApi uacGroupFeignApi;



    @Override
    public int saveProject(PmcProject pmcProject, LoginAuthDto loginAuthDto) {
        int result = 0;
        pmcProject.setUpdateInfo(loginAuthDto);
        if (pmcProject.isNew() && pmcProject.getIsContract() == 1) { //新增项目,有合同
            pmcProject.setId(super.generateId());
            PmcContract pmcContract = pmcContractMapper.selectByPrimaryKey(pmcProject.getContractId());
            if (pmcContract == null) {
                throw new PmcBizException(ErrorCodeEnum.PMC10081003, pmcProject.getContractId());
            }
            //添加甲方、乙方信息
            pmcProject.setPartyAId(pmcContract.getPartyAId());
            pmcProject.setPartyAName(pmcContract.getPartyAName());
            pmcProject.setPartyBId(pmcContract.getPartyBId());
            pmcProject.setPartyBName(pmcContract.getPartyBName());

            result = pmcProjectMapper.insertSelective(pmcProject);
            //添加进关系表
            PmcProjectUser pmcProjectUser = new PmcProjectUser();
            pmcProjectUser.setProjectId(pmcProject.getId());
            pmcProjectUser.setUserId(loginAuthDto.getUserId());
            pmcProjectUserMapper.insertSelective(pmcProjectUser);

        } else if (pmcProject.isNew()) {  //虚拟项目
            pmcProject.setId(super.generateId());
            pmcProjectMapper.insertSelective(pmcProject);
            //添加进关系表
            PmcProjectUser pmcProjectUser = new PmcProjectUser();
            pmcProjectUser.setProjectId(pmcProject.getId());
            pmcProjectUser.setUserId(loginAuthDto.getUserId());
            pmcProjectUserMapper.insertSelective(pmcProjectUser);
        } else {                    //更新项目信息
            result = pmcProjectMapper.updateByPrimaryKeySelective(pmcProject);
            if (result < 1) {
                throw new PmcBizException(ErrorCodeEnum.PMC10081001, pmcProject.getId());
            }
        }
        return result;
    }

    @Override
    public PmcProject getProjectById(Long projectId) {
        PmcProject pmcProject = pmcProjectMapper.selectByPrimaryKey(projectId);
        return pmcProject;
    }

    @Override
    public List<PmcProject> getProjectListByGroupId(Long groupId) {
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        //公司ID
        groupId = companyDto.getId();
        Example example = new Example(PmcProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("partyAId", groupId);
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andEqualTo("partyBId", groupId);
        example.or(criteria2);
        example.setOrderByClause("created_time desc");
        List<PmcProject> pmcProjectList = pmcProjectMapper.selectByExample(example);
        return pmcProjectList;
    }

    @Override
    public PageInfo getProjectListWithPage(BaseQuery baseQuery) {
        PageHelper.startPage(baseQuery.getPageNum(), baseQuery.getPageSize());
        List<PmcProject> pmcProjectList = pmcProjectMapper.selectAll();
        return new PageInfo<>(pmcProjectList);
    }

    @Override
    public List<PmcProject> getProjectByUserId(Long userId) {
        return pmcProjectMapper.getProjectByUserId(userId);
    }

    @Override
    public List<PmcProject> getProjectByContractId(Long contractId) {
        return pmcProjectMapper.getProjectByContractId(contractId);
    }

    @Override
    public void deleteProjectById(Long projectId) {
        if (pmcInspectTaskService.getTasksByProjectId(projectId) != null) {
            pmcInspectTaskService.deleteTaskByProjectId(projectId); //删除级联的巡检任务
        }
        Example example = new Example(PmcProjectUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        if(pmcProjectUserMapper.selectByExample(example)!=null){ //删除级联的项目用户关系表
            this.deleteProUser(projectId);
        }
        Integer result = pmcProjectMapper.deleteByPrimaryKey(projectId);
        if (result < 1) {
            throw new PmcBizException(ErrorCodeEnum.PMC10081002, projectId);
        }
    }

    @Override
    public List<PmcProjectUser> queryProUserByProjectId(Long projectId){
        Example example = new Example(PmcProjectUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        return pmcProjectUserMapper.selectByExample(example);
    }

    @Override
    public int addProUser(PmcProjectUser pmcProjectUser) {
        int result = 0;
        result = pmcProjectUserMapper.insertSelective(pmcProjectUser);
        return result;
    }

    @Override
    public int deleteProUser(Long projectId) {
        int result = 0;
        Example example = new Example(PmcProjectUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        result = pmcProjectUserMapper.deleteByExample(example);
        return result;
    }

    @Override
    public int deleteProUser2(PmcProjectUser pmcProjectUser) {
        int result = 0;
        Long projectId = pmcProjectUser.getProjectId();
        Long userId = pmcProjectUser.getUserId();
        Example example = new Example(PmcProjectUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        criteria.andEqualTo("userId", userId);
        result = pmcProjectUserMapper.deleteByExample(example);
        return result;
    }

    @Override
    public List<Long> getEngineersIdByProjectId(Long projectId) {
        List<Long> engineersId = new ArrayList<>();
        Example example = new Example(PmcProjectUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        List<PmcProjectUser> pmcProjectUserList =  pmcProjectUserMapper.selectByExample(example);
        for (PmcProjectUser pmcProjectUser : pmcProjectUserList) {
            engineersId.add(pmcProjectUser.getUserId());
        }
        return  engineersId;
    }

    @Override
    public int getProjectCount(Long groupId) {
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(groupId).getResult();
        //公司ID
        groupId = companyDto.getId();
        Example example = new Example(PmcProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("partyAId", groupId);
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andEqualTo("partyBId", groupId);
        example.or(criteria2);
        return pmcProjectMapper.selectCountByExample(example);
    }

    @Override
    public List<PmcProject> getProjectList(PmcProReqQueryDto pmcProReqQueryDto) {
        if (pmcProReqQueryDto.getGroupId() == null) {
            throw new PmcBizException(ErrorCodeEnum.PMC10081026);
        }
        CompanyDto companyDto = uacGroupFeignApi.getCompanyInfoById(pmcProReqQueryDto.getGroupId()).getResult();
        //公司ID
        Long groupId = companyDto.getId();
        Example example = new Example(PmcProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("partyAId", groupId);
        if (pmcProReqQueryDto.getProjectType() != null) {
            criteria.andEqualTo("projectType", pmcProReqQueryDto.getProjectType());
        }
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andEqualTo("partyBId", groupId);
        if (pmcProReqQueryDto.getProjectType() != null) {
            criteria2.andEqualTo("projectType", pmcProReqQueryDto.getProjectType());
        }
        example.or(criteria2);
        example.setOrderByClause("created_time desc");
        List<PmcProject> pmcProjectList = pmcProjectMapper.selectByExample(example);
        return pmcProjectList;
    }
}
