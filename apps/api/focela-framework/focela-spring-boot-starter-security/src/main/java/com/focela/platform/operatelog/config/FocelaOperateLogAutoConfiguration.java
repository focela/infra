package com.focela.platform.operatelog.config;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Operate log auto-configuration.
 */
@EnableLogRecord(tenant = "")
@AutoConfiguration
@SuppressWarnings("deprecation")
public class FocelaOperateLogAutoConfiguration extends FocelaOperateLogConfiguration {
}
