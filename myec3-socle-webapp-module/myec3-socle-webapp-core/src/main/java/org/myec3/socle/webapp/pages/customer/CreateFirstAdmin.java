package org.myec3.socle.webapp.pages.customer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.AdminProfileService;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

public class CreateFirstAdmin extends AbstractPage {

	private static final Log logger = LogFactory.getLog(CreateFirstAdmin.class);

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business service providing methods and specifics operations on
	 * {@link ProfileType} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Customer} objects
	 */
	@Inject
	@Named("customerService")
	private CustomerService customerService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AdminProfile} objects
	 */
	@Inject
	@Named("adminProfileService")
	private AdminProfileService adminProfileService;

	@Component(id = "creation_customer_form")
	private Form form;

	@Inject
	private ComponentResources componentResources;

	@InjectPage
	private CreateSubscriptions createSubscriptionsPage;

	@InjectPage
	private Index indexPage;

	@SuppressWarnings("unused")
	@Property
	private String errorMessage;

	@Persist
	private Customer customer;

	@Persist
	private UploadedFile logo;

	private AdminProfile adminProfile;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		if (this.customer == null) {
			return Create.class;
		}

		if (this.adminProfile == null) {
			this.adminProfile = new AdminProfile();
			this.adminProfile.setUser(new User());
			this.adminProfile.setAddress(new Address());
		}
		return Boolean.TRUE;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "email")
	public void validateEmail(String email) {
		if (this.adminProfileService.emailAlreadyExists(email, this.adminProfile)) {
			this.form.recordError(this.getMessages().get("recording-duplicate-error-message"));
			logger.info(this.getMessages().get("recording-duplicate-error-message"));
		}

		if (this.adminProfileService.usernameAlreadyExists(email, this.adminProfile)) {
			this.form.recordError(this.getMessages().get("recording-username-duplicate-error-message"));
			logger.info(this.getMessages().get("recording-username-duplicate-error-message"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {

			// Create Customer
			this.customerService.create(this.customer);

			// Create User
			User user = this.adminProfile.getUser();
			user.setCivility(Civility.MR);
			user.setLabel(user.getFirstname() + " " + user.getLastname());
			user.setName(user.getLabel());
			user.setUsername(adminProfile.getEmail().toLowerCase());

			// password for mail
			String password = this.userService.generatePassword();
			user.setPassword(this.userService.generateHashPassword(password));

			this.userService.create(user);

			// Create AdminProfile
			// Profile Type Admin
			ProfileType profileType = this.profileTypeService.findByValue(ProfileTypeValue.ADMIN);

			this.adminProfile.setName(user.getName());
			this.adminProfile.setLabel(user.getLabel());
			this.adminProfile.setCustomer(this.customer);
			this.adminProfile.setUser(user);
			this.adminProfile.setProfileType(profileType);

			// add admin customer role for each application
			List<Role> listRoles = new ArrayList<Role>();
			for (Application application : this.customer.getApplications()) {
				Role role = this.roleService
						.findBasicRoleByProfileTypeAndApplication(this.adminProfile.getProfileType(), application);
				if (role != null) {
					listRoles.add(role);
				}
			}
			this.adminProfile.setRoles(listRoles);
			this.adminProfileService.create(this.adminProfile);

			// If a logo has been uploaded
			if (null != this.logo) {
				this.uploadLogo();
				this.customerService.update(this.customer);
			}

			// Synchronize creation to external applications
			this.notifyAllApplications();

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}
		// Clean persistant fields
		this.componentResources.discardPersistentFieldChanges();
		this.createSubscriptionsPage.discardPersistentFields();
		this.indexPage.setSuccessMessage(this.getMessages().get("recording-customer-success-message"));
		return Index.class;
	}

	public void uploadLogo() throws IOException {
		// retrive tmp logo on filer
		String tmpFileLogoPath = GuWebAppConstants.FILER_LOGO_PATH + GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.FILER_LOGO_TMP_FOLDER + "/" + this.logo.getFileName();

		File tmpFile = new File(tmpFileLogoPath);
		byte[] tmpByte = FileUtils.readFileToByteArray(tmpFile);

		String file_extension = this.logo.getFileName().substring(this.logo.getFileName().lastIndexOf("."));
		String file_name = "logo";

		String targetPath = GuWebAppConstants.FILER_LOGO_PATH + GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.CUSTOMER_FOLDER_NAME + this.customer.getId() + "/";

		File targetFolder = new File(targetPath);
		targetFolder.mkdirs();

		File copiedLogo = new File(targetPath + file_name + file_extension);
		FileUtils.writeByteArrayToFile(copiedLogo, tmpByte);

		this.customer.setLogoUrl(GuWebAppConstants.FILER_LOGO_URL + GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.CUSTOMER_FOLDER_NAME + customer.getId() + "/" + file_name + file_extension);

		// Delete tmp file
		FileUtils.forceDelete(tmpFile);
	}

	public void notifyAllApplications() {

		// Synchronize the new Customer
		this.synchronizationNotificationService.notifyCreation(this.customer);

		// Synchronize the new admin Profile
		this.synchronizationNotificationService.notifyCreation(this.adminProfile);
	}

	public AdminProfile getAdminProfile() {
		return adminProfile;
	}

	public void setAdminProfile(AdminProfile adminProfile) {
		this.adminProfile = adminProfile;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}
}
