package com.focela.platform.system.config.sms.client.impl;

import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract SMS client providing template methods to reduce redundant code in subclasses
 *
 * @since 2021/2/1 9:28
 */
@Slf4j
public abstract class AbstractSmsClient implements SmsClient {

    /**
     * SMS channel configuration
     */
    protected volatile SmsChannelProperties properties;

    public AbstractSmsClient(SmsChannelProperties properties) {
        this.properties = properties;
    }

    /**
     * Initialize
     */
    public final void init() {
        log.debug("[init][config ({}) init complete]", properties);
    }

    public final void refresh(SmsChannelProperties properties) {
        // check whether an update is needed
        if (properties.equals(this.properties)) {
            return;
        }
        log.info("[refresh][config ({}) changed, re-init]", properties);
        this.properties = properties;
        // initialize
        this.init();
    }

    @Override
    public Long getId() {
        return properties.getId();
    }

}
