package org.myec3.socle.webapp.pages.synchroman.role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("unused")
public class View extends AbstractPage {

	private static final Logger logger = LogManager.getLogger(View.class);

	@Inject
	@Service("roleService")
	private RoleService roleService;

	private Role role;

	private String errorMessage;

	@Persist("flash")
	private String successMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		logger.debug("OnActivate with id : " + id);
		this.role = this.roleService.findOne(id);

		if (null == this.role) {
			logger.error("[OnActivate] no role was found with with id : " + id);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.role != null) ? this.role.getId() : null;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
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
