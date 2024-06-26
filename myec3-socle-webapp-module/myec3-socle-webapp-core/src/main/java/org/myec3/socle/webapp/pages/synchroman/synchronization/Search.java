package org.myec3.socle.webapp.pages.synchroman.synchronization;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Search extends AbstractPage {

	// Template attributes
	@SuppressWarnings("unused")
	@Component(id = "synchronization_search_form")
	private Form synchronizationSearchForm;

	@Property
	private Date searchStartDate;

	@Property
	private Date searchEndDate;

	@Property
	private Application searchApplication;

	@Property
	private String searchStatut;

	@Property
	private String searchResourceType;

	@Property
	private String searchIsFinal;

	@Property
	private Boolean searchIsFinalValue;

	@Property
	private List<SynchronizationLog> synchronizationLogResult;

	@SuppressWarnings("unused")
	@Property
	@Persist("flash")
	private String infoMessage;

	// Pages n Services
	@Inject
	@Named("synchronizationLogService")
	private SynchronizationLogService synchronizationLogService;

	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Inject
	private Messages messages;

	@InjectPage
	private SearchResult searchResultPage;

	// Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		// INIT Search Date now() - 7 days
		LocalDate dateLastWeek = LocalDate.now().minusDays(7L);
		this.searchStartDate = Date.from(dateLastWeek.atStartOfDay()
				.atZone(ZoneId.systemDefault())
				.toInstant());
		// init end date to now
		LocalDate dateTomorrow = LocalDate.now().plusDays(1L);
		this.searchEndDate = Date.from(dateTomorrow.atStartOfDay()
				.atZone(ZoneId.systemDefault())
				.toInstant());
	}

	// Form
	@OnEvent(component = "synchronization_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		if (searchIsFinal.equals("TOUS")) {
			this.searchIsFinal = null;
			this.searchIsFinalValue = null;
		} else if (searchIsFinal.equals("EN COURS")) {
			this.searchIsFinalValue = Boolean.FALSE;
		} else {
			this.searchIsFinalValue = Boolean.TRUE;
		}

		this.synchronizationLogResult = this.synchronizationLogService
				.findAllSynchronizationLogByCriteria(searchStartDate,
						searchEndDate, searchApplication,
						searchResourceType, null,
						searchStatut, searchIsFinalValue);

		if (this.synchronizationLogResult.isEmpty()) {
			this.infoMessage = this.messages.get("empty-result-message");
			return null;
		}
		this.searchResultPage
				.setSynchronizationLogResult(synchronizationLogResult);
		return SearchResult.class;
	}

	public Map<Application, String> getApplicationSynchronizedList() {
		List<Application> applicationSynchronizedList = this.synchronizationSubscriptionService
				.findAllApplicationsSubscribe();
		Map<Application, String> mapApplicationSynchronized = new HashMap<>();
		for (Application application : applicationSynchronizedList) {
			mapApplicationSynchronized.put(application, application.getName());
		}
		return mapApplicationSynchronized;
	}

	public GenericListEncoder<Application> getApplicationSynchronizedEncoder() {
		return new GenericListEncoder<>(
				this.synchronizationSubscriptionService
						.findAllApplicationsSubscribe());
	}

	public Map<ResourceType, String> getResourceTypeModel() {
		return Stream.of(
				new AbstractMap.SimpleEntry<>(ResourceType.EMPLOYEE_PROFILE, "Employé"),
				new AbstractMap.SimpleEntry<>(ResourceType.AGENT_PROFILE, "Agent"),
				new AbstractMap.SimpleEntry<>(ResourceType.COMPANY, "Entreprise"),
				new AbstractMap.SimpleEntry<>(ResourceType.ORGANISM, "Organisme"),
				new AbstractMap.SimpleEntry<>(ResourceType.ORGANISM_DEPARTMENT, "Service"),
				new AbstractMap.SimpleEntry<>(ResourceType.ESTABLISHMENT, "Etablissement")
		).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
		return new GenericListEncoder<>(Arrays.asList(ResourceType.values()));
	}
}
