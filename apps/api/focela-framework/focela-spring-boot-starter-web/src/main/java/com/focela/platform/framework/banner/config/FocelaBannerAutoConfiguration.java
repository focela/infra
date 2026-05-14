package com.focela.platform.framework.banner.config;

import com.focela.platform.framework.banner.core.BannerApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the Banner.
 */
@AutoConfiguration
public class FocelaBannerAutoConfiguration {

    @Bean
    public BannerApplicationRunner bannerApplicationRunner() {
        return new BannerApplicationRunner();
    }

}
