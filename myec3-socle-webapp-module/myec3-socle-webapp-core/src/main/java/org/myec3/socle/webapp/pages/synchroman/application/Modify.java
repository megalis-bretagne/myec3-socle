/**
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.synchroman.application;

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
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.StructureApplicationInfoService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.List;

/**
 * Page used to modify the application{@link Application}<br />
 * <p>
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/application/Modify.tml<br
 * />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 * page<br />
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
    @Named("structureApplicationInfoService")
    private StructureApplicationInfoService structureApplicationInfoService;

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

    private List<Structure> structureList;

    private List<StructureApplicationInfo> structureApplicationInfoList;


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

        this.structureList = this.structureService.findAllStructureByApplication(this.application);
        this.structureApplicationInfoList = this.structureApplicationInfoService.findAllByApplication(this.application);

        return Boolean.TRUE;
    }

    @OnEvent(EventConstants.PASSIVATE)
    public Long onPassivate() {
        return (this.application != null) ? this.application.getId() : null;
    }

    @OnEvent(EventConstants.SUCCESS)
    public Object onSuccess() {
        logger.debug("Enterring into method OnSuccess");
        // if add a nbMaxLicenses on the application then we set nbMaxLicenses on its structures
        if (this.application.getNbMaxLicenses() != null && this.application.getNbMaxLicenses() > 0L) {
            this.updateNbMaxLicensesStructure();
        } else {
            this.clearNbMaxLicensesStructure();
        }

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

        this.checkNbMaxLicensesInferiorToSumStructure();

    }

    /**
     * check if the new value is less than the total of NbLicensesMax on each structure
     */
    private void checkNbMaxLicensesInferiorToSumStructure() {
        if (this.application.getNbMaxLicenses() != null && this.application.getNbMaxLicenses() > 0L) {
            long sumNbMaxlicenses = 0;

            for (StructureApplicationInfo structureApplicationInfo : this.structureApplicationInfoList) {
                if (structureApplicationInfo.getNbMaxLicenses() != null) {
                    sumNbMaxlicenses += structureApplicationInfo.getNbMaxLicenses();
                }
            }

            if (sumNbMaxlicenses > this.application.getNbMaxLicenses()) {
                this.form.recordError(String.format(this.messages.get("application-nbMaxLicenses-error"), sumNbMaxlicenses));
            }
        }
    }

    /**
     * The NbMaxLicenses on each structures is determined in relation to its already subscribed profiles
     */
    private void updateNbMaxLicensesStructure() {
        for (Structure structure : this.structureList) {
            long nbLicense;
            if (structure.getStructureType().getValue().equals(StructureTypeValue.ORGANISM)) {
                nbLicense = this.agentProfileService.findAllAgentProfilesByOrganismAndApplication((Organism) structure, this.application).size();
            } else {
                nbLicense = this.employeeProfileService.findAllEmployeeProfilesByCompanyAndApplication((Company) structure, this.application).size();
            }
            this.structureApplicationInfoService.create(new StructureApplicationInfo(structure, this.application, nbLicense));
        }
    }

    /**
     * Clear all NbMaxLicenses on its Structures
     */
    private void clearNbMaxLicensesStructure() {
        for (StructureApplicationInfo structureApplicationInfo : this.structureApplicationInfoList) {
            this.structureList.stream().filter(
                    s -> s.getId().equals(structureApplicationInfo.getStructureApplicationInfoId().getStructuresId())).findAny().ifPresent(structure -> structureApplicationInfo.setNbMaxLicenses(null));
            this.structureApplicationInfoService.delete(structureApplicationInfo);
        }

    }

}


