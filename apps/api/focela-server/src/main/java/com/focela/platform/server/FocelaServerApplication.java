package com.focela.platform.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${focela.info.base-package}
@SpringBootApplication(scanBasePackages = {"${focela.info.base-package}.server", "${focela.info.base-package}.module"})
public class FocelaServerApplication {

    public static void main(String[] args) {
        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章

        SpringApplication.run(FocelaServerApplication.class, args);
//        new SpringApplicationBuilder(FocelaServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://www.example.com/quick-start/ 文章
    }

}
