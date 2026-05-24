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
 *
 * @deprecated use {@link FocelaOperateLogAutoConfiguration}. This class remains the active
 * auto-configuration import for backward compatibility.
 */
@Deprecated(since = "1.0.0", forRemoval = false)
@EnableLogRecord(tenant = "") // tenant attribute is not used, leave it empty
@AutoConfiguration
@Slf4j
public class FocelaOperateLogConfiguration {

    @Bean("DefaultiLogRecordService")
    @Primary
    public ILogRecordService logRecordService(OperateLogContractApi operateLogApi) {
        return new DefaultLogRecordService(operateLogApi);
    }

}
