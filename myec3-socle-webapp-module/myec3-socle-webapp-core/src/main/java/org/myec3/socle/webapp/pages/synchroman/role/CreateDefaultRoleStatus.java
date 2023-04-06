package org.myec3.socle.webapp.pages.synchroman.role;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.service.ProfileTypeRoleService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class CreateDefaultRoleStatus extends AbstractPage {
	private static final Logger logger = LogManager.getLogger(CreateDefaultRoleStatus.class);

	private Role role;
	private List<ProfileTypeRole> listProfileTypeRoles;

	private GenericListEncoder<ProfileTypeRole> profileTypeRoleEncoder;
	private ProfileTypeRole profileTypeRoleLoop;

	@Inject
	@Service("profileTypeRoleService")
	private ProfileTypeRoleService profileTypeRoleService;

	@InjectPage
	private DetailRole detailRolePage;

	@Inject
	@Service("roleService")
	private RoleService roleService;

	@Inject
	private Messages messages;

	@Component(id = "creation_default_role_status_form")
	private Form form;

	// Template properties
	@Property
	@Persist("Flash")
	private String errorMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {

		logger.debug("onActivate");

		this.role = roleService.findOne(id);

		if (null == this.role) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PREPARE)
	public void onPrepare() {
		logger.debug("prepare");

		// We get the list of profileType into profileTypeRole
		this.listProfileTypeRoles = profileTypeRoleService.findAllProfileTypeRoleByRole(this.role);
		this.profileTypeRoleEncoder = new GenericListEncoder<ProfileTypeRole>(this.listProfileTypeRoles);
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		logger.debug("success");
		logger.debug("Selected roles count: " + this.listProfileTypeRoles.size());

		try {
			for (ProfileTypeRole profileTypeRole : listProfileTypeRoles) {
				profileTypeRoleService.update(profileTypeRole);
			}
		} catch (RuntimeException ex) {
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}

		this.detailRolePage.setRole(this.role);
		return this.detailRolePage;
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

	public GenericListEncoder<ProfileTypeRole> getProfileTypeRoleEncoder() {
		return profileTypeRoleEncoder;
	}

	public ProfileTypeRole getProfileTypeRoleLoop() {
		return profileTypeRoleLoop;
	}

	public void setProfileTypeRoleLoop(ProfileTypeRole profileTypeRoleLoop) {
		this.profileTypeRoleLoop = profileTypeRoleLoop;
	}

	public List<ProfileTypeRole> getListProfileTypeRoles() {
		return listProfileTypeRoles;
	}

	public void setListProfileTypeRoles(List<ProfileTypeRole> listProfileTypeRoles) {
		this.listProfileTypeRoles = listProfileTypeRoles;
	}
}
