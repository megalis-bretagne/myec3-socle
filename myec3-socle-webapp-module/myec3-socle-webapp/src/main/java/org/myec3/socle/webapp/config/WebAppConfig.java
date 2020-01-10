package org.myec3.socle.webapp.config;

import org.myec3.socle.config.CoreConfig;
import org.myec3.socle.synchro.core.config.SynchroCoreConfig;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "org.myec3.socle.webapp.controller" })
@Import({ CoreConfig.class, WebSecurityConfig.class, SynchroCoreConfig.class})
@ImportResource("classpath:pwdExpirationNotificationMyec3Context.xml")
public class WebAppConfig implements WebMvcConfigurer {

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		reloadableResourceBundleMessageSource.setBasename("classpath:init");
		reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
		return reloadableResourceBundleMessageSource;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").resourceChain(true)
				.addResolver(new PathResourceResolver());
		registry.addResourceHandler("/static/**/*").addResourceLocations("/static/");
	}

	@Bean
	public PropertiesFactoryBean configProperties() {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("app.properties"));
		propertiesFactoryBean.setFileEncoding("UTF-8");
		return propertiesFactoryBean;
	}

}
