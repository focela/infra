package com.focela.platform.module.system.repository.mapper.sms;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.QueryWrapperX;
import com.focela.platform.module.system.repository.entity.sms.SmsCodeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeMapper extends BaseMapperX<SmsCodeEntity> {

    /**
     * 获得手机号的最后一个手机验证码
     *
     * @param mobile 手机号
     * @param scene 发送场景，选填
     * @param code 验证码 选填
     * @return 手机验证码
     */
    default SmsCodeEntity selectLastByMobile(String mobile, String code, Integer scene) {
        return selectOne(new QueryWrapperX<SmsCodeEntity>()
                .eq("mobile", mobile)
                .eqIfPresent("scene", scene)
                .eqIfPresent("code", code)
                .orderByDesc("id")
                .limitN(1));
    }

}
