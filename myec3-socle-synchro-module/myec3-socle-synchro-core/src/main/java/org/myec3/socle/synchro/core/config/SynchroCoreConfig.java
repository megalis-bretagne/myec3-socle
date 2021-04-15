package org.myec3.socle.synchro.core.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.config.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource({ "classpath:socleCore.properties", "classpath:db.properties", "classpath:pwd.properties", "classpath:database.properties" })
@ComponentScan(basePackages = { "org.myec3.socle.core", "org.myec3.socle.synchro.core.domain",
		"org.myec3.socle.synchro.core.service" })
@EnableTransactionManagement
public class SynchroCoreConfig {

	private static final Log logger = LogFactory.getLog(CoreConfig.class);

	@Autowired
	private Environment env;

	@Value("${dataSource.driverClassName}")
	private String driverClassName;

	@Value("${dataSource.url}")
	private String dataSourceUrl;

	@Value("${dataSource.username}")
	private String dataSourceUsername;

	@Value("${dataSource.password}")
	private String dataSourcePassword;

	@Value("${dataSource.maxActive}")
	private Integer dataSourceMaxActive;

	@Value("${dataSource.maxWait}")
	private Integer dataSourceMaxWait;

	@Value("${dataSource.poolPreparedStatements}")
	private Boolean poolPreparedStatements;

	@Value("${dataSource.testOnBorrow}")
	private Boolean testOnBorrow;

	@Value("${dataSource.testWhileIdle}")
	private Boolean testWhileIdle;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "org.myec3.socle" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(dataSourceUrl);
		dataSource.setUsername(dataSourceUsername);
		dataSource.setPassword(dataSourcePassword);
		dataSource.setMaxActive(dataSourceMaxActive);
		dataSource.setMaxWait(dataSourceMaxWait);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestWhileIdle(testWhileIdle);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
		properties.setProperty("hibernate.cache.use_second_level_cache",
				env.getProperty("hibernate.cache.use_second_level_cache"));
		properties.setProperty("hibernate.cache.region.factory_class",
				env.getProperty("hibernate.cache.region.factory_class"));
		properties.setProperty("hibernate.javax.cache.provider",
				env.getProperty("hibernate.javax.cache.provider"));
		properties.setProperty("hibernate.id.new_generator_mappings",
				env.getProperty("hibernate.id.new_generator_mappings"));

		return properties;
	}
}
