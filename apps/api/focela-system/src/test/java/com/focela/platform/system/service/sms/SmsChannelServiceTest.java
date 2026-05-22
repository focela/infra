package com.focela.platform.system.service.sms;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelPageRequest;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelSaveRequest;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.repository.mapper.sms.SmsChannelMapper;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.SmsClientFactory;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.SMS_CHANNEL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import(DefaultSmsChannelService.class)
public class SmsChannelServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultSmsChannelService smsChannelService;

    @Resource
    private SmsChannelMapper smsChannelMapper;

    @MockitoBean
    private SmsClientFactory smsClientFactory;
    @MockitoBean
    private SmsTemplateService smsTemplateService;

    @Test
    public void createSmsChannel_success() {
        // prepare parameters
        SmsChannelSaveRequest request = randomPojo(SmsChannelSaveRequest.class, o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned

        // invoke
        Long smsChannelId = smsChannelService.createSmsChannel(request);
        // assert
        assertNotNull(smsChannelId);
        // verify record properties are correct
        SmsChannelEntity smsChannel = smsChannelMapper.selectById(smsChannelId);
        assertPojoEquals(request, smsChannel, "id");
    }

    @Test
    public void updateSmsChannel_success() {
        // mock data
        SmsChannelEntity dbSmsChannel = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel);// @Sql: first insert an existing record
        // prepare parameters
        SmsChannelSaveRequest request = randomPojo(SmsChannelSaveRequest.class, o -> {
            o.setId(dbSmsChannel.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
            o.setCallbackUrl(randomString());
        });

        // invoke
        smsChannelService.updateSmsChannel(request);
        // verify update is correct
        SmsChannelEntity smsChannel = smsChannelMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, smsChannel);
    }

    @Test
    public void updateSmsChannel_missing() {
        // prepare parameters
        SmsChannelSaveRequest request = randomPojo(SmsChannelSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> smsChannelService.updateSmsChannel(request), SMS_CHANNEL_NOT_FOUND);
    }

    @Test
    public void deleteSmsChannel_success() {
        // mock data
        SmsChannelEntity dbSmsChannel = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbSmsChannel.getId();

        // invoke
        smsChannelService.deleteSmsChannel(id);
        // verify data no longer exists
        assertNull(smsChannelMapper.selectById(id));
    }

    @Test
    public void deleteSmsChannel_missing() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> smsChannelService.deleteSmsChannel(id), SMS_CHANNEL_NOT_FOUND);
    }

    @Test
    public void deleteSmsChannel_hasChildren() {
        // mock data
        SmsChannelEntity dbSmsChannel = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbSmsChannel.getId();
        // mock the method
        when(smsTemplateService.getSmsTemplateCountByChannelId(eq(id))).thenReturn(10L);

        // invoke and assert exception
        assertServiceException(() -> smsChannelService.deleteSmsChannel(id), SMS_CHANNEL_HAS_CHILDREN);
    }

    @Test
    public void getSmsChannel() {
        // mock data
        SmsChannelEntity dbSmsChannel = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel); // @Sql: first insert an existing record
        // prepare parameters
        Long id = dbSmsChannel.getId();

        // invoke, and assert
        assertPojoEquals(dbSmsChannel, smsChannelService.getSmsChannel(id));
    }

    @Test
    public void getSmsChannelList() {
        // mock data
        SmsChannelEntity dbSmsChannel01 = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel01);
        SmsChannelEntity dbSmsChannel02 = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(dbSmsChannel02);
        // prepare parameters

        // invoke
        List<SmsChannelEntity> list = smsChannelService.getSmsChannelList();
        // assert
        assertEquals(2, list.size());
        assertPojoEquals(dbSmsChannel01, list.get(0));
        assertPojoEquals(dbSmsChannel02, list.get(1));
    }

    @Test
    public void getSmsChannelPage() {
       // mock data
       SmsChannelEntity dbSmsChannel = randomPojo(SmsChannelEntity.class, o -> { // will be queried later
           o.setSignature("Focela");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setCreateTime(buildTime(2020, 12, 12));
       });
       smsChannelMapper.insert(dbSmsChannel);
       // test signature mismatch
       smsChannelMapper.insert(cloneIgnoreId(dbSmsChannel, o -> o.setSignature("source")));
       // test status mismatch
       smsChannelMapper.insert(cloneIgnoreId(dbSmsChannel, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // test createTime mismatch
       smsChannelMapper.insert(cloneIgnoreId(dbSmsChannel, o -> o.setCreateTime(buildTime(2020, 11, 11))));
       // prepare parameters
       SmsChannelPageRequest request = new SmsChannelPageRequest();
       request.setSignature("Focela");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setCreateTime(buildBetweenTime(2020, 12, 1, 2020, 12, 24));

       // invoke
       PageResult<SmsChannelEntity> pageResult = smsChannelService.getSmsChannelPage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbSmsChannel, pageResult.getList().get(0));
    }

    @Test
    public void getSmsClientById() {
        // mock data
        SmsChannelEntity channel = randomPojo(SmsChannelEntity.class);
        smsChannelMapper.insert(channel);
        // prepare parameters
        Long id = channel.getId();
        // mock the method
        SmsClient mockClient = mock(SmsClient.class);
        SmsChannelProperties properties = BeanUtils.toBean(channel, SmsChannelProperties.class);
        when(smsClientFactory.createOrUpdateSmsClient(eq(properties))).thenReturn(mockClient);

        // invoke
        SmsClient client = smsChannelService.getSmsClient(id);
        // assert
        assertSame(client, mockClient);
    }

    @Test
    public void getSmsClient_code() {
        // prepare parameters
        String code = randomString();
        // mock the method
        SmsClient mockClient = mock(SmsClient.class);
        when(smsClientFactory.getSmsClient(eq(code))).thenReturn(mockClient);

        // invoke
        SmsClient client = smsChannelService.getSmsClient(code);
        // assert
        assertSame(client, mockClient);
    }

}
