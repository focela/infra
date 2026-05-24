package com.focela.platform.banner.core;

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
                    "https://platform.focela.com/api-doc/",
                    "https://platform.focela.com",
                    "https://t.zsxq.com/02Yf6M7Qn");

            // Report module
            if (isNotPresent("com.focela.platform.report.config.security.SecurityConfiguration")) {
                System.out.println("[Report module focela-report - disabled][see https://platform.focela.com/report/ to enable]");
            }
            // Workflow
            if (isNotPresent("com.focela.platform.bpm.config.flowable.BpmFlowableConfiguration")) {
                System.out.println("[Workflow module focela-bpm - disabled][see https://platform.focela.com/bpm/ to enable]");
            }
            // Mall system
            if (isNotPresent("com.focela.platform.trade.config.web.TradeWebConfiguration")) {
                System.out.println("[Mall system focela-mall - disabled][see https://platform.focela.com/mall/build/ to enable]");
            }
            // ERP system
            if (isNotPresent("com.focela.platform.erp.config.web.ErpWebConfiguration")) {
                System.out.println("[ERP system focela-erp - disabled][see https://platform.focela.com/erp/build/ to enable]");
            }
            // CRM system
            if (isNotPresent("com.focela.platform.crm.config.web.CrmWebConfiguration")) {
                System.out.println("[CRM system focela-crm - disabled][see https://platform.focela.com/crm/build/ to enable]");
            }
            // WeChat MP
            if (isNotPresent("com.focela.platform.mp.config.mp.MpConfiguration")) {
                System.out.println("[WeChat MP focela-mp - disabled][see https://platform.focela.com/mp/build/ to enable]");
            }
            // Payment platform
            if (isNotPresent("com.focela.platform.pay.config.pay.PayConfiguration")) {
                System.out.println("[Payment system focela-pay - disabled][see https://platform.focela.com/pay/build/ to enable]");
            }
            // AI large model
            if (isNotPresent("com.focela.platform.ai.config.web.AiWebConfiguration")) {
                System.out.println("[AI large model focela-ai - disabled][see https://platform.focela.com/ai/build/ to enable]");
            }
            // IoT
            if (isNotPresent("com.focela.platform.iot.config.web.IotWebConfiguration")) {
                System.out.println("[IoT focela-iot - disabled][see https://platform.focela.com/iot/build/ to enable]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
