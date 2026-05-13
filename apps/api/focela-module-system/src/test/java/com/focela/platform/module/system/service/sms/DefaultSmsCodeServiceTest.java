package com.focela.platform.module.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.focela.platform.module.system.entity.sms.SmsCodeEntity;
import com.focela.platform.module.system.repository.mapper.sms.SmsCodeMapper;
import com.focela.platform.module.system.enums.sms.SmsSceneEnum;
import com.focela.platform.module.system.config.sms.config.SmsCodeProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(DefaultSmsCodeService.class)
public class DefaultSmsCodeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultSmsCodeService smsCodeService;

    @Resource
    private SmsCodeMapper smsCodeMapper;

    @MockitoBean
    private SmsCodeProperties smsCodeProperties;
    @MockitoBean
    private SmsSendService smsSendService;

    @BeforeEach
    public void setUp() {
        when(smsCodeProperties.getExpireTimes()).thenReturn(Duration.ofMinutes(5));
        when(smsCodeProperties.getSendFrequency()).thenReturn(Duration.ofMinutes(1));
        when(smsCodeProperties.getSendMaximumQuantityPerDay()).thenReturn(10);
        when(smsCodeProperties.getBeginCode()).thenReturn(9999);
        when(smsCodeProperties.getEndCode()).thenReturn(9999);
    }

    @Test
    public void sendSmsCode_success() {
        // 准备参数
        SmsCodeSendReqDTO reqDTO = randomPojo(SmsCodeSendReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });

        // 调用
        smsCodeService.sendSmsCode(reqDTO);
        // 断言 code 验证码
        SmsCodeEntity smsCodeDO = smsCodeMapper.selectOne(null);
        assertPojoEquals(reqDTO, smsCodeDO);
        assertEquals("9999", smsCodeDO.getCode());
        assertEquals(1, smsCodeDO.getTodayIndex());
        assertFalse(smsCodeDO.getUsed());
        // 断言调用
        verify(smsSendService).sendSingleSms(eq(reqDTO.getMobile()), isNull(), isNull(),
                eq("user-sms-login"), eq(MapUtil.of("code", "9999")));
    }

    @Test
    public void sendSmsCode_tooFast() {
        // mock 数据
        SmsCodeEntity smsCodeDO = randomPojo(SmsCodeEntity.class,
                o -> o.setMobile("15601691300").setTodayIndex(1));
        smsCodeMapper.insert(smsCodeDO);
        // 准备参数
        SmsCodeSendReqDTO reqDTO = randomPojo(SmsCodeSendReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });

        // 调用，并断言异常
        assertServiceException(() -> smsCodeService.sendSmsCode(reqDTO),
                SMS_CODE_SEND_TOO_FAST);
    }

    @Test
    public void sendSmsCode_exceedDay() {
        // mock 数据
        SmsCodeEntity smsCodeDO = randomPojo(SmsCodeEntity.class,
                o -> o.setMobile("15601691300").setTodayIndex(10).setCreateTime(LocalDateTime.now()));
        smsCodeMapper.insert(smsCodeDO);
        // 准备参数
        SmsCodeSendReqDTO reqDTO = randomPojo(SmsCodeSendReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });
        when(smsCodeProperties.getSendFrequency()).thenReturn(Duration.ofMillis(0));

        // 调用，并断言异常
        assertServiceException(() -> smsCodeService.sendSmsCode(reqDTO),
                SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
    }

    @Test
    public void testUseSmsCode_success() {
        // 准备参数
        SmsCodeUseReqDTO reqDTO = randomPojo(SmsCodeUseReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> {
            o.setMobile(reqDTO.getMobile()).setScene(reqDTO.getScene())
                    .setCode(reqDTO.getCode()).setUsed(false);
        }));

        // 调用
        smsCodeService.useSmsCode(reqDTO);
        // 断言
        SmsCodeEntity smsCodeDO = smsCodeMapper.selectOne(null);
        assertTrue(smsCodeDO.getUsed());
        assertNotNull(smsCodeDO.getUsedTime());
        assertEquals(reqDTO.getUsedIp(), smsCodeDO.getUsedIp());
    }

    @Test
    public void validateSmsCode_success() {
        // 准备参数
        SmsCodeValidateReqDTO reqDTO = randomPojo(SmsCodeValidateReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(reqDTO.getMobile())
                .setScene(reqDTO.getScene()).setCode(reqDTO.getCode()).setUsed(false)));

        // 调用
        smsCodeService.validateSmsCode(reqDTO);
    }

    @Test
    public void validateSmsCode_notFound() {
        // 准备参数
        SmsCodeValidateReqDTO reqDTO = randomPojo(SmsCodeValidateReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });

        // 调用，并断言异常
        assertServiceException(() -> smsCodeService.validateSmsCode(reqDTO),
                SMS_CODE_NOT_FOUND);
    }

    @Test
    public void validateSmsCode_expired() {
        // 准备参数
        SmsCodeValidateReqDTO reqDTO = randomPojo(SmsCodeValidateReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(reqDTO.getMobile())
                .setScene(reqDTO.getScene()).setCode(reqDTO.getCode()).setUsed(false)
                .setCreateTime(LocalDateTime.now().minusMinutes(6))));

        // 调用，并断言异常
        assertServiceException(() -> smsCodeService.validateSmsCode(reqDTO),
                SMS_CODE_EXPIRED);
    }

    @Test
    public void validateSmsCode_used() {
        // 准备参数
        SmsCodeValidateReqDTO reqDTO = randomPojo(SmsCodeValidateReqDTO.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(reqDTO.getMobile())
                .setScene(reqDTO.getScene()).setCode(reqDTO.getCode()).setUsed(true)
                .setCreateTime(LocalDateTime.now())));

        // 调用，并断言异常
        assertServiceException(() -> smsCodeService.validateSmsCode(reqDTO),
                SMS_CODE_USED);
    }

}
