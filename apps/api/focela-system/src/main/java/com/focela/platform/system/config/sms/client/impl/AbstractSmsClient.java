package com.focela.platform.system.config.sms.client.impl;

import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信客户端的抽象类，提供模板方法，减少子类的冗余代码
 *
 * @since 2021/2/1 9:28
 */
@Slf4j
public abstract class AbstractSmsClient implements SmsClient {

    /**
     * 短信渠道配置
     */
    protected volatile SmsChannelProperties properties;

    public AbstractSmsClient(SmsChannelProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化
     */
    public final void init() {
        log.debug("[init][config ({}) init complete]", properties);
    }

    public final void refresh(SmsChannelProperties properties) {
        // 判断是否更新
        if (properties.equals(this.properties)) {
            return;
        }
        log.info("[refresh][config ({})changed, re-init]", properties);
        this.properties = properties;
        // 初始化
        this.init();
    }

    @Override
    public Long getId() {
        return properties.getId();
    }

}
