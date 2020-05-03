package com.ananops.provider.model.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Uac Group-Role mapper.
 *
 * @author Duan Bingyue
 */
@Data
@Table(name = "an_uac_role_group")
@Alias(value = "uacRoleGroup")
public class UacRoleGroup implements Serializable {

    private static final long serialVersionUID = -5351697764979557897L;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Id
    @Column(name = "group_id")
    private Long groupId;
}