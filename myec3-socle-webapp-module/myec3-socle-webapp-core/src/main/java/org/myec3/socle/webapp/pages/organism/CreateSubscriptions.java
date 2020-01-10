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
package org.myec3.socle.webapp.pages.organism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used during organism{@link Organism} creation process.<br />
 * 
 * It's the second step to create an organism. In this step your must fill<br />
 * organism's{@link Organism} applications{@link Application} subscribed.<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle
 *      /webapp/pages/organism/CreateSubscriptions.tml
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class CreateSubscriptions extends AbstractPage {

	private static final Log logger = LogFactory.getLog(CreateSubscriptions.class);

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Property
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@InjectPage
	private CreateFirstAgent createFirstAgent;

	@Persist
	private Set<Application> selectedApplications;

	@Inject
	private PropertyAccess propertyAccess;

	private GenericListEncoder<Application> applicationEncoder;

	private List<Application> availableApplications;

	private Application applicationLoop;

	private Organism organism;

	@Component(id = "modification_form")
	private Form form;

	public Boolean beginRender() {
		if (getSelectedApplications() == null) {
			setSelectedApplications(new HashSet<Application>());
		}

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	// Page Activation n Passivation
	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);
		if (null == this.organism) {
			return false;
		}

		if (null == this.organism.getAddress()) {
			this.organism.setAddress(new Address());
		}

		this.availableApplications = applicationService.findAllApplicationSubscribableByStructureTypeAndCustomer(
				this.organism.getStructureType(), this.organism.getCustomer());

		this.applicationEncoder = new GenericListEncoder<Application>(this.availableApplications);

		// Check if loggedUser can access at this organism
		return this.hasRights(this.organism);

	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			this.organism.setEnabled(Boolean.TRUE);
			List<Application> apps = new ArrayList<Application>();
			for (Application application : this.selectedApplications) {
				apps.add(application);
			}

			// add default application dpending on structureType and customer
			List<Application> defaultApplications = this.applicationService
					.findAllDefaultApplicationsByStructureTypeAndCustomer(this.organism.getStructureType(),
							this.organism.getCustomer());

			for (Application application : defaultApplications) {
				apps.add(application);
			}
			this.organism.setApplications(apps);

			// Update the organism
			this.organismService.update(this.organism);

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			logger.error(e);
			return null;
		}

		OrganismDepartment organismDepartment = this.organismDepartmentService
				.findRootOrganismDepartment(this.organism);

		// Synchronize the new Organism
		this.synchronizationService.notifyCreation(this.organism);

		// Synchronize the organismDepartment
		this.synchronizationService.notifyCreation(organismDepartment);

		// Call next page
		this.createFirstAgent.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.createFirstAgent.setOrganism(this.organism);
		return this.createFirstAgent;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

	public void setAvailableApplications(List<Application> availableApplications) {
		this.availableApplications = availableApplications;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return applicationEncoder;
	}

	public boolean isSelected() {
		return getSelectedApplications().contains(getApplicationLoop());
	}

	/**
	 * Add application to the selected set if selected.
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			getSelectedApplications().add(getApplicationLoop());
		} else {
			getSelectedApplications().remove(getApplicationLoop());
		}
	}

	public Set<Application> getSelectedApplications() {
		return selectedApplications;
	}

	public void setSelectedApplications(Set<Application> selectedApplications) {
		this.selectedApplications = selectedApplications;
	}

	public List<Application> getAvailableApplications() {
		return availableApplications;
	}

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
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
}