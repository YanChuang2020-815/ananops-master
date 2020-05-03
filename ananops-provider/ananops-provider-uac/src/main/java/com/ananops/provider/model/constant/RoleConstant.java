package com.ananops.provider.model.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 为创建的用户方和服务方管理员角色提供初始角色
 *
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-02-27 15:51
 */

public class RoleConstant {

    /**
     * 用户方管理员角色可操作的平台默认角色
     */
    public static final List<Long> USER_DEFAULT_ROLE_IDS = Arrays.asList(
            781801330418123776L,  // 监管人员
            781804819726730240L,  // 用户负责人
            781804819726730241L,  // 保卫干事
            781805692947268608L   // 用户值机员
            );

    /**
     * 服务方管理员角色可操作的平台默认角色
     */
    public static final List<Long> FAC_DEFAULT_ROLE_IDS = Arrays.asList(
            781824027910996992L,  // 服务商负责人
            781828423130547200L  // 服务商业务员
    );
}
