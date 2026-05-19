package com.focela.platform.system.service.sms;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.system.domain.entity.sms.SmsCodeEntity;
import com.focela.platform.system.repository.mapper.sms.SmsCodeMapper;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import com.focela.platform.system.config.sms.SmsCodeProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.date.DateUtils.isToday;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * SMS verification code Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultSmsCodeService implements SmsCodeService {

    private final SmsCodeProperties smsCodeProperties;

    private final SmsCodeMapper smsCodeMapper;

    private final SmsSendService smsSendService;

    @Override
    public void sendSmsCode(SmsCodeSendRpcRequest request) {
        SmsSceneEnum sceneEnum = SmsSceneEnum.getCodeByScene(request.getScene());
        Assert.notNull(sceneEnum, "verification code scene ({}) configuration not found", request.getScene());
        // create the verification code
        String code = createSmsCode(request.getMobile(), request.getScene(), request.getCreateIp());
        // send the verification code
        smsSendService.sendSingleSms(request.getMobile(), null, null,
                sceneEnum.getTemplateCode(), MapUtil.of("code", code));
    }

    private String createSmsCode(String mobile, Integer scene, String ip) {
        // validate whether a code can be sent; scenes are not filtered
        SmsCodeEntity lastSmsCode = smsCodeMapper.selectLastByMobile(mobile, null, null);
        if (lastSmsCode != null) {
            if (LocalDateTimeUtil.between(lastSmsCode.getCreateTime(), LocalDateTime.now()).toMillis()
                    < smsCodeProperties.getSendFrequency().toMillis()) { // sent too frequently
                throw exception(SMS_CODE_SEND_TOO_FAST);
            }
            if (isToday(lastSmsCode.getCreateTime()) && // must be today to count against today's limit
                    lastSmsCode.getTodayIndex() >= smsCodeProperties.getSendMaximumQuantityPerDay()) { // exceeds today's send limit.
                throw exception(SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
            }
            // TODO Focela: enhancement, daily quota per IP
            // TODO Focela: enhancement, hourly quota per IP
        }

        // create the verification code record
        String code = String.format("%0" + smsCodeProperties.getEndCode().toString().length() + "d",
                randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        SmsCodeEntity newSmsCode = SmsCodeEntity.builder().mobile(mobile).code(code).scene(scene)
                .todayIndex(lastSmsCode != null && isToday(lastSmsCode.getCreateTime()) ? lastSmsCode.getTodayIndex() + 1 : 1)
                .createIp(ip).used(false).build();
        smsCodeMapper.insert(newSmsCode);
        return code;
    }

    @Override
    public void useSmsCode(SmsCodeUseRpcRequest request) {
        // check whether the verification code is valid
        SmsCodeEntity lastSmsCode = validateSmsCode0(request.getMobile(), request.getCode(), request.getScene());
        // consume the verification code
        smsCodeMapper.updateById(SmsCodeEntity.builder().id(lastSmsCode.getId())
                .used(true).usedTime(LocalDateTime.now()).usedIp(request.getUsedIp()).build());
    }

    @Override
    public void validateSmsCode(SmsCodeValidateRpcRequest request) {
        validateSmsCode0(request.getMobile(), request.getCode(), request.getScene());
    }

    private SmsCodeEntity validateSmsCode0(String mobile, String code, Integer scene) {
        // validate the verification code
        SmsCodeEntity lastSmsCode = smsCodeMapper.selectLastByMobile(mobile, code, scene);
        // if the verification code does not exist, throw an exception
        if (lastSmsCode == null) {
            throw exception(SMS_CODE_NOT_FOUND);
        }
        // expired
        if (LocalDateTimeUtil.between(lastSmsCode.getCreateTime(), LocalDateTime.now()).toMillis()
                >= smsCodeProperties.getExpireTimes().toMillis()) { // verification code expired
            throw exception(SMS_CODE_EXPIRED);
        }
        // check whether the verification code has been used
        if (Boolean.TRUE.equals(lastSmsCode.getUsed())) {
            throw exception(SMS_CODE_USED);
        }
        return lastSmsCode;
    }

}
