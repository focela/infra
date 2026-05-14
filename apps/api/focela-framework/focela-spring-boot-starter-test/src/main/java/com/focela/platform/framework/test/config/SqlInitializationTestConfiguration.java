package com.focela.platform.framework.test.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * Test Configuration for SQL initialization.
 *
 * Why not use org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration?
 * Because in unit tests we set spring.main.lazy-initialization to true to enable lazy initialization,
 * which prevents DataSourceInitializationConfiguration from being initialized.
 * The implementation of this class is essentially a copy of DataSourceInitializationConfiguration.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(AbstractScriptDatabaseInitializer.class)
@ConditionalOnSingleCandidate(DataSource.class)
@ConditionalOnClass(name = "org.springframework.jdbc.datasource.init.DatabasePopulator")
@Lazy(value = false) // disable lazy initialization
@EnableConfigurationProperties(SqlInitializationProperties.class)
public class SqlInitializationTestConfiguration {

	@Bean
	public DataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
																				   SqlInitializationProperties initializationProperties) {
		DatabaseInitializationSettings settings = createFrom(initializationProperties);
		return new DataSourceScriptDatabaseInitializer(dataSource, settings);
	}

	static DatabaseInitializationSettings createFrom(SqlInitializationProperties properties) {
		DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
		settings.setSchemaLocations(properties.getSchemaLocations());
		settings.setDataLocations(properties.getDataLocations());
		settings.setContinueOnError(properties.isContinueOnError());
		settings.setSeparator(properties.getSeparator());
		settings.setEncoding(properties.getEncoding());
		settings.setMode(properties.getMode());
		return settings;
	}

}
