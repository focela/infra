package com.focela.platform.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Project bootstrap class.
 *
 * If you run into startup issues, please carefully read https://www.example.com/quick-start/.
 */
@SuppressWarnings("SpringComponentScan") // Ignore IDEA being unable to resolve ${focela.info.base-package}
@SpringBootApplication(scanBasePackages = {"${focela.info.base-package}.server", "${focela.info.base-package}.system", "${focela.info.base-package}.infra"})
public class FocelaServerApplication {

    public static void main(String[] args) {
        // If you run into startup issues, please carefully read https://www.example.com/quick-start/.

        SpringApplication.run(FocelaServerApplication.class, args);
//        new SpringApplicationBuilder(FocelaServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

        // If you run into startup issues, please carefully read https://www.example.com/quick-start/.
    }

}
