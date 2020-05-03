package com.ananops.provider.model.constant;

/**
 * @author Bingyue Duan
 * @version 1.0
 * @date 2020-04-07 16:50
 */

public class SysDictConstant {

    /**
     * 报修系统所需字典项
     */
    public enum SysDictEnum {

        TROUBLE_TYPE(861547645766737920L, "故障类型"),
        TROUBLE_ADDRESS(863531121525137408L, "故障位置"),
        DEVICE_TYPE(864374714221469696L, "设备类型"),
        TROUBLE_LEVEL(864377279877226496L, "故障等级"),
        EMERGENCY_LEVEL(864410595435225088L, "紧急程度");

        private String value;
        private Long id;

        SysDictEnum(Long id, String value) {
            this.id = id;
            this.value = value;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * Gets code.
         *
         * @return the code
         */
        public Long getId() {
            return id;
        }
    }

}
