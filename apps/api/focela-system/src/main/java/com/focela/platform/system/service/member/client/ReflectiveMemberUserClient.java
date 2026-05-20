package com.focela.platform.system.service.member.client;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reflection-based adapter for the optional member module API.
 */
@Component
public class ReflectiveMemberUserClient implements MemberUserClient {

    @Value("${focela.info.base-package}")
    private String basePackage;

    private volatile Object memberUserApi;

    @Override
    public String getMobile(Long id) {
        Object user = getMemberUser(id);
        if (user == null) {
            return null;
        }
        return ReflectUtil.invoke(user, "getMobile");
    }

    @Override
    public String getEmail(Long id) {
        Object user = getMemberUser(id);
        if (user == null) {
            return null;
        }
        return ReflectUtil.invoke(user, "getEmail");
    }

    private Object getMemberUser(Long id) {
        return ReflectUtil.invoke(getMemberUserApi(), "getUser", id);
    }

    private Object getMemberUserApi() {
        if (memberUserApi == null) {
            memberUserApi = SpringUtil.getBean(ClassUtil.loadClass(String.format("%s.module.member.api.user.MemberUserApi", basePackage)));
        }
        return memberUserApi;
    }

}
