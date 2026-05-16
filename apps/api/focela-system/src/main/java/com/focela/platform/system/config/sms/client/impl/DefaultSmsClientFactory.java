package com.focela.platform.system.config.sms.client.impl;

import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.SmsClientFactory;
import com.focela.platform.system.config.sms.enums.SmsChannelEnum;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SMS client factory implementation
 */
@Validated
@Slf4j
public class DefaultSmsClientFactory implements SmsClientFactory {

    /**
     * SMS client Map
     * key: channel ID, using {@link SmsChannelProperties#getId()}
     */
    private final ConcurrentMap<Long, AbstractSmsClient> channelIdClients = new ConcurrentHashMap<>();

    /**
     * SMS client Map
     * key: channel code, using {@link SmsChannelProperties#getCode()} ()}
     *
     * Note: in some scenarios we need to obtain the client for a specific channel type, so this map is needed.
     * For example, parsing SMS receive results is fairly generic and does not need to use {@link #channelIdClients} keyed by a specific channel ID.
     */
    private final ConcurrentMap<String, AbstractSmsClient> channelCodeClients = new ConcurrentHashMap<>();

    public DefaultSmsClientFactory() {
        // initialize the channelCodeClients map
        Arrays.stream(SmsChannelEnum.values()).forEach(channel -> {
            // create an empty SmsChannelProperties object
            SmsChannelProperties properties = new SmsChannelProperties().setCode(channel.getCode())
                    .setApiKey("default default").setApiSecret("default");
            // create the SMS client
            AbstractSmsClient smsClient = createSmsClient(properties);
            channelCodeClients.put(channel.getCode(), smsClient);
        });
    }

    @Override
    public SmsClient getSmsClient(Long channelId) {
        return channelIdClients.get(channelId);
    }

    @Override
    public SmsClient getSmsClient(String channelCode) {
        return channelCodeClients.get(channelCode);
    }

    @Override
    public SmsClient createOrUpdateSmsClient(SmsChannelProperties properties) {
        AbstractSmsClient client = channelIdClients.get(properties.getId());
        if (client == null) {
            client = this.createSmsClient(properties);
            client.init();
            channelIdClients.put(client.getId(), client);
        } else {
            client.refresh(properties);
        }
        return client;
    }

    private AbstractSmsClient createSmsClient(SmsChannelProperties properties) {
        SmsChannelEnum channelEnum = SmsChannelEnum.getByCode(properties.getCode());
        Assert.notNull(channelEnum, String.format("channel type (%s) is empty", channelEnum));
        // create the client
        switch (channelEnum) {
            case ALIYUN: return new AliyunSmsClient(properties);
            case DEBUG_DING_TALK: return new DebugDingTalkSmsClient(properties);
            case TENCENT: return new TencentSmsClient(properties);
            case HUAWEI: return  new HuaweiSmsClient(properties);
            case QINIU: return new QiniuSmsClient(properties);
        }
        // creation failed: error log + throw exception
        log.error("[createSmsClient][config ({}) cannot find suitable client implementation]", properties);
        throw new IllegalArgumentException(String.format("config (%s) cannot find suitable client implementation", properties));
    }

}
