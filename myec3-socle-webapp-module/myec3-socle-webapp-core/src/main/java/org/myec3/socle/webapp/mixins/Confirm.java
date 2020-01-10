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
package org.myec3.socle.webapp.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Mixins class used to configure confirm dialog to display at the end user
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
@Import(library = {"confirm.js"})
public class Confirm {

	@Parameter(value = "Are you sure?", defaultPrefix = BindingConstants.LITERAL)
	private String message;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@InjectContainer
	private ClientElement element;

	@AfterRender
	public void afterRender() {
		javaScriptSupport.addScript("new Confirm('%s', '%s');",
				element.getClientId(), message);
	}

}