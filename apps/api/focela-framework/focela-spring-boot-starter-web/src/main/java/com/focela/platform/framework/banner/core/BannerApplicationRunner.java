package com.focela.platform.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "接口文档: \t{} \n\t" +
                            "开发文档: \t{} \n\t" +
                            "视频教程: \t{} \n" +
                            "----------------------------------------------------------",
                    "https://www.example.com/api-doc/",
                    "https://www.example.com",
                    "https://t.zsxq.com/02Yf6M7Qn");

            // 数据报表
            if (isNotPresent("com.focela.platform.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[报表模块 focela-module-report - 已禁用][参考 https://www.example.com/report/ 开启]");
            }
            // 工作流
            if (isNotPresent("com.focela.platform.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
                System.out.println("[工作流模块 focela-module-bpm - 已禁用][参考 https://www.example.com/bpm/ 开启]");
            }
            // 商城系统
            if (isNotPresent("com.focela.platform.module.trade.framework.web.config.TradeWebConfiguration")) {
                System.out.println("[商城系统 focela-module-mall - 已禁用][参考 https://www.example.com/mall/build/ 开启]");
            }
            // ERP 系统
            if (isNotPresent("com.focela.platform.module.erp.framework.web.config.ErpWebConfiguration")) {
                System.out.println("[ERP 系统 focela-module-erp - 已禁用][参考 https://www.example.com/erp/build/ 开启]");
            }
            // CRM 系统
            if (isNotPresent("com.focela.platform.module.crm.framework.web.config.CrmWebConfiguration")) {
                System.out.println("[CRM 系统 focela-module-crm - 已禁用][参考 https://www.example.com/crm/build/ 开启]");
            }
            // 微信公众号
            if (isNotPresent("com.focela.platform.module.mp.framework.mp.config.MpConfiguration")) {
                System.out.println("[微信公众号 focela-module-mp - 已禁用][参考 https://www.example.com/mp/build/ 开启]");
            }
            // 支付平台
            if (isNotPresent("com.focela.platform.module.pay.framework.pay.config.PayConfiguration")) {
                System.out.println("[支付系统 focela-module-pay - 已禁用][参考 https://www.example.com/pay/build/ 开启]");
            }
            // AI 大模型
            if (isNotPresent("com.focela.platform.module.ai.framework.web.config.AiWebConfiguration")) {
                System.out.println("[AI 大模型 focela-module-ai - 已禁用][参考 https://www.example.com/ai/build/ 开启]");
            }
            // IoT 物联网
            if (isNotPresent("com.focela.platform.module.iot.framework.web.config.IotWebConfiguration")) {
                System.out.println("[IoT 物联网 focela-module-iot - 已禁用][参考 https://www.example.com/iot/build/ 开启]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
