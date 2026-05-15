package com.focela.platform.web.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "focela.web")
@Validated
@Data
public class WebProperties {

    @NotNull(message = "APP API must not be blank")
    private Api appApi = new Api("/app-api", "**.controller.app.**");
    @NotNull(message = "Admin API must not be blank")
    private Api adminApi = new Api("/admin-api", "**.controller.admin.**");

    @NotNull(message = "Admin UI must not be blank")
    private Ui adminUi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        /**
         * API prefix, applied as a unified prefix to all RESTful APIs provided by Controllers
         *
         *
         * Purpose: this prefix prevents Swagger and Actuator from being accidentally exposed externally through Nginx (security issue).
         *      With this, Nginx only needs to forward all endpoints under /api/*.
         *
         * @see FocelaWebAutoConfiguration#configurePathMatch(PathMatchConfigurer)
         */
        @NotEmpty(message = "API prefix must not be blank")
        private String prefix;

        /**
         * Ant path rule for the Controller package
         *
         * Main purpose is to set the specified {@link #prefix} for the Controllers.
         */
        @NotEmpty(message = "Controller package must not be blank")
        private String controller;

    }

    @Data
    @Valid
    public static class Ui {

        /**
         * Access URL
         */
        private String url;

    }

}
