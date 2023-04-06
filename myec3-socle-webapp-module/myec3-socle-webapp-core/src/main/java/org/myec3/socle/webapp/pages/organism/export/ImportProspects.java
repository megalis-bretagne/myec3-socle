package org.myec3.socle.webapp.pages.organism.export;

import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.util.CsvHelper;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page used during prospect organism{@link Organism} import process.<br />
 * 
 * It's an entry point for uploading csv file of prospect organisms<br />
 * and adding them into organisms database.<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle/webapp/pages/organism/ImportProspects.tml
 * 
 * @author Ludovic Lavigne <ludovic.lavigne@atos.net>
 */
public class ImportProspects extends AbstractPage {
	
	private static final Logger logger = LoggerFactory.getLogger(ImportProspects.class);
	private static final int HEADER_LENGTH = 21;
	private static final int MIN_NB_LINES = 2;
	private static final String PROSPECTS_FILENAME = "prospects";
	
	@Inject
	private Messages messages;
	
	@Inject
	private ComponentResources componentResources;
	
	@Component(id = "organism_import_form")
	private Form form;
	
	@Inject
	@Service("organismService")
	private OrganismService organismService;
	
	// File uploaded
	@Property
	private UploadedFile uploadedFile;

	// File writed on the filer
	private File tmpFile;
	
	@InjectPage
	private Report reportPage;
	
	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}
	
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		// Check if loggedUser can access at this page
		//return this.hasRights(this.organism);
		return Boolean.TRUE;
	}
	
	@OnEvent(value = EventConstants.VALIDATE, component = "organism_import_form")
	public void onValidate() throws IOException {
		try {

			// We check that file uploaded is not null
			if (null != this.uploadedFile) {

				// Check file size
				if (this.uploadedFile.getSize() > GuWebAppConstants.IMPORT_MAX_FILE_SIZE) {
					this.form.recordError(this.messages
							.get("file-invalid-size-error"));
				}

				// Check mime type
				if (!CsvHelper.getAuthorizedMimeType().contains(
						this.uploadedFile.getContentType().toString())) {
					logger.warn("Wrong MIME type detected: " + this.uploadedFile.getContentType().toString());
					this.form.recordError(this.messages.get("file-mime-type-error"));
				}

				// Check file extension
				String fileName = this.uploadedFile.getFileName();
				String fileExtension = fileName.substring(
						fileName.lastIndexOf("."), fileName.length());
				if (!CsvHelper.isCsvExtension(fileExtension)) {
					this.form.recordError(this.messages
							.get("file-extension-error"));
				}

				// We read the file only if there are no errors
				if (!this.form.getHasErrors()) {

					// Retrieve number of lines
					int nbLines = CsvHelper.countLines(this.uploadedFile
							.getStream());

					// Check max number of lines allowed
					if (nbLines > GuWebAppConstants.IMPORT_MAX_LINES) {
						this.form.recordError(this.messages
								.get("file-max-lines-error"));
					}

					// Check that file contains more than 1 line
					if (nbLines < MIN_NB_LINES) {
						this.form.recordError(this.messages
								.get("file-min-lines-error"));
					} else {
						// Check headers
						this.checkFileHeaders();
					}
				}

				if (!this.form.getHasErrors()) {
					// Write file uploaded on the filer
					this.writeFile();
				}
			}
		} catch (IllegalArgumentException e) {
			logger.debug(e.getMessage());
			this.form.recordError(e.getMessage());
		} catch (IOException e) {
			logger.error("An error has occured during writing the file on filer : "
					+ e.getMessage());
			this.form.recordError(this.messages.get("file-write-exception"));
		} catch (Exception e) {
			logger.error("An unexpected error has occured during the validation of the file to upload");
			this.form.recordError(this.messages.get("validate-exception"));
		}

	}

	@OnEvent(value = EventConstants.SUCCESS, component = "organism_import_form")
	public Object onSuccess() {
		// Set params to the next page
		//this.reportPage.setUploadedFile(this.tmpFile);

		// Clear persistent params of this page
		this.clearPersistentParams();

		// Return the next page
		return this.reportPage;
	}
//	@OnEvent(EventConstants.PASSIVATE)
//	public Long onPassivate() {
//		return (this.organism != null) ? this.organism.getId() : null;
//	}

	/**
	 * Method called by tapestry in case of error during upload the file
	 * 
	 * @throws IOException
	 */
	Object onUploadException(FileUploadException ex) throws IOException {
		this.form.recordError(this.messages.get("file-upload-exception"));
		return this;
	}

	/**
	 * Write file on filer
	 */
	public void writeFile() throws IOException {
		byte[] bytes = IOUtils.toByteArray(this.uploadedFile.getStream());
		File file = new File(GuWebAppConstants.FILER_IMPORT_PATH
				+ PROSPECTS_FILENAME + CsvHelper.getExtension());
		FileUtils.writeByteArrayToFile(file, bytes);
		this.tmpFile = file;
	}

	/**
	 * Retrieve file headers and check that headers expected are present
	 * 
	 * @throws Exception
	 */
	public void checkFileHeaders() throws Exception {
		String[] headers = CsvHelper.getHeaders(this.uploadedFile.getStream());
		if (headers.length != HEADER_LENGTH) {
			throw new IllegalArgumentException(
					this.messages.get("file-header-length-error-message"));
		}

		for (int i = 0; i < headers.length; i++) {
			if (!headers[i].trim().equalsIgnoreCase(
					this.messages.get("organism-import-header-column-" + i).trim())) {
				throw new IllegalArgumentException(
						this.messages.get("file-header-title-error-message"));
			}
		}
	}

	/**
	 * Method used to clear persistent params annoted by @Persist
	 */
	public void clearPersistentParams() {
		this.componentResources.discardPersistentFieldChanges();
		this.uploadedFile = null;
	}

	/**
	 * Method used to clear persistent params of the next and current page
	 */
	public void clearAllPersistentParams() {
		this.clearPersistentParams();
		//this.reportPage.clearPersistentParams();
	}
}
