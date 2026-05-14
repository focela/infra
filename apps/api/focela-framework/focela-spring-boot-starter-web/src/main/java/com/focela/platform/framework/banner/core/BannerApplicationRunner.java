package com.focela.platform.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * After the project starts successfully, prints documentation-related URLs.
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // delay 1 second so this prints last
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application started successfully!\n\t" +
                            "API docs: \t{} \n\t" +
                            "Dev docs: \t{} \n\t" +
                            "Video tutorials: \t{} \n" +
                            "----------------------------------------------------------",
                    "https://www.example.com/api-doc/",
                    "https://www.example.com",
                    "https://t.zsxq.com/02Yf6M7Qn");

            // Report module
            if (isNotPresent("com.focela.platform.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[Report module focela-module-report - disabled][see https://www.example.com/report/ to enable]");
            }
            // Workflow
            if (isNotPresent("com.focela.platform.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
                System.out.println("[Workflow module focela-module-bpm - disabled][see https://www.example.com/bpm/ to enable]");
            }
            // Mall system
            if (isNotPresent("com.focela.platform.module.trade.framework.web.config.TradeWebConfiguration")) {
                System.out.println("[Mall system focela-module-mall - disabled][see https://www.example.com/mall/build/ to enable]");
            }
            // ERP system
            if (isNotPresent("com.focela.platform.module.erp.framework.web.config.ErpWebConfiguration")) {
                System.out.println("[ERP system focela-module-erp - disabled][see https://www.example.com/erp/build/ to enable]");
            }
            // CRM system
            if (isNotPresent("com.focela.platform.module.crm.framework.web.config.CrmWebConfiguration")) {
                System.out.println("[CRM system focela-module-crm - disabled][see https://www.example.com/crm/build/ to enable]");
            }
            // WeChat MP
            if (isNotPresent("com.focela.platform.module.mp.framework.mp.config.MpConfiguration")) {
                System.out.println("[WeChat MP focela-module-mp - disabled][see https://www.example.com/mp/build/ to enable]");
            }
            // Payment platform
            if (isNotPresent("com.focela.platform.module.pay.framework.pay.config.PayConfiguration")) {
                System.out.println("[Payment system focela-module-pay - disabled][see https://www.example.com/pay/build/ to enable]");
            }
            // AI large model
            if (isNotPresent("com.focela.platform.module.ai.framework.web.config.AiWebConfiguration")) {
                System.out.println("[AI large model focela-module-ai - disabled][see https://www.example.com/ai/build/ to enable]");
            }
            // IoT
            if (isNotPresent("com.focela.platform.module.iot.framework.web.config.IotWebConfiguration")) {
                System.out.println("[IoT focela-module-iot - disabled][see https://www.example.com/iot/build/ to enable]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
