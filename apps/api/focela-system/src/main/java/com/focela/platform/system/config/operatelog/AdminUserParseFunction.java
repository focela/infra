package com.focela.platform.system.config.operatelog;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.service.user.UserService;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link IParseFunction} implementation for admin user name
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserParseFunction implements IParseFunction {

    public static final String NAME = "getAdminUserById";

    private final UserService adminUserService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // get user information
        UserEntity user = adminUserService.getUser(Convert.toLong(value));
        if (user == null) {
            log.warn("[apply][get user {{}} is empty", value);
            return "";
        }
        // return format: Focela(13888888888)
        String nickname = user.getNickname();
        if (StrUtil.isEmpty(user.getMobile())) {
            return nickname;
        }
        return StrUtil.format("{}({})", nickname, user.getMobile());
    }

}
