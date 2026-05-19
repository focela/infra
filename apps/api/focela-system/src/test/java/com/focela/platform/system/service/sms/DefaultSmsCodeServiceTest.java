package com.focela.platform.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.system.domain.entity.sms.SmsCodeEntity;
import com.focela.platform.system.repository.mapper.sms.SmsCodeMapper;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import com.focela.platform.system.config.sms.SmsCodeProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
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
        // prepare parameters
        SmsCodeSendRpcRequest request = randomPojo(SmsCodeSendRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });

        // invoke
        smsCodeService.sendSmsCode(request);
        // assert code
        SmsCodeEntity smsCodeEntity = smsCodeMapper.selectOne(null);
        assertPojoEquals(request, smsCodeEntity);
        assertEquals("9999", smsCodeEntity.getCode());
        assertEquals(1, smsCodeEntity.getTodayIndex());
        assertFalse(smsCodeEntity.getUsed());
        // assert call
        verify(smsSendService).sendSingleSms(eq(request.getMobile()), isNull(), isNull(),
                eq("user-sms-login"), eq(MapUtil.of("code", "9999")));
    }

    @Test
    public void sendSmsCode_tooFast() {
        // mock data
        SmsCodeEntity smsCodeEntity = randomPojo(SmsCodeEntity.class,
                o -> o.setMobile("15601691300").setTodayIndex(1));
        smsCodeMapper.insert(smsCodeEntity);
        // prepare parameters
        SmsCodeSendRpcRequest request = randomPojo(SmsCodeSendRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });

        // invoke, and assert exception
        assertServiceException(() -> smsCodeService.sendSmsCode(request),
                SMS_CODE_SEND_TOO_FAST);
    }

    @Test
    public void sendSmsCode_exceedDay() {
        // mock data
        SmsCodeEntity smsCodeEntity = randomPojo(SmsCodeEntity.class,
                o -> o.setMobile("15601691300").setTodayIndex(10).setCreateTime(LocalDateTime.now()));
        smsCodeMapper.insert(smsCodeEntity);
        // prepare parameters
        SmsCodeSendRpcRequest request = randomPojo(SmsCodeSendRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        });
        when(smsCodeProperties.getSendFrequency()).thenReturn(Duration.ofMillis(0));

        // invoke, and assert exception
        assertServiceException(() -> smsCodeService.sendSmsCode(request),
                SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
    }

    @Test
    public void testUseSmsCode_success() {
        // prepare parameters
        SmsCodeUseRpcRequest request = randomPojo(SmsCodeUseRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> {
            o.setMobile(request.getMobile()).setScene(request.getScene())
                    .setCode(request.getCode()).setUsed(false);
        }));

        // invoke
        smsCodeService.useSmsCode(request);
        // assert
        SmsCodeEntity smsCodeEntity = smsCodeMapper.selectOne(null);
        assertTrue(smsCodeEntity.getUsed());
        assertNotNull(smsCodeEntity.getUsedTime());
        assertEquals(request.getUsedIp(), smsCodeEntity.getUsedIp());
    }

    @Test
    public void validateSmsCode_success() {
        // prepare parameters
        SmsCodeValidateRpcRequest request = randomPojo(SmsCodeValidateRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(request.getMobile())
                .setScene(request.getScene()).setCode(request.getCode()).setUsed(false)));

        // invoke
        smsCodeService.validateSmsCode(request);
    }

    @Test
    public void validateSmsCode_notFound() {
        // prepare parameters
        SmsCodeValidateRpcRequest request = randomPojo(SmsCodeValidateRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });

        // invoke, and assert exception
        assertServiceException(() -> smsCodeService.validateSmsCode(request),
                SMS_CODE_NOT_FOUND);
    }

    @Test
    public void validateSmsCode_expired() {
        // prepare parameters
        SmsCodeValidateRpcRequest request = randomPojo(SmsCodeValidateRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(request.getMobile())
                .setScene(request.getScene()).setCode(request.getCode()).setUsed(false)
                .setCreateTime(LocalDateTime.now().minusMinutes(6))));

        // invoke, and assert exception
        assertServiceException(() -> smsCodeService.validateSmsCode(request),
                SMS_CODE_EXPIRED);
    }

    @Test
    public void validateSmsCode_used() {
        // prepare parameters
        SmsCodeValidateRpcRequest request = randomPojo(SmsCodeValidateRpcRequest.class, o -> {
            o.setMobile("15601691300");
            o.setScene(randomEle(SmsSceneEnum.values()).getScene());
        });
        smsCodeMapper.insert(randomPojo(SmsCodeEntity.class, o -> o.setMobile(request.getMobile())
                .setScene(request.getScene()).setCode(request.getCode()).setUsed(true)
                .setCreateTime(LocalDateTime.now())));

        // invoke, and assert exception
        assertServiceException(() -> smsCodeService.validateSmsCode(request),
                SMS_CODE_USED);
    }

}
