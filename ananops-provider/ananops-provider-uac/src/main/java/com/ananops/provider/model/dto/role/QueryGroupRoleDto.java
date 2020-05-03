package com.ananops.provider.model.dto.role;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * Created By ChengHao On 2020/3/13
 */
@Data
@ApiModel(value = "查询组织的角色")
public class QueryGroupRoleDto implements Serializable {
    private static final long serialVersionUID = 6916207965209561660L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态
     */
    private String status;

    /**
     * 角色id
     */
    private List<Long> roleIds;

}
