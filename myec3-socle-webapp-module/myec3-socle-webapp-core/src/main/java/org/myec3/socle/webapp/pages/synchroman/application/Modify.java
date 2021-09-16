/**
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
package org.myec3.socle.webapp.pages.synchroman.application;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplication;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.StructureApplicationService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.webapp.pages.AbstractPage;

import java.util.List;

/**
 * Page used to modify the application{@link Application}<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/application/Modify.tml<br
 * />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 */

@SuppressWarnings("unused")
public class Modify extends AbstractPage {

    private static final Logger logger = LogManager.getLogger(Modify.class);

    @InjectPage
    private DetailApplication detailsApplicationPage;

    @Inject
    @Named("structureService")
    private StructureService structureService;

    @Inject
    @Named("structureApplicationService")
    private StructureApplicationService structureApplicationService;

    @Inject
    @Named("agentProfileService")
    private AgentProfileService agentProfileService;

    @Inject
    @Named("employeeProfileService")
    private EmployeeProfileService employeeProfileService;

    @Property
    private Application application;
    @Property
    private StructureTypeValue structureTypeValueSelected;

    // Services n pages
    @Inject
    private Messages messages;

    @Property
    private String errorMessage;

    @Component(id = "modification_form")
    private Form form;

    // Page Activation n Passivation
    @OnEvent(EventConstants.ACTIVATE)
    public void activation() {
        super.initUser();
    }

    /**
     * event activate
     */

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate() {
        return this.detailsApplicationPage;
    }

    @OnEvent(EventConstants.ACTIVATE)

    public Object onActivate(Long id) {
        this.application = this.applicationService.findOne(id);
        if (null == this.application) {
            logger.error("[OnActivate] no application was found with with id : {}", id);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @OnEvent(EventConstants.PASSIVATE)
    public Long onPassivate() {
        return (this.application != null) ? this.application.getId() : null;
    }

    @OnEvent(EventConstants.SUCCESS)
    public Object onSuccess() {
        logger.debug("Enterring into method OnSuccess");
        try {
            this.applicationService.update(this.application);
        } catch (Exception e) {
            this.errorMessage = this.getMessages().get("recording-error-message");
            return null;
        }
        this.detailsApplicationPage.setSuccessMessage(this.messages.get("recording-success-message"));
        this.detailsApplicationPage.setApplication(this.application);
        return this.detailsApplicationPage;
    }

    @OnEvent(EventConstants.CANCELED)
    public Object onFormCancel() {
        this.detailsApplicationPage.setApplication(this.application);
        return View.class;
    }

    @OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
    public void onValidate() {
        // Check if an application with the same name already exists
        Application foundApplication = this.applicationService.findByName(application.getName());
        if (foundApplication != null && !foundApplication.getId().equals(this.application.getId())) {
            this.form.recordError(this.messages.get("application-exists-error"));
        }
        // Check if nbMaxLicence is inferior at the number total of subscription.
        if (isNbMaxLicensesInferiorToSubscription()) {
            this.form.recordError(this.messages.get("application-nbMaxLicenses-error"));
        }

    }

    private boolean isNbMaxLicensesInferiorToSubscription() {
        if (this.application.getnbMaxLicenses() != null && this.application.getnbMaxLicenses() != 0L) {
            long licensesSubscribed = 0;
            List<Structure> structureList = this.structureService.findAllStructureByApplication(this.application);
            List<StructureApplication> structureApplicationList = this.structureApplicationService.findAllByApplication(this.application);
            for (StructureApplication structureApplication : structureApplicationList) {
                Structure structure = structureList.stream().filter(
                        s -> s.getId().equals(structureApplication.getStructureApplicationId().getStructuresId())).findAny().orElse(null);
                if (structure != null) {

                    if (structure.getStructureType().getValue().equals(StructureTypeValue.ORGANISM)) {
                        licensesSubscribed += this.agentProfileService.findAllAgentProfilesByOrganismAndApplication((Organism) structure, this.application).size();
                    } else {
                        licensesSubscribed += this.employeeProfileService.findAllEmployeeProfilesByCompanyAndApplication((Company) structure, this.application).size();
                    }

                }
            }
            return licensesSubscribed > this.application.getnbMaxLicenses();
        }
        return false;
    }

}


