package org.myec3.socle.webapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.tapestry5.spring.TapestrySpringFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class MainWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		// Init param
		container.setInitParameter("tapestry.app-package", "org.myec3.socle.webapp");
		container.setInitParameter("tapestry.use-external-spring-context", "true");

		container.setInitParameter("encoding", "UTF-8");

		container.addFilter("charsetFilter", CharacterEncodingFilter.class);
		container.getFilterRegistration("charsetFilter")
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		container.addFilter("JpaFilter", OpenEntityManagerInViewFilter.class);
		container.getFilterRegistration("JpaFilter")
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		container.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
		container.getFilterRegistration("springSecurityFilterChain")
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		container.addFilter("app", TapestrySpringFilter.class);
		container.getFilterRegistration("app").addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true,
				"/*");

		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.scan("org.myec3.socle.webapp");
		container.addListener(new ContextLoaderListener(context));

		ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

	}

}
