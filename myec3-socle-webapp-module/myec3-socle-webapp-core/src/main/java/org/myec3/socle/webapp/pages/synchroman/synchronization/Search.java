package org.myec3.socle.webapp.pages.synchroman.synchronization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

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
	private String searchHttpStatus;

	@Property
	private String searchResourceType;

	@Property
	private String searchMethodType;

	@Property
	private String searchSynchronizationType;

	@Property
	private String searchStatut;

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

	// Form
	@OnEvent(component = "synchronization_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		if (searchStatut.equals("TOUS")) {
			this.searchStatut = null;
		}
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
						searchResourceType, searchHttpStatus,
						searchSynchronizationType, searchMethodType,
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
		List<Application> applicationSynchronizedList = new ArrayList<Application>();
		applicationSynchronizedList = this.synchronizationSubscriptionService
				.findAllApplicationsSubscribe();
		Map<Application, String> mapApplicationSynchronized = new HashMap<Application, String>();
		for (Application application : applicationSynchronizedList) {
			mapApplicationSynchronized.put(application, application.getName());
		}
		return mapApplicationSynchronized;
	}

	public GenericListEncoder<Application> getApplicationSynchronizedEncoder() {
		List<Application> applicationSynchronizedList = new ArrayList<Application>();
		applicationSynchronizedList = this.synchronizationSubscriptionService
				.findAllApplicationsSubscribe();
		GenericListEncoder<Application> encoder = new GenericListEncoder<Application>(
				applicationSynchronizedList);
		return encoder;
	}

	public Map<HttpStatus, String> getHttpStatusModel() {
		HttpStatus[] availableHttpStatus = HttpStatus.values();
		Map<HttpStatus, String> mapHttpStatus = new HashMap<HttpStatus, String>();
		for (HttpStatus httpStatus : availableHttpStatus) {
			mapHttpStatus.put(httpStatus, httpStatus.toString());
		}
		return mapHttpStatus;
	}

	public GenericListEncoder<HttpStatus> getHttpStatusEncoder() {
		HttpStatus[] availableHttpStatus = HttpStatus.values();
		List<HttpStatus> listHttpStatus = new ArrayList<HttpStatus>();
		for (HttpStatus httpStatus : availableHttpStatus) {
			listHttpStatus.add(httpStatus);
		}
		GenericListEncoder<HttpStatus> encoder = new GenericListEncoder<HttpStatus>(
				listHttpStatus);
		return encoder;
	}

	public Map<MethodType, String> getMethodTypeModel() {
		MethodType[] availableMethodType = MethodType.values();
		Map<MethodType, String> mapMethodType = new HashMap<MethodType, String>();
		for (MethodType methodType : availableMethodType) {
			mapMethodType.put(methodType, methodType.toString());
		}
		return mapMethodType;
	}

	public GenericListEncoder<MethodType> getMethodTypeEncoder() {
		MethodType[] availableMethodType = MethodType.values();
		List<MethodType> listMethodType = new ArrayList<MethodType>();
		for (MethodType methodType : availableMethodType) {
			listMethodType.add(methodType);
		}
		GenericListEncoder<MethodType> encoder = new GenericListEncoder<MethodType>(
				listMethodType);
		return encoder;
	}

	public Map<SynchronizationType, String> getSynchronizationTypeModel() {
		SynchronizationType[] availableSynchronizationType = SynchronizationType
				.values();
		Map<SynchronizationType, String> mapSynchronizationType = new HashMap<SynchronizationType, String>();
		for (SynchronizationType synchronizationType : availableSynchronizationType) {
			mapSynchronizationType.put(synchronizationType, synchronizationType
					.toString());
		}
		return mapSynchronizationType;
	}

	public GenericListEncoder<SynchronizationType> getSynchronizationTypeEncoder() {
		SynchronizationType[] availableSynchronizationType = SynchronizationType
				.values();
		List<SynchronizationType> listSynchronizationType = new ArrayList<SynchronizationType>();
		for (SynchronizationType synchronizationType : availableSynchronizationType) {
			listSynchronizationType.add(synchronizationType);
		}
		GenericListEncoder<SynchronizationType> encoder = new GenericListEncoder<SynchronizationType>(
				listSynchronizationType);
		return encoder;
	}

	public Map<ResourceType, String> getResourceTypeModel() {
		ResourceType[] availableResourceType = ResourceType.values();

		Map<ResourceType, String> mapResourceType = new HashMap<ResourceType, String>();
		for (ResourceType resourceType : availableResourceType) {
			mapResourceType.put(resourceType, resourceType.toString());
		}
		return mapResourceType;
	}

	public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
		ResourceType[] availableResourceType = ResourceType.values();
		List<ResourceType> listResourceType = new ArrayList<ResourceType>();
		for (ResourceType resourceType : availableResourceType) {
			listResourceType.add(resourceType);
		}
		GenericListEncoder<ResourceType> encoder = new GenericListEncoder<ResourceType>(
				listResourceType);
		return encoder;
	}
}
