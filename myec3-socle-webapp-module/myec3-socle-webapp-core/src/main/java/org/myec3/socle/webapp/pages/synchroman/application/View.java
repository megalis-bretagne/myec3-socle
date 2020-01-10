package org.myec3.socle.webapp.pages.synchroman.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class View extends AbstractPage {

	private static final Logger logger = LogManager.getLogger(View.class);

	@Inject
	@Service("applicationService")
	private ApplicationService applicationService;

	private Application application;

	private String errorMessage;

	@Persist("flash")
	private String successMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		logger.debug("OnActivate with id : " + id);
		this.application = this.applicationService.findOne(id);

		if (null == this.application) {
			logger.error("[OnActivate] no application was found with with id : " + id);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.application != null) ? this.application.getId() : null;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
}
