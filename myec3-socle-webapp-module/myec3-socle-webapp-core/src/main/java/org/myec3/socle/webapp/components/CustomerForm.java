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
package org.myec3.socle.webapp.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Component class used to manage (add or modify) {@link Customer}<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/CustomerForm.tml
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 * 
 */
public class CustomerForm extends AbstractPage {

	// Services n pages
	@SuppressWarnings("rawtypes")
	@Environmental
	private ComponentEventResultProcessor componentEventResultProcessor;

	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true)
	private Customer customer;

	@Parameter(required = true, defaultPrefix = "prop")
	@Property
	private Object cancelRedirect;

	@Component(id = "modification_form")
	private Form form;

	private UploadedFile logo;

	private Boolean manageCompanies;
	
	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		if (null != logo) {
			if (!isAuthorizedMimeType()) {
				this.form.recordError(this.getMessages().get(
						"invalid-file-type-error"));
			}
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		this.getCustomer().setAuthorizedToManageCompanies(manageCompanies);
		final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(
				componentEventResultProcessor);
		this.componentResources.triggerEvent("participativeprocessformok",
				null, callback);
	}

	@OnEvent(EventConstants.CANCELED)
	public Object cancelRedirect() {
		return this.cancelRedirect;
	}

	/**
	 * @return true if is a creation of new agent profile
	 */
	public Boolean getisNewCustomer() {
		if (null == this.customer.getId())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	public Boolean isAuthorizedMimeType() {
		String contentType = this.logo.getContentType();
		if ("image/bmp".equals(contentType) || "image/gif".equals(contentType)
				|| "image/jpeg".equals(contentType)
				|| "image/png".equals(contentType)
				|| "image/x-png".equals(contentType)
				|| "image/pjpeg".equals(contentType)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Boolean getManageCompanies() {
		return this.customer.isAuthorizedToManageCompanies();
	}

	public void setManageCompanies(Boolean manageCompanies) {
		this.manageCompanies = manageCompanies;
	}
	
	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}
}
