package com.ananops.provider.model.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "an_imc_user_item")
@Alias(value = "imcUserItem")
public class ImcUserItem implements Serializable {
    private static final long serialVersionUID = -6997003969639602958L;
    /**
     * 甲方用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 巡检任务子项id
     */
    @Column(name = "item_id")
    private Long itemId;

}