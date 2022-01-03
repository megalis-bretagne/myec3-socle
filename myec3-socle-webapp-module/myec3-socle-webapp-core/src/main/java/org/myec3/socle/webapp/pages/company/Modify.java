/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.company;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.CompanyNafCode;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.PersonService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.holders.PersonHolder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

import javax.inject.Named;
import java.util.*;

/**
 * Page used to modify a company {@link Company}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/Modify.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * 
 */
@Import(library = { "context:/static/js/custom_datepicker.js" })
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
	 * {@link Company} objects
	 */
	@Inject
	@Named("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on {@link Person}
	 * objects
	 */
	@Inject
	@Named("personService")
	private PersonService personService;

	@Property
	@Persist
	private List<PersonHolder> personHolders;

	@SuppressWarnings("unused")
	@Property
	private PersonHolder personHolder;

	@InjectPage
	private DetailCompany detailCompanyPage;

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Property
	private Company company;

	@Property
	private CompanyNafCode companyNafCode;

	@Component(id = "modification_form")
	private Form form;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private Request request;

	private CompanyWSinfo mpsWS = new MpsWsClient();

	/**
	 * Initialize the form
	 */
	@OnEvent(EventConstants.PREPARE_FOR_RENDER)
	public void onPrepareForRender() {

		// Get all persons - ask business service to find them (from the
		// database)
		List<Person> persons = this.company.getResponsibles();

		this.personHolders = new ArrayList<>();
		for (Person person : persons) {
			this.personHolders.add(new PersonHolder(person, Boolean.FALSE, Boolean.FALSE, person.getId()));
		}
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return this.detailCompanyPage;
	}

	/**
	 * @param id : organism id
	 * @return : current page if logged user has rights to access at this page else
	 *         return to Index
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// Retrieve the company by it's ID
		this.company = this.companyService.findOne(id);
		if (null == this.company) {
			return false;
		}

		// Retrieve company's nafCode
		if (this.companyNafCode == null) {
			CompanyNafCode[] nafCodes = CompanyNafCode.values();
			for (CompanyNafCode nafCode : nafCodes) {
				if (nafCode.getApeCode().equalsIgnoreCase(this.company.getApeCode()))
					this.companyNafCode = nafCode;
			}
		}

		// Check if loggedUser can access to this user
		return this.hasRights(this.company);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.company != null) ? this.company.getId() : null;
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		if (BooleanUtils.isFalse(this.company.getForeignIdentifier()) && BooleanUtils.isFalse(this.companyService.isSiretValid(this.company.getSiren(), this.company.getNic()))) {
			this.form.recordError(this.getMessages().get("invalid-siren-nic-error"));
		} else if (BooleanUtils.isFalse(this.company.getForeignIdentifier()) && !mpsWS.companyExist(this.company)) {
			this.form.recordError(this.getMessages().get("invalid-company-error"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			List<Person> personsToCreate = new ArrayList<>();
			List<Person> personsToDelete = new ArrayList<>();
			List<Person> personsToUpdate = new ArrayList<>();

			List<Person> newListOfResponsibles = new ArrayList<>();

			for (PersonHolder holder : personHolders) {
				if (holder.getPerson().getType() == null) {
					holder.getPerson().setType("PP");
				}
				if (holder.isNew()) {
					personsToCreate.add(holder.getPerson());
				} else if (holder.isDeleted()) {
					personsToDelete.add(holder.getPerson());
				} else {
					personsToUpdate.add(holder.getPerson());
				}
			}

			for (Person personUpdated : personsToUpdate) {
				newListOfResponsibles.add(personUpdated);
			}

			// For new responsibles
			for (Person personAdded : personsToCreate) {
				personAdded.setId(null);
				personAdded.setCompany(this.company);
				personAdded.setName(personAdded.getLastname() + " " + personAdded.getFirstname());
				newListOfResponsibles.add(personAdded);
			}

			// For deleted responsibles
			for (Person personRemoved : personsToDelete) {
				this.company.removeResponsible(personRemoved);
				this.personService.delete(personRemoved);
			}

			// FIXME add enum into Company Bean instead of 2 strings, be
			// careful of sync. existing applications
			if (companyNafCode != null) {
				this.company.setApeCode(companyNafCode.getApeCode());
				this.company.setApeNafLabel(this.getApeNaflabelValue(companyNafCode.name()));
			}

			// Add responsibles
			this.company.clearResponsibles();
			this.company.addResponsibles(newListOfResponsibles);

			// Update the company
			this.companyService.update(this.company);

			// Send notification to external applications
			this.synchronizationService.notifyUpdate(this.company);

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			logger.error(e);
			return null;
		}

		this.detailCompanyPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.detailCompanyPage.setCompany(this.company);
		this.componentResources.discardPersistentFieldChanges();
		return this.detailCompanyPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.detailCompanyPage.setCompany(this.company);
		this.componentResources.discardPersistentFieldChanges();
		return View.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		super.initUser();
	}

	@SuppressWarnings("rawtypes")
	public ValueEncoder getEncoder() {
		return new ValueEncoder<PersonHolder>() {

			@Override
			public String toClient(PersonHolder value) {
				Long key = value.getKey();
				return key.toString();
			}

			@Override
			public PersonHolder toValue(String keyAsString) {
				Long key = new Long(keyAsString);
				for (PersonHolder holder : personHolders) {
					if (holder.getKey().equals(key)) {
						return holder;
					}
				}
				throw new IllegalArgumentException(
						"Received key \"" + key + "\" which has no counterpart in this collection: " + personHolders);
			}
		};
	}

	/**
	 * Event triggered when you add a responsible
	 * 
	 * @return a personHolder
	 */
	@OnEvent(value = EventConstants.ADD_ROW, component = "person_list")
	public PersonHolder onAddRow() {
		Person newPerson = new Person();
		PersonHolder newPersonHolder = new PersonHolder(newPerson, Boolean.TRUE, Boolean.FALSE, 0 - System.nanoTime());
		this.personHolders.add(newPersonHolder);
		return newPersonHolder;
	}

	/**
	 * Event when we re removing row in ajaxform loop
	 * 
	 * @param personHolder
	 */
	@OnEvent(value = EventConstants.REMOVE_ROW, component = "person_list")
	public void removePerson(PersonHolder personHolder) {
		int index = this.personHolders.indexOf(personHolder);
		PersonHolder holder = this.personHolders.get(index);
		// If the person is new, remove them from the list. Else, flag them to
		// be deleted from the database.
		if (holder.isNew()) {
			this.personHolders.remove(personHolder);
		} else {
			holder.setDeleted(Boolean.TRUE);
		}
	}

	/**
	 * @param key
	 * @return the label of NafCode from app.properties
	 */
	public String getApeNaflabelValue(String key) {
		return this.getMessages().get(key);
	}

	/**
	 * list for select NafCode
	 */
	public Map<CompanyNafCode, String> getListOfCompanyNafCode() {
		Map<CompanyNafCode, String> companies = new LinkedHashMap<>();
		CompanyNafCode[] companiesList = CompanyNafCode.values();
		for (CompanyNafCode nafCode : companiesList) {
			companies.put(nafCode,
					nafCode + " - " + this.getApeNaflabelValue(nafCode.name()));
		}

		return companies;
	}

	public Boolean getResponsablesDisplay() {

		if (this.company.getResponsibles().isEmpty() || BooleanUtils.isTrue(this.company.getForeignIdentifier())) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}

	}

	public Boolean getPostalAddressDisplay() {
		if (this.company.getAddress().getPostalAddress() != null &&
				!StringUtils.EMPTY.equals(this.company.getAddress().getPostalAddress()) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getPostalCodeDisplay() {
		if (this.company.getAddress().getPostalCode() != null &&
				!StringUtils.EMPTY.equals(this.company.getAddress().getPostalCode()) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	public Boolean getCityDisplay() {
		if (this.company.getAddress().getCity() != null &&
				!StringUtils.EMPTY.equals(this.company.getAddress().getCity()) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getCantonDisplay() {
		if (this.company.getAddress().getCanton() != null &&
				!StringUtils.EMPTY.equals(this.company.getAddress().getCanton()) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Enable Update Siren for Admin only
	 * @return
	 */
	public Boolean getDisableSiren() {
		return !this.getIsAdmin();
	}

	public Boolean getLegalCategoryDisplay() {
		if ((this.company.getLegalCategory() != null &&
				!StringUtils.isEmpty(this.company.getLegalCategory().getLabel())) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getApeCodeDisplay() {
		if (this.companyNafCode != null &&
				!StringUtils.EMPTY.equals(this.companyNafCode.getApeCode()) &&
				BooleanUtils.isFalse(this.company.getForeignIdentifier())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getAdministrativeStateValueDisplay() {
		if (this.company.getAdministrativeState().getAdminStateValue() != null
				&& !StringUtils.EMPTY.equals(this.company.getAdministrativeState().getAdminStateValue().getLabel())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getCreationDateDisplay() {
		if (this.company.getCreationDate() != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Boolean getRadiationDateDisplay() {
		if (this.company.getRadiationDate() != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public ValueEncoder<CompanyNafCode> getNafCodeEncoder() {
		CompanyNafCode[] comp = CompanyNafCode.values();
		List<CompanyNafCode> cmps = new ArrayList<>();
		Collections.addAll(cmps, comp);
		return new GenericListEncoder<>(cmps);
	}
}
