/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p/>
 * This file is part of MyEc3.
 * <p/>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p/>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.CompanyNafCode;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.myec3.socle.core.service.InseeLegalCategoryService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.holders.PersonHolder;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

import javax.inject.Named;
import java.util.*;

/**
 * Page used during creation company process {@link Company}<br />
 * <p>
 * It's the second step to create a new company. In this step your must
 * fill<br />
 * company's informations if the company not already exists into the
 * database.<br />
 * <p>
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/InitCompany.tml<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Import(library = {"context:/static/js/custom_datepicker.js"})
public class InitCompany {

    private static Log logger = LogFactory.getLog(InitCompany.class);

    // Socle core services
    /**
     * Business Service providing methods and specifics operations on
     * {@link Company} objects
     */
    @Inject
    @Service("companyService")
    private CompanyService companyService;

    /**
     * Business Service providing methods and specifics operations on
     * {@link InseeGeoCode} objects
     */
    @Inject
    @Service("inseeGeoCodeService")
    private InseeGeoCodeService inseeGeoCodeService;

    @Inject
    @Named("establishmentService")
    private EstablishmentService establishmentService;

    @Inject
    @Service("inseeLegalCategoryService")
    private InseeLegalCategoryService inseeLegalCategoryService;

    @Inject
    private Messages messages;

    @Persist(PersistenceConstants.FLASH)
    private String successMessage;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String errorMessage;

    @Persist(PersistenceConstants.FLASH)
    private Company company;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String nic;

    @Property
    private CompanyNafCode companyNafCode;

    @Property
    private Boolean refreshPostalCode = Boolean.FALSE;

    @SuppressWarnings("unused")
    @Property
    private Boolean companyNotExists = Boolean.FALSE;

    @SuppressWarnings("unused")
    @Component
    private Submit submit;

    @Persist(PersistenceConstants.FLASH)
    private List<InseeGeoCode> citiesList;

    @InjectPage
    private org.myec3.socle.webapp.pages.company.establishment.Create establishmentPage;

    @Component(id = "modification_form")
    private Form form;

    @Persist
    private List<PersonHolder> personHolders;

    @SuppressWarnings("unused")
    @Property
    private PersonHolder personHolder;

    private CompanyWSinfo mpsWS = new MpsWsClient();

    @Inject
    private Request request;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Persist
    private Boolean isLocHallesTheme;

    public void setIsLocHallesTheme(Boolean isLocHallesTheme) {
        this.isLocHallesTheme = isLocHallesTheme;
    }

    public Boolean getIsLocHallesTheme() {
        return isLocHallesTheme;
    }

    /**
     * Initializes the form
     */
    @OnEvent(EventConstants.PREPARE_FOR_RENDER)
    public void onPrepareForRender() {
        if (this.company != null) {
            if (null == this.company.getForeignIdentifier() || !this.company.getForeignIdentifier()) {
                // french company case
                this.nic = this.company.getNic();
                List<Company> companyExists = this.companyService
                        .findAllByCriteria(null, this.company.getSiren(), null,
                                null);
                // search in mps ws if not in db
                if (companyExists.size() == 0) {
                    this.companyNotExists = Boolean.TRUE;
                    // we put service in function parameters
                    if (company.getLabel().length() == 0) {
                        try {
                            this.company = mpsWS.updateCompanyInfo(this.company, inseeLegalCategoryService);
                            logger.info("Prepare company : " + company.toString());
                            if (this.company.getEstablishments() != null
                                    && !this.company.getEstablishments().isEmpty()
                                    && this.company.getEstablishments().get(0).getDiffusableInformations() != null
                                    && this.company.getEstablishments().get(0).getDiffusableInformations()
                                    .equals(Boolean.TRUE)) {
                                this.errorMessage = this.messages.get("mps-not-diffusable-message");
                            }
                        } catch (Exception e) {
                            logger.error("Prepare company action went in error ", e);
                            this.errorMessage = this.messages.get("mps-error-message");
                        }
                    }
                    logger.info("Prepare mandataires : " + company.getResponsibles());
                    this.initializePersonsHolder(Boolean.FALSE, this.company.getResponsibles());
                } else {
                    // company in db
                    this.company = companyExists.get(0);
                    this.company.getApplications();

                    // Resets the attributes of ajaxformloop list
                    this.initializePersonsHolder(Boolean.TRUE,
                            this.company.getResponsibles());
                }

                // find nafCode
                logger.info("Check ape code : " + company.getApeCode());
                if (this.companyNafCode == null) {
                    CompanyNafCode[] nafCodes = CompanyNafCode.values();
                    for (CompanyNafCode nafCode : nafCodes) {
                        if (nafCode.getApeCode().equalsIgnoreCase(
                                this.company.getApeCode()))
                            this.companyNafCode = nafCode;
                    }
                }
            } else {
                // Resets the attributes of ajaxformloop list
                this.initializePersonsHolder(Boolean.FALSE,
                        this.company.getResponsibles());
            }
        }
    }

    @OnEvent(EventConstants.ACTIVATE)
    public Object Activation() {
        this.company = company;
        if (this.company == null) {
            logger.warn("Enterring in init company page with a company = null. Redirect user to siren page...");
            return Siren.class;
        }
        return Boolean.TRUE;
    }

    @OnEvent(EventConstants.PASSIVATE)
    public void onPassivate() {
        this.company = company;
    }

    /**
     * validate form
     */
    @OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
    public void onValidate() {

        if (null == this.company.getLabel() || this.company.getLabel().isEmpty()) {
            this.form.recordError(this.messages.get("label-required"));
        }

        if (null == this.company.getCreationDate() && this.company.getId() == null) {
            this.form.recordError(this.messages.get("creationDate-required"));
        }

        if (this.company.getId() == null && (this.personHolder == null || this.personHolders.isEmpty())) {
            this.form.recordError(this.messages.get("responsibles-required"));
        }

        if ((this.company.getEmail() == null || this.company.getEmail().isEmpty())
                && this.company.getId() == null) {
            this.form.recordError(this.messages.get("email-required"));
        }

        if (!this.company.getForeignIdentifier().booleanValue()
                && (this.company.getSiren() == null || this.company.getSiren().isEmpty())) {
            this.form.recordError(this.messages.get("siren-required"));
        }

    }

    // Form events
    @OnEvent(EventConstants.SUCCESS)
    public Object onSuccess() {
        try {
            List<Person> personsToCreate = new ArrayList<Person>();
            List<Person> newListOfResponsibles = new ArrayList<Person>();

            if (this.personHolders != null) {
                for (PersonHolder holder : this.personHolders) {
                    if (holder.getPerson().getCivility() == null) {
                        holder.getPerson().setCivility(Civility.MR);
                    }
                    if (this.responsibleIsValid(holder.getPerson())) {
                        if (holder.isNew()) {
                            personsToCreate.add(holder.getPerson());
                        } else {
                            newListOfResponsibles.add(holder.getPerson());
                        }
                    }
                }
            }

            // For new responsibles
            for (Person personAdded : personsToCreate) {
                personAdded.setId(null);
                personAdded.setCompany(this.company);

                personAdded.setName(personAdded.getLastname() + " "
                        + personAdded.getFirstname());
                if (personAdded.getType() == null) {
                    personAdded.setType("PP");
                }

                newListOfResponsibles.add(personAdded);
            }
            // FIXME add enum into Company Bean instead of 2 strings, be
            // careful of sync. existing applications
            if (this.companyNafCode != null) {
                this.company.setApeCode(companyNafCode.getApeCode());
                this.company.setApeNafLabel(this
                        .getApeNaflabelValue(this.companyNafCode.name()));
            }
            // if french company
            if (null == this.company.getForeignIdentifier()
                    || !this.company.getForeignIdentifier()) {
                // set inseeCode via select
                if (null == this.company.getInsee()) {
                    this.company.setInsee(this.company.getAddress()
                            .getCity());
                }
            }

            if (this.nic != null) {
                this.company.setNic(this.nic);
            }

            if (this.company.getId() != null) {
                this.companyService.populateCollections(company);

                // If we have establishments, populate the array
                if (this.company.getEstablishments().size() > 0) {
                    List<Establishment> myEstablishments = establishmentService
                            .findAllEstablishmentsByCompany(this.company);
                    for (Establishment isHeadOffice : myEstablishments) {
                        if (isHeadOffice.getIsHeadOffice().equals(Boolean.TRUE)) {
                            this.company.setNic(null);
                        }
                    }

                }
            }

            this.company.setResponsibles(newListOfResponsibles);
            this.establishmentPage.setCompany(this.company);
            this.establishmentPage.setNic(this.nic);
        } catch (Exception e) {
            this.errorMessage = this.messages
                    .get("recording-error-message");
            logger.error("error during init company success : ", e);
            return null;
        }

        // step 2
        this.establishmentPage.setSuccessMessage(this.messages
                .get("recording-success-message"));
        this.establishmentPage.setIsLocHallesTheme(this.isLocHallesTheme);
        this.establishmentPage.setFromCompanyCreation(true);
        logger.trace("InitCompany succeeded, going for establishment creation...");
        return this.establishmentPage;
    }

    /**
     * disable fields if company already exists
     *
     * @return true if company exists
     */
    public Boolean getIsEditionDisabled() {
        if (this.company.getSiren() != null) {
            List<Company> isCompanyExists = this.companyService
                    .findAllByCriteria(null, this.company.getSiren(), null,
                            null);

            if (null != isCompanyExists) {
                if (isCompanyExists.size() > 0) {
                    if (null != isCompanyExists.get(0).getLabel()) {
                        if (isCompanyExists.get(0).getLabel() != "") {
                            logger.debug("company already exists : "
                                    + isCompanyExists.get(0).getLabel());
                            return Boolean.TRUE;
                        }
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * list for select NafCode
     */
    public Map<CompanyNafCode, String> getListOfCompanyNafCode() {
        Map<CompanyNafCode, String> companies = new LinkedHashMap<CompanyNafCode, String>();
        CompanyNafCode[] companiesList = CompanyNafCode.values();
        for (CompanyNafCode companyNafCode : companiesList) {
            companies.put(
                    companyNafCode,
                    companyNafCode
                            + " - "
                            + this.getApeNaflabelValue(companyNafCode.name()
                            .toString()));
        }

        return companies;
    }

    /**
     * @return the current web context path
     */
    public String getWebContext() {
        return request.getContextPath();
    }

    public ValueEncoder<CompanyNafCode> getNafCodeEncoder() {
        CompanyNafCode[] comp = CompanyNafCode.values();
        List<CompanyNafCode> cmps = new ArrayList<CompanyNafCode>();
        for (CompanyNafCode companyNafCode : comp) {
            cmps.add(companyNafCode);
        }
        return new GenericListEncoder<CompanyNafCode>(cmps);
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
                throw new IllegalArgumentException("Received key \"" + key
                        + "\" which has no counterpart in this collection: "
                        + personHolders);
            }
        };
    }

    /**
     * Event when we add person in ajaxformlooop
     *
     * @return person
     */
    @OnEvent(value = EventConstants.ADD_ROW, component = "person_list")
    public PersonHolder onAddRow() {
        Person newPerson = new Person();
        PersonHolder newPersonHolder = new PersonHolder(newPerson,
                Boolean.TRUE, Boolean.FALSE, 0 - System.nanoTime());
        this.personHolders.add(newPersonHolder);
        return newPersonHolder;
    }

    /**
     * Event when we re removing row in ajaxform loop
     */
    @OnEvent(value = EventConstants.REMOVE_ROW, component = "person_list")
    public void removePerson(PersonHolder personHolder) {
        this.personHolders.remove(personHolder);
    }

    /**
     * find in app.properties naflabel
     *
     * @param key
     * @return NafLabel
     */
    public String getApeNaflabelValue(String key) {
        return this.messages.get(key);
    }

    public Map<String, String> getCitiesList() {
        Map<String, String> citiesMap = new HashMap<String, String>();

        if (null != this.citiesList) {
            for (InseeGeoCode inseeGeoCode : this.citiesList) {
                citiesMap.put(inseeGeoCode.getInseeCode(),
                        inseeGeoCode.getLabel());
            }
            if (0 == this.citiesList.size()) {
                citiesMap.put("", this.messages.get("no-town-label"));
            }
        }
        return citiesMap;
    }

    /**
     * @param citiesList
     */
    public void setCitiesList(List<InseeGeoCode> citiesList) {
        this.citiesList = citiesList;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Boolean responsibleIsValid(Person responsible) {
        if ((responsible.getFirstname() != null)
                && (responsible.getLastname() != null)
                && (responsible.getCivility() != null)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String getSuccessMessage() {
        return this.successMessage;
    }

    public void setSuccessMessage(String message) {
        this.successMessage = message;
    }

    void onSelectedFromSubmit() {
        this.refreshPostalCode = Boolean.FALSE;
    }

    public List<PersonHolder> getPersonHolders() {
        return personHolders;
    }

    public void setPersonHolders(List<PersonHolder> personHolders) {
        this.personHolders = personHolders;
    }

    public Boolean getLabelDisplay() {

        if (this.company.getLabel() != null && this.company.getLabel() != "") {
            return Boolean.FALSE;

        } else {
            return Boolean.TRUE;
        }
    }

    public Boolean getLegalCategoryDisplay() {

        if (this.company.getLegalCategory() != null && this.company.getLegalCategory().toString() != "") {
            return Boolean.FALSE;

        } else {
            return Boolean.TRUE;
        }
    }

    public Boolean getNafLabelDisplay() {

        if (this.company.getApeCode() != null && this.company.getApeCode() != "") {
            return Boolean.TRUE;

        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean getResponsablesDisplay() {

        if (this.company.getResponsibles().isEmpty()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }

    public Boolean getCreationDateDisplay() {

        if (this.company.getCreationDate() == null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean getRadiationDateDisplay() {

        if (this.company.getRadiationDate() == null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean getPersonEmailDisplay() {

        if (this.personHolder.getPerson().getEmail() == null
                && this.company.getId() == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public Boolean getPersonPhoneDisplay() {

        if (this.personHolder.getPerson().getPhone() == null
                && this.company.getId() == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void initializePersonsHolder(Boolean personsExistInDatabase,
                                        List<Person> responsibles) {
        // Resets the attributes of ajaxformloop list
        if (this.personHolders == null) {
            this.personHolders = new ArrayList<PersonHolder>();
            if (responsibles != null) {
                if (personsExistInDatabase) {
                    for (Person person : responsibles) {
                        this.personHolders.add(new PersonHolder(person,
                                Boolean.FALSE, Boolean.FALSE, person.getId()));
                    }
                } else {
                    for (Person person : responsibles) {
                        this.personHolders.add(new PersonHolder(person,
                                Boolean.TRUE, Boolean.FALSE, 0 - System
                                .nanoTime()));
                    }
                }
            }
        }
    }
}
