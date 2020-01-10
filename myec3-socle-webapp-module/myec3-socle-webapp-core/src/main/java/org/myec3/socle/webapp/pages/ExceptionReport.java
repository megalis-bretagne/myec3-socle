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
package org.myec3.socle.webapp.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

/**
 * Class used to display your own exception page, override default Tapestry page<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/ExceptionReport.tml
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * 
 */
public class ExceptionReport implements ExceptionReporter {

	@SuppressWarnings("unused")
	@Property
	private Throwable exception;

	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}
}
