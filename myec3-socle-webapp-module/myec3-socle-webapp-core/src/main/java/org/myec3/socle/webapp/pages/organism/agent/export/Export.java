package org.myec3.socle.webapp.pages.organism.agent.export;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ExportAgentCSVGeneratorService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.webapp.utils.CsvStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Page used to export a list of {@link AgentProfile}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/Export.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Nicolas Kaczmarek <nicolas.NK.kaczmarek@atosorigin.com>
 */
public class Export extends org.myec3.socle.webapp.pages.user.Export {

	private static final Logger logger = LoggerFactory.getLogger(Export.class);

	@Inject
	private Messages messages;

	@Inject
	private ComponentResources componentResources;

	@Component(id = "agent_export_form")
	private Form form;

	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Inject
	@Named("exportAgentCSVGeneratorService")
	private ExportAgentCSVGeneratorService exportAgentCSVGeneratorService;

	// Next Page
	@InjectPage
	private Report reportPage;

	// Current organism
	@Property
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

		// Clear persistent params of the current and next page
		this.clearAllPersistentParams();

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	/**
	 * 
	 * Generate a csv file by filling an hashmap. Keys are attribute of the header,
	 * values are the values for the attribute
	 * 
	 * @throws IOException
	 */
	@OnEvent(value = EventConstants.SUCCESS, component = "agent_export_form")
	public StreamResponse onSuccess() throws IOException {

		StreamResponse sr = null;

		if (super.getIsAdmin() || super.getIsGlobalManagerAgent()) {
			try {

				StringWriter sw = new StringWriter();
				List<Application> applicationsList = applicationService.findAllApplicationByStructure(organism);
				List<Organism> organismsList = Collections.singletonList(organism);

				try (CSVWriter csvWriter = new CSVWriter(sw, ExportAgentCSVGeneratorService.CSV_DEFAULT_SEPARATOR)) {
					exportAgentCSVGeneratorService.writeCsv(organismsList, applicationsList, csvWriter);
				}

				sr = new CsvStreamResponse(sw, "export_agent");

			} catch (IllegalArgumentException e) {
				logger.debug(e.getMessage());
				this.form.recordError(e.getMessage());
			} catch (Exception e) {
				logger.error("An unexpected error has occured during the generation of the file {} ", e.getMessage());
				this.form.recordError(this.messages.get("generation-exception"));
			}
		} else {
			logger.error("this user is not an admin");
			this.form.recordError(this.messages.get("not-admin-exception"));
		}

		return sr;

	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	/**
	 * Method used to clear persistent params annoted by @Persist
	 */
	public void clearPersistentParams() {
		this.componentResources.discardPersistentFieldChanges();
	}

	/**
	 * Method used to clear persistent params of the next and current page
	 */
	public void clearAllPersistentParams() {
		this.clearPersistentParams();
		this.reportPage.clearPersistentParams();
	}

}
