package org.myec3.socle.webapp.pages.customer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to create a new {@link Customer}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/customer/Create.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractPage {

	@Component(id = "creation_customer_form")
	private Form form;

	// Template properties
	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	private Customer customer;

	@Property
	private UploadedFile logo;

	@InjectPage
	private CreateSubscriptions createSubscriptions;

	@Property
	private Boolean manageCompanies;

	@OnEvent(EventConstants.ACTIVATE)
	public void onActivate() {
		this.customer = new Customer();
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "logo")
	public void validateLogo(UploadedFile logo) {
		if (null != logo) {
			if (!isAuthorizedMimeType(logo)) {
				this.form.recordError(this.getMessages().get(
						"invalid-file-type-error"));
			}
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			this.customer.setAuthorizedToManageCompanies(manageCompanies);

			// Upload logo in tmp folder
			if (this.logo != null) {
				this.uploadTmpLogo();
			}

			// Go to the newt page
			this.createSubscriptions.setCustomer(this.customer);
			this.createSubscriptions.setLogo(this.logo);
			return this.createSubscriptions;
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get(
					"recording-error-message");
			return null;
		}
	}

	public Boolean isAuthorizedMimeType(UploadedFile logo) {
		String contentType = logo.getContentType();
		if ("image/bmp".equals(contentType) || "image/gif".equals(contentType)
				|| "image/jpeg".equals(contentType)
				|| "image/png".equals(contentType)
				|| "image/x-png".equals(contentType)
				|| "image/pjpeg".equals(contentType)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void uploadTmpLogo() throws IOException {
		String targetPath = GuWebAppConstants.FILER_LOGO_PATH
				+ GuWebAppConstants.FILER_LOGO_CUSTOMERS_FOLDER
				+ GuWebAppConstants.FILER_LOGO_TMP_FOLDER + "/";

		File targetDirectory = new File(targetPath);
		targetDirectory.mkdirs();

		File copiedLogo = new File(targetPath + this.logo.getFileName());
		// Check if file already exists
		if (copiedLogo.exists()) {
			// Delete file
			FileUtils.forceDelete(copiedLogo);
		}

		this.logo.write(copiedLogo);
	}
}
