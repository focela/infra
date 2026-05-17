package com.focela.platform.operatelog.config;

import com.focela.platform.common.api.system.logger.OperateLogContractApi;
import com.focela.platform.operatelog.core.service.DefaultLogRecordService;
import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Operate log configuration class.
 */
@EnableLogRecord(tenant = "") // tenant attribute is not used, leave it empty
@AutoConfiguration
@Slf4j
public class FocelaOperateLogConfiguration {

    @Bean
    @Primary
    public ILogRecordService DefaultiLogRecordService(OperateLogContractApi operateLogApi) {
        return new DefaultLogRecordService(operateLogApi);
    }

}
