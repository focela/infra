package com.focela.platform.system.repository.mapper.sms;

import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.QueryWrapperX;
import com.focela.platform.system.entity.sms.SmsCodeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeMapper extends BaseMapperX<SmsCodeEntity> {

    /**
     * Get the last SMS verification code for a mobile number
     *
     * @param mobile mobile number
     * @param scene  sending scene, optional
     * @param code   verification code, optional
     * @return SMS verification code
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
