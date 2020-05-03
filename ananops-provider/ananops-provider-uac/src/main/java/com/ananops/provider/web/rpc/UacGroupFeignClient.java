package com.ananops.provider.web.rpc;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.core.support.BaseController;
import com.ananops.provider.mapper.UacGroupUserMapper;
import com.ananops.provider.model.domain.UacGroup;
import com.ananops.provider.model.domain.UacGroupUser;
import com.ananops.provider.model.dto.group.CompanyDto;
import com.ananops.provider.model.dto.group.GroupNameLikeQuery;
import com.ananops.provider.model.dto.group.GroupSaveDto;
import com.ananops.provider.model.dto.group.GroupStatusDto;
import com.ananops.provider.model.vo.GroupZtreeVo;
import com.ananops.provider.model.dto.user.IdStatusDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.service.UacGroupService;
import com.ananops.provider.service.UacUserService;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 操作Group
 *
 * Created by bingyueduan on 2019/12/29.
 */
@RestController
@Api(value = "API - UacGroupFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UacGroupFeignClient extends BaseController implements UacGroupFeignApi {

    @Resource
    private UacGroupUserMapper uacGroupUserMapper;

    @Resource
    private UacGroupService uacGroupService;

    @Resource
    private UacUserService uacUserService;

    @Override
    @ApiOperation(httpMethod = "POST", value = "编辑用户组")
    public Wrapper<Long> groupSave(@RequestBody GroupSaveDto groupSaveDto) {
        logger.info("groupSave - 注册或更新组织. groupSaveDto={}", groupSaveDto);
        UacGroup uacGroup = new UacGroup();
        try {
            BeanUtils.copyProperties(uacGroup, groupSaveDto);
        } catch (Exception e) {
            logger.error("用户组Dto与用户组状态传输Dto属性拷贝异常");
            e.printStackTrace();
        }
        uacGroupService.saveUacGroup(uacGroup, groupSaveDto.getLoginAuthDto());
        return WrapMapper.ok(groupSaveDto.getId());
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "修改Group状态")
    public Wrapper<Integer> modifyGroupStatus(@RequestBody IdStatusDto idStatusDto) {
        LoginAuthDto loginAuthDto = getLoginAuthDto();
        int result = uacGroupService.updateUacGroupStatusById(idStatusDto, loginAuthDto);
        return WrapMapper.ok(result);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "通过状态查询Group集合")
    public Wrapper<List<GroupSaveDto>> queryListByStatus(@RequestBody GroupStatusDto groupStatusDto) {
        List<GroupSaveDto> result = new ArrayList<>();
        UacGroup uacGroup = new UacGroup();
        try {
            BeanUtils.copyProperties(uacGroup, groupStatusDto);
        } catch (Exception e) {
            logger.error("用户组Dto与用户组状态传输Dto属性拷贝异常");
            e.printStackTrace();
        }
        List<UacGroup> groups = uacGroupService.select(uacGroup);
        for (UacGroup uacGroupT : groups) {
            GroupSaveDto groupSaveDto = new GroupSaveDto();
            try {
                BeanUtils.copyProperties(groupSaveDto, uacGroupT);
            } catch (Exception e) {
                logger.error("用户组Dto与用户组传输Dto属性拷贝异常");
                e.printStackTrace();
            }
            result.add(groupSaveDto);
        }
        return WrapMapper.ok(result);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "通过Id查询Group信息")
    public Wrapper<GroupSaveDto> getUacGroupById(@RequestParam("groupId") Long groupId) {
        logger.info("根据组织Id查询组织信息");
        GroupSaveDto groupSaveDto = new GroupSaveDto();
        UacGroup uacGroup = uacGroupService.queryById(groupId);
        if (uacGroup != null) {
            try {
                BeanUtils.copyProperties(groupSaveDto, uacGroup);
            } catch (Exception e) {
                logger.error("用户组Dto与用户组传输Dto属性拷贝异常");
                e.printStackTrace();
            }
        }
        return WrapMapper.ok(groupSaveDto);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "通过组织名称及类型模糊查询Group信息")
    public Wrapper<List<GroupSaveDto>> getUacGroupByLikeName(@RequestBody GroupNameLikeQuery groupNameLikeQuery) {
        logger.info("通过公司名称模糊查询Group信息");
        List<GroupSaveDto> groupSaveDtos = new ArrayList<>();
        Preconditions.checkArgument(!StringUtils.isEmpty(groupNameLikeQuery.getGroupName()), ErrorCodeEnum.UAC10015011.msg());
        Preconditions.checkArgument(!StringUtils.isEmpty(groupNameLikeQuery.getType()), ErrorCodeEnum.UAC10015012.msg());
        List<UacGroup> uacGroups = uacGroupService.queryByLikeName(groupNameLikeQuery.getGroupName());
        if (uacGroups != null) {
            for (UacGroup uacGroup : uacGroups) {
                if (!uacGroup.getType().equals(groupNameLikeQuery.getType()))
                    continue;
                GroupSaveDto groupSaveDto = new GroupSaveDto();
                try {
                    BeanUtils.copyProperties(groupSaveDto, uacGroup);
                } catch (Exception e) {
                    logger.error("用户组Dto与用户组传输Dto属性拷贝异常");
                    e.printStackTrace();
                }
                groupSaveDtos.add(groupSaveDto);
            }

        }
        return WrapMapper.ok(groupSaveDtos);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据Group的Id查询对应的全部User的Id")
    public Wrapper<List<Long>> getUacUserIdListByGroupId(@RequestParam("groupId")Long groupId){
        logger.info("根据组织Id查询组织对应的全部用户的Id");
        List<Long> res = new ArrayList<>();
        Set<UacGroupUser> allUserSet = new HashSet<>();
        List<GroupZtreeVo> groupZtreeVos = uacGroupService.getGroupTree(groupId);
        for (GroupZtreeVo groupZtreeVo : groupZtreeVos) {
            List<UacGroupUser> bindUserDtoList = uacGroupUserMapper.listByGroupId(groupZtreeVo.getId());
            allUserSet.addAll(bindUserDtoList);
        }
        allUserSet.forEach(uacGroupUser -> {
            res.add(uacGroupUser.getUserId());
        });
        return WrapMapper.ok(res);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "根据组织Id查询组织列表")
    public Wrapper<List<GroupZtreeVo>> getGroupTreeById(@PathVariable("groupId") Long groupId) {
        logger.info("根据组织Id查询组织列表");
        List<com.ananops.provider.model.vo.GroupZtreeVo> tree = uacGroupService.getGroupTree(groupId);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "操作成功", tree);
    }

    @Override
    @ApiOperation(httpMethod = "POST", value = "通过组织ID查询其所在公司信息")
    public Wrapper<CompanyDto> getCompanyInfoById(@PathVariable("groupId") Long groupId) {
        CompanyDto companyDto = new CompanyDto();
        org.springframework.beans.BeanUtils.copyProperties(uacGroupService.getCompanyInfo(groupId),companyDto);
        return WrapMapper.ok(companyDto);
    }
}
