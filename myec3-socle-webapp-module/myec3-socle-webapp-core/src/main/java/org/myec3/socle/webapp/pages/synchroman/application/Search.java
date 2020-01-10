package org.myec3.socle.webapp.pages.synchroman.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.StructureTypeApplicationService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to search an application{@link Application}<br />
 * Redirect to SearchResult page if at least one application has been
 * found<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/application/Search.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Alice Millour
 */

public class Search extends AbstractPage {

	// @SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Service("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Inject
	@Service("structureTypeService")
	private StructureTypeService structureTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureTypeApplication} objects
	 */
	@Inject
	@Service("structureTypeApplicationService")
	private StructureTypeApplicationService structureTypeApplicationService;

	@Component(id = "application_search_form")
	private Form applicationSearchForm;

	@Inject
	private Messages messages;

	@Property
	private String searchName;

	@Property
	private StructureTypeValue structureTypeValueSelected;

	@Property
	private String searchLabel;

	@Property
	private List<Application> applicationResults;

	@Property
	private Application applicationResult;

	@InjectPage
	private SearchResult searchResultPage;

	// Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	// Form
	@OnEvent(component = "application_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		StructureType searchStructureTypeSubscription = null;
		if (structureTypeValueSelected != null) {
			searchStructureTypeSubscription = structureTypeService.findByValue(structureTypeValueSelected);
		}
		/* In case the user has entered a name */
		if (searchName != null) {
			this.applicationResult = this.applicationService.findByName(searchName);
			applicationResults = new ArrayList<Application>();
			if (this.applicationResult != null) {
				this.applicationResults.add(applicationResult);
			}
		} else {
			this.applicationResults = this.applicationService.findAllByCriteria(searchLabel,
					searchStructureTypeSubscription);
		}

		if (this.applicationResults.isEmpty()) {
			this.infoMessage = this.getMessages().get("empty-result-message");
			return null;
		}

		this.searchResultPage.setApplicationsResult(applicationResults);
		return SearchResult.class;
	}

	/**
	 * list for select StructureType
	 */
	public Map<StructureType, String> getStructureTypeValueList() {
		List<StructureType> listAvailableStructureType = this.structureTypeService
				.findAll();

		Map<StructureType, String> mapStructureType = new HashMap<StructureType, String>();
		for (StructureType structureType : listAvailableStructureType) {
			mapStructureType.put(structureType, structureType.getValue()
					.toString());
		}
		return mapStructureType;
	}

	public ValueEncoder<StructureType> getStructureTypeValueEncoder() {
		List<StructureType> listAvailableStructureType = this.structureTypeService
				.findAll();
		return new GenericListEncoder<StructureType>(
				listAvailableStructureType);
	}
}
