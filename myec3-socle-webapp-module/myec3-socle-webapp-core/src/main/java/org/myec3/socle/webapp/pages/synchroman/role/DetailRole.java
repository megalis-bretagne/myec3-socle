package org.myec3.socle.webapp.pages.synchroman.role;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * @author Alice Millour
 */
public class DetailRole extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private static final Logger logger = LogManager.getLogger(Modify.class);

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;
	private Role role;
	@Property
	private ProfileTypeRole profileTypeRole;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.role = this.roleService.findOne(id);
		if (null == this.role) {
			logger.error("[OnActivate] no role was found with with id : " + id);
			return Boolean.FALSE;
		}
		return true;
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

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

}
