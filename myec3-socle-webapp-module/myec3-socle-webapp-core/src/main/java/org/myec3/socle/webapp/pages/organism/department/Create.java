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
package org.myec3.socle.webapp.pages.organism.department;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.components.OrganismDepartmentForm;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Page used to create an organism department{@link OrganismDepartment}.<br />
 * 
 * In this step your must fill organism department attributes.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/department/Create.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Create extends AbstractPage {

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@InjectPage
	@Property
	private View viewPage;

	@SuppressWarnings("unused")
	@Component(id = "organism_department_form")
	private OrganismDepartmentForm form;

	@Property
	private OrganismDepartment department;

	@Property
	private Organism organism;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id, Long idDepartment) {
		this.department = new OrganismDepartment();
		this.department.setCreatedUserId(this.getUserIdLogged());
		this.department.setExternalId(new Long(0));
		this.department.setParentDepartment(this.organismDepartmentService.findOne(idDepartment));
		this.department.setOrganism(this.organismService.findOne(id));
		this.organism = this.organismService.findOne(id);
		if (this.department.getOrganism() != null) {
			this.department.setSiren(this.department.getOrganism().getSiren());
			this.department.setDescription(this.department.getOrganism().getDescription());
			this.department.setPhone(this.department.getOrganism().getPhone());
			this.department.setFax(this.department.getOrganism().getFax());
			this.department.setWebsite(this.department.getOrganism().getWebsite());
			this.department.setAddress(new Address());
			this.fillOrganismDepartmentAddressFields();
		}

		if (null == this.department.getAddress()) {
			this.department.setAddress(new Address());
		}

		if (null == this.department.getAbbreviation()) {
			this.department.setAbbreviation("");
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	public Object onPassivate() {
		// arrayList is mandatory if you want to pass multiple parameters into
		// passivate
		List<Long> result = new ArrayList<Long>();
		result.add(this.department.getOrganism().getId());
		result.add(this.department.getParentDepartment().getId());
		result.add(this.organism.getId());
		return result;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			this.department.setName(department.getLabel() + Calendar.getInstance().getTimeInMillis());
			this.organismDepartmentService.create(this.department);
			this.synchronizationService.notifyCreation(this.department);
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.viewPage.setOrganism(this.department.getOrganism());
		return this.viewPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

	public void fillOrganismDepartmentAddressFields() {
		if ((this.department.getParentDepartment() != null) && (this.department.getOrganism() != null)) {

			Address parentDepartmentAddress = this.department.getParentDepartment().getAddress();
			Address organismAddress = this.organism.getAddress();

			// FILL ADDRESS FIELDS
			if (parentDepartmentAddress != null) {
				// City field
				if (!parentDepartmentAddress.getCity().isEmpty()) {
					this.department.getAddress().setCity(parentDepartmentAddress.getCity());
				} else {
					this.department.getAddress().setCity(organismAddress.getCity());
				}

				// Country field
				if (parentDepartmentAddress.getCountry() != null) {
					this.department.getAddress().setCountry(parentDepartmentAddress.getCountry());
				} else {
					this.department.getAddress().setCountry(organismAddress.getCountry());
				}

				// PostalAddress field
				if (!parentDepartmentAddress.getPostalAddress().isEmpty()
						&& !(parentDepartmentAddress.getPostalAddress().equals(" "))) {
					this.department.getAddress().setPostalAddress(parentDepartmentAddress.getPostalAddress());
				} else {
					this.department.getAddress().setPostalAddress(organismAddress.getPostalAddress());
				}

				// Postal code field
				if (!parentDepartmentAddress.getPostalCode().isEmpty()) {
					this.department.getAddress().setPostalCode(parentDepartmentAddress.getPostalCode());
				} else {
					this.department.getAddress().setPostalCode(organismAddress.getPostalCode());
				}
			} else {
				if (organismAddress != null) {
					this.department.setAddress(organismAddress);
				}
			}

			// FILL EMAIL FIELD
			if (this.department.getParentDepartment().getEmail() != null
					&& !(this.department.getParentDepartment().getEmail().isEmpty())) {
				this.department.setEmail(this.department.getParentDepartment().getEmail().toLowerCase());
			} else {
				this.department.setEmail(this.organism.getEmail().toLowerCase());
			}
		}
	}
}
