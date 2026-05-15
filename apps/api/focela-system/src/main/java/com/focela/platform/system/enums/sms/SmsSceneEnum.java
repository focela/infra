package com.focela.platform.system.enums.sms;

import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * SMS verification code send scene enum
 */
@Getter
@AllArgsConstructor
public enum SmsSceneEnum implements ArrayValuable<Integer> {

    MEMBER_LOGIN(1, "user-sms-login", "Member - login with mobile"),
    MEMBER_UPDATE_MOBILE(2, "user-update-mobile", "Member - update mobile"),
    MEMBER_UPDATE_PASSWORD(3, "user-update-password", "Member - update password"),
    MEMBER_RESET_PASSWORD(4, "user-reset-password", "Member - forgot password"),

    ADMIN_MEMBER_LOGIN(21, "admin-sms-login", "Admin user - login with mobile"),
    ADMIN_MEMBER_REGISTER(22, "admin-sms-register", "Admin user - register with mobile"),
    ADMIN_MEMBER_RESET_PASSWORD(23, "admin-reset-password", "Admin user - forgot password");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(SmsSceneEnum::getScene).toArray(Integer[]::new);

    /**
     * Verification scene ID
     */
    private final Integer scene;
    /**
     * Template code
     */
    private final String templateCode;
    /**
     * Description
     */
    private final String description;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static SmsSceneEnum getCodeByScene(Integer scene) {
        return ArrayUtil.firstMatch(sceneEnum -> sceneEnum.getScene().equals(scene),
                values());
    }

}
