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

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.components.OrganismDepartmentForm;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to modify an organism department{@link OrganismDepartment}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/department/Modify.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Modify extends AbstractPage {

	@SuppressWarnings("unused")
	@Property
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

	@InjectPage
	@Property
	private DetailDepartment detailPage;

	@SuppressWarnings("unused")
	@Component(id = "organism_department_form")
	private OrganismDepartmentForm form;

	@Property
	private OrganismDepartment organismDepartment;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		super.initUser();

		this.organismDepartment = this.organismDepartmentService.findOne(id);
		if (null == this.organismDepartment) {
			return false;
		}

		// Root department can't be modified
		if (null == this.organismDepartment.getParentDepartment()) {
			return Boolean.FALSE;
		}

		// fill address fiels
		this.fillOrganismDepartmentAddressFields();

		if (this.organismDepartment.getAddress() == null) {
			this.organismDepartment.setAddress(new Address());
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organismDepartment.getOrganism());
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organismDepartment != null) ? this.organismDepartment.getId() : null;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			// Update organismDepartment
			this.organismDepartmentService.update(this.organismDepartment);
			this.synchronizationService.notifyUpdate(this.organismDepartment);
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		this.detailPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.detailPage.setOrganismDepartment(this.organismDepartment);
		return this.detailPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.detailPage.setOrganismDepartment(this.organismDepartment);
		return View.class;
	}

	public void fillOrganismDepartmentAddressFields() {
		if ((this.organismDepartment.getParentDepartment() != null)
				&& (this.organismDepartment.getOrganism() != null)) {

			Address parentDepartmentAddress = this.organismDepartment.getParentDepartment().getAddress();
			Address organismAddress = this.organismDepartment.getOrganism().getAddress();
			Address currentDepartmentAddress = this.organismDepartment.getAddress();

			// FILL ADDRESS FIELDS
			if ((parentDepartmentAddress != null) && (currentDepartmentAddress != null)) {
				// City field
				if (currentDepartmentAddress.getCity().isEmpty()) {
					if (!parentDepartmentAddress.getCity().isEmpty()) {
						this.organismDepartment.getAddress().setCity(parentDepartmentAddress.getCity());
					} else {
						this.organismDepartment.getAddress().setCity(organismAddress.getCity());
					}
				}

				// Country field
				if (currentDepartmentAddress.getCountry() == null) {
					if (parentDepartmentAddress.getCountry() != null) {
						this.organismDepartment.getAddress().setCountry(parentDepartmentAddress.getCountry());
					} else {
						this.organismDepartment.getAddress().setCountry(organismAddress.getCountry());
					}
				}

				// PostalAddress field
				if ((currentDepartmentAddress.getPostalAddress().isEmpty())
						|| (currentDepartmentAddress.getPostalAddress().equals(" "))) {
					if (!parentDepartmentAddress.getPostalAddress().isEmpty()
							&& !(parentDepartmentAddress.getPostalAddress().equals(" "))) {
						this.organismDepartment.getAddress()
								.setPostalAddress(parentDepartmentAddress.getPostalAddress());
					} else {
						this.organismDepartment.getAddress().setPostalAddress(organismAddress.getPostalAddress());
					}
				}

				// Postal code field
				if (currentDepartmentAddress.getPostalCode().isEmpty()) {
					if (!parentDepartmentAddress.getPostalCode().isEmpty()) {
						this.organismDepartment.getAddress().setPostalCode(parentDepartmentAddress.getPostalCode());
					} else {
						this.organismDepartment.getAddress().setPostalCode(organismAddress.getPostalCode());
					}
				}
			} else {
				if (organismAddress != null) {
					this.organismDepartment.setAddress(organismAddress);
				}
			}

			// FILL EMAIL FIELD
			if ((this.organismDepartment.getEmail() == null) || (this.organismDepartment.getEmail().isEmpty())) {
				if (this.organismDepartment.getParentDepartment().getEmail() != null
						&& !(this.organismDepartment.getParentDepartment().getEmail().isEmpty())) {
					this.organismDepartment
							.setEmail(this.organismDepartment.getParentDepartment().getEmail().toLowerCase());
				} else {
					this.organismDepartment.setEmail(this.organismDepartment.getOrganism().getEmail().toLowerCase());
				}
			}
		}
	}
}
