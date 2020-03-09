package org.myec3.socle.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.util.DockerSecretsDatabasePasswordProcessor;
import org.myec3.socle.core.util.UtilTechException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StreamUtils;

@Configuration
@PropertySource({ "classpath:socleCore.properties", "classpath:database.properties" })
@ComponentScan(basePackages = { "org.myec3.socle.core", "org.myec3.socle.synchro.api" })
@EnableTransactionManagement
public class CoreConfig {

	private static final Log logger = LogFactory.getLog(CoreConfig.class);

	@Autowired
	private Environment env;

	@Value("${dataSource.driverClassName}")
	private String driverClassName;

	@Value("${dataSource.url}")
	private String dataSourceUrl;

	@Value("${dataSource.username}")
	private String dataSourceUsername;

	@Value("${dataSource.password:#{null}}")
	private String dataSourcePassword;

	@Value("${dataSource.password.path:#{null}}")
	private String dataSourcePasswordPath;

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
		String password = dataSourcePassword;
		if (StringUtils.isEmpty(password)){
			String pwdSecretPath = dataSourcePasswordPath;
			Resource resource = new FileSystemResource(pwdSecretPath);
			if (resource.exists()) {
				if (logger.isInfoEnabled()) {
					logger.info("Recuperation du mdp bdd depuis le secret " + pwdSecretPath);
				}
				try {
					password = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
				} catch (IOException e) {
					throw new UtilTechException("Erreur lors de l'acces au fichier " + pwdSecretPath,e);
				}
			}
		}
		else {
			logger.info("Recuperation du mdp bdd depuis la propriete dataSourcePassword");
		}

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(dataSourceUrl);
		dataSource.setUsername(dataSourceUsername);
		dataSource.setPassword(password);
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

	@Bean
	public SchedulerFactoryBean ramScheduler() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "ramScheduler");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", "5");
		properties.setProperty("org.quartz.threadPool.threadPriority", "1");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		scheduler.setQuartzProperties(properties);

		return scheduler;
	}

	@Bean
	public SchedulerFactoryBean jmsScheduler() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setJobFactory(new SpringBeanJobFactory());

		scheduler.setConfigLocation(new ClassPathResource("socleCore.properties"));
		scheduler.setWaitForJobsToCompleteOnShutdown(true);
		return scheduler;
	}

}
