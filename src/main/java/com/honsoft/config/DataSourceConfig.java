package com.honsoft.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	
	@Autowired
	private Environment env;

	@Bean
	@ConfigurationProperties("mysql.datasource.hikari")
	public DataSourceProperties mysqlDataSourceProperties() {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		return dataSourceProperties;
	}
	
	@Bean
	public DataSource mysqlDataSource() {
		return mysqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean 
	public DataSourceInitializer mysqlDataSourceInitializer(@Qualifier("mysqlDataSource")DataSource dataSource) {
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		//resourceDatabasePopulator.addScript(null);
		resourceDatabasePopulator.setIgnoreFailedDrops(true);
		
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
		dataSourceInitializer.setEnabled(env.getProperty("mysql.datasource.initialize", Boolean.class, false));
		
		return dataSourceInitializer;
	}
	
	@Bean
	public PlatformTransactionManager mysqlDataSourceTransactionManager(@Qualifier("mysqlDataSource")DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	
}
