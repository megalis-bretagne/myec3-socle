package org.myec3.socle.webapp.pages.organism;

import java.io.UnsupportedEncodingException;

import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display organism's API-related informations
 * {@link Organism}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/ApiManagement.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Nicolas Buisson (A602499) <nicolas.buisson@worldline.com>
 * 
 */
public class ApiManagement extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	private Organism organism;

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
		this.organism = this.organismService.findOne(id);

		if (null == this.organism) {
			return Index.class;
		}

		// Check if loggedUser can access at this organism details
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	@OnEvent(component = "generate", value = EventConstants.ACTION)
	public void onGenerate() {
		Long id = this.organism.getId();
		String password = RandomStringUtils.randomAlphanumeric(12);
		String newKey = null;
		try {
			newKey = new String(Base64.encodeBase64((id + ":" + password).getBytes()), "UTF-8");
			this.organism.setApiKey(newKey);
			this.organismService.update(this.organism);
			this.synchronizationService.notifyUpdate(this.organism);
			this.setSuccessMessage(this.getMessages().get("generate-api-key-success"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.setErrorMessage(this.getMessages().get("generate-api-key-error"));
		}
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
