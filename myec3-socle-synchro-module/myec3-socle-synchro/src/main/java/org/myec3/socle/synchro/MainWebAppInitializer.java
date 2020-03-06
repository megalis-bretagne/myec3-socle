package org.myec3.socle.synchro;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.myec3.socle.synchro.config.SynchroAppConfig;
import org.myec3.socle.synchro.core.config.SynchroCoreConfig;
import org.myec3.socle.synchro.scheduler.config.SynchroSchedulerConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MainWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(SynchroCoreConfig.class, SynchroSchedulerConfig.class, SynchroAppConfig.class);
		ctx.setServletContext(servletContext);
		ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");
	}

}
