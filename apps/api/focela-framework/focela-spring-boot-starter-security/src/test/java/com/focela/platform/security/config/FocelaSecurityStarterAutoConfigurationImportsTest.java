package com.focela.platform.security.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FocelaSecurityStarterAutoConfigurationImportsTest {

    private static final Path AUTO_CONFIGURATION_IMPORTS = Path.of(
            "src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");

    @Test
    void importsUseAutoConfigurationEntryPoints() throws Exception {
        List<String> imports = Files.readAllLines(AUTO_CONFIGURATION_IMPORTS).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        assertThat(imports).containsExactly(
                "com.focela.platform.security.config.FocelaSecurityAutoConfiguration",
                "com.focela.platform.security.config.FocelaSecurityFilterChainAutoConfiguration",
                "com.focela.platform.operatelog.config.FocelaOperateLogAutoConfiguration");
        assertThat(imports).allSatisfy(importClass -> assertThat(importClass).endsWith("AutoConfiguration"));
    }
}
