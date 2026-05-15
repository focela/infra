package com.focela.platform.system.config.sms.core.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS channel enum
 *
 * @since 2021/1/25 10:56
 */
@Getter
@AllArgsConstructor
public enum SmsChannelEnum {

    DEBUG_DING_TALK("DEBUG_DING_TALK", "Debug (DingTalk)"),
    ALIYUN("ALIYUN", "Aliyun"),
    TENCENT("TENCENT", "Tencent Cloud"),
    HUAWEI("HUAWEI", "Huawei Cloud"),
    QINIU("QINIU", "Qiniu Cloud"),
    ;

    /**
     * Code
     */
    private final String code;
    /**
     * Name
     */
    private final String name;

    public static SmsChannelEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }

}

