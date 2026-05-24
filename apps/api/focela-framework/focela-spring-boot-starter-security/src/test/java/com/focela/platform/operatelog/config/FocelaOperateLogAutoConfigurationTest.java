package com.focela.platform.operatelog.config;

import com.focela.platform.common.api.system.logger.OperateLogContractApi;
import com.focela.platform.operatelog.core.service.DefaultLogRecordService;
import com.mzt.logapi.service.ILogRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FocelaOperateLogAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FocelaOperateLogAutoConfiguration.class))
            .withBean(OperateLogContractApi.class, () -> mock(OperateLogContractApi.class));

    @Test
    void providesDefaultLogRecordService() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("DefaultiLogRecordService");
            assertThat(context.getBean("DefaultiLogRecordService")).isInstanceOf(DefaultLogRecordService.class);
            assertThat(context.getBean(ILogRecordService.class)).isInstanceOf(DefaultLogRecordService.class);
        });
    }
}
