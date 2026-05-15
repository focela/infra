package com.focela.platform.swagger.config;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Swagger auto-configuration class, based on OpenAPI + Springdoc.
 *
 * Notes:
 * 1. Springdoc docs: <a href="https://github.com/springdoc/springdoc-openapi">repository</a>
 * 2. The Swagger spec was renamed OpenAPI in 2015; they refer to the same thing.
 */
@AutoConfiguration(before = Knife4jAutoConfiguration.class) // before reason: ensure overridden Knife4jOpenApiCustomizer takes effect first. Related discussion: https://github.com/YunaiV/ruoyi-vue-pro/issues/954
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true) // disabled when set to false
@Import(Knife4jOpenApiCustomizer.class)
public class FocelaSwaggerAutoConfiguration {

    // ========== Global OpenAPI configuration ==========

    @Bean
    public OpenAPI createApi(SwaggerProperties properties) {
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
        OpenAPI openAPI = new OpenAPI()
                // interface info
                .info(buildInfo(properties))
                // interface security configuration
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }

    /**
     * API summary info
     */
    private Info buildInfo(SwaggerProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail()))
                .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }

    /**
     * Security mode: configures the token parameter to be passed via the Authorization request header
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // type
                .name(HttpHeaders.AUTHORIZATION) // request header name
                .in(SecurityScheme.In.HEADER); // token location
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }

    /**
     * Custom OpenAPI handler
     */
    @Bean
    @Primary // purpose: prefer our OpenAPIService Bean so that startup does not fail after one-click package rename
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {
        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties,
                propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    // ========== Grouped OpenAPI configuration ==========

    /**
     * API grouping for all modules
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return buildGroupedOpenApi("all", "");
    }

    public static GroupedOpenApi buildGroupedOpenApi(String group) {
        return buildGroupedOpenApi(group, group);
    }

    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/admin-api/" + path + "/**", "/app-api/" + path + "/**")
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(buildTenantHeaderParameter())
                        .addParametersItem(buildSecurityHeaderParameter()))
                .addOperationCustomizer(buildOperationIdCustomizer())
                .build();
    }

    /**
     * Build the tenant ID request header parameter
     *
     * @return multi-tenant parameter
     */
    private static Parameter buildTenantHeaderParameter() {
        return new Parameter()
                .name(HEADER_TENANT_ID) // header name
                .description("Tenant ID") // description
                .in(String.valueOf(SecurityScheme.In.HEADER)) // request header
                .schema(new IntegerSchema()._default(1L).name(HEADER_TENANT_ID).description("Tenant ID")); // default: tenant ID is 1
    }

    /**
     * Build the Authorization authentication request header parameter
     *
     * Workaround for Knife4j <a href="https://gitee.com/xiaoym/knife4j/issues/I69QBU">Authorize not working, request header does not include parameter</a>
     *
     * @return authentication parameter
     */
    private static Parameter buildSecurityHeaderParameter() {
        return new Parameter()
                .name(HttpHeaders.AUTHORIZATION) // header name
                .description("Authentication Token") // description
                .in(String.valueOf(SecurityScheme.In.HEADER)) // request header
                .schema(new StringSchema()._default("Bearer test1").name(HEADER_TENANT_ID).description("Authentication Token")); // default: user ID is 1
    }

    /**
     * Core: custom OperationId generation rule, combining "class name prefix + method name"
     *
     * @see <a href="https://github.com/YunaiV/ruoyi-vue-pro/issues/957">app-api prefix not effective, all use admin-api</a>
     */
    private static OperationCustomizer buildOperationIdCustomizer() {
        return (operation, handlerMethod) -> {
            // 1. get controller class name (e.g. UserController)
            String className = handlerMethod.getBeanType().getSimpleName();
            // 2. extract class name prefix (remove the Controller suffix, e.g. UserController -> User)
            String classPrefix = className.replaceAll("Controller$", "");
            // 3. get the method name (e.g. list)
            String methodName = handlerMethod.getMethod().getName();
            // 4. combine to generate operationId (e.g. User_list)
            String operationId = classPrefix + "_" + methodName;
            // 5. set the custom operationId
            operation.setOperationId(operationId);
            return operation;
        };
    }

}

