package org.myec3.socle.webapp.pages.customer;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.components.CustomerForm;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;

public class Modify extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Modify.class);
	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Customer} objects
	 */
	@Inject
	@Service("customerService")
	private CustomerService customerService;

	@Component(id = "customer_form")
	private CustomerForm customerForm;

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@InjectPage
	@Property
	private ListCustomers listCustomerPage;

	@Property
	private Customer customer;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.customer = this.customerService.findOne(id);
		if (null == this.customer) {
			return false;
		}
		return true;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			this.customerService.update(this.customer);

			// If a logo has been uploaded
			if (null != this.customerForm.getLogo()) {
				this.uploadLogo();
				this.customerService.update(this.customer);
			}

			this.synchronizationService.notifyUpdate(this.customer);

		} catch (Exception e) {
			logger.error("An error has occured", e);
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		this.listCustomerPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		return this.listCustomerPage;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.customer != null) ? this.customer.getId() : null;
	}

	public void uploadLogo() throws IOException {
		String targetPath = GuWebAppConstants.FILER_LOGO_PATH + GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.CUSTOMER_FOLDER_NAME + this.customer.getId() + "/";

		File targetDirectory = new File(targetPath);
		targetDirectory.mkdirs();

		String file_name = "logo";
		String file_extension = this.customerForm.getLogo().getFileName()
				.substring(this.customerForm.getLogo().getFileName().lastIndexOf("."));

		File copiedLogo = new File(targetPath + file_name + file_extension);
		// Check if file already exists
		if (copiedLogo.exists()) {
			// Delete file
			FileUtils.forceDelete(copiedLogo);
		}

		this.customerForm.getLogo().write(copiedLogo);

		this.customer.setLogoUrl(GuWebAppConstants.FILER_LOGO_URL + GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.CUSTOMER_FOLDER_NAME + customer.getId() + "/" + file_name + file_extension);
	}
}
