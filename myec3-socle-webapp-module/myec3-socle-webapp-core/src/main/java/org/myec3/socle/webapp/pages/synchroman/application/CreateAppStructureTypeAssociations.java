package org.myec3.socle.webapp.pages.synchroman.application;

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
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.StructureTypeApplicationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class CreateAppStructureTypeAssociations extends AbstractPage {

	private static final Logger logger = LogManager.getLogger(CreateAppStructureTypeAssociations.class);

	private Application application;
	private List<StructureTypeApplication> listStructureTypeApplications;

	private GenericListEncoder<StructureTypeApplication> structureTypeApplicationEncoder;
	private StructureTypeApplication structureTypeApplicationLoop;

	@Inject
	@Service("structureTypeApplicationService")
	private StructureTypeApplicationService structureTypeApplicationService;

	@InjectPage
	private View viewPage;

	@Inject
	@Service("applicationService")
	private ApplicationService applicationService;

	@Inject
	private Messages messages;

	@Component(id = "creation_application_subscription_form")
	private Form form;

	// Template properties
	@Property
	@Persist("Flash")
	private String errorMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {

		logger.debug("onActivate");

		this.application = applicationService.findOne(id);

		if (null == this.application) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PREPARE)
	public void onPrepare() {
		logger.debug("prepare");

		// We get the list of structureType into structureTypeApplication
		this.listStructureTypeApplications = structureTypeApplicationService
				.findAllStructureTypeApplicationByApplication(this.application);
		this.structureTypeApplicationEncoder = new GenericListEncoder<StructureTypeApplication>(
				this.listStructureTypeApplications);
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		logger.debug("success");
		logger.debug("Selected applications count: " + this.listStructureTypeApplications.size());
		try {
			for (StructureTypeApplication structureTypeApplication : this.listStructureTypeApplications) {
				structureTypeApplication.setSubscribable(Boolean.TRUE);
				structureTypeApplicationService.update(structureTypeApplication);
			}
		} catch (RuntimeException ex) {
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}

		this.viewPage.setApplication(this.application);
		return this.viewPage;
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

	public GenericListEncoder<StructureTypeApplication> getStructureTypeApplicationEncoder() {
		return structureTypeApplicationEncoder;
	}

	public StructureTypeApplication getStructureTypeApplicationLoop() {
		return structureTypeApplicationLoop;
	}

	public void setStructureTypeApplicationLoop(StructureTypeApplication structureTypeApplicationLoop) {
		this.structureTypeApplicationLoop = structureTypeApplicationLoop;
	}

	public List<StructureTypeApplication> getListStructureTypeApplications() {
		return listStructureTypeApplications;
	}

	public void setListStructureTypeApplications(List<StructureTypeApplication> listStructureTypeApplications) {
		this.listStructureTypeApplications = listStructureTypeApplications;
	}
}
