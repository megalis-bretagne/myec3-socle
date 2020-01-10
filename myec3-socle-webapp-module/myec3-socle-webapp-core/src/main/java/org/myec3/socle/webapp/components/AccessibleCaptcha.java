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

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * Component class used to create a captcha during the regeneration password
 * phase.<br>
 * 
 * Corresponding tapestry template file is:
 * src/main/resources/org/myec3/socle/webapp/components/AccessibleCaptcha.tml
 * 
 * 
 * @see org.myec3.socle.webapp.pages.user.RegeneratePassword
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
public class AccessibleCaptcha {

	@Parameter
	@Property
	private Integer questionKey;

	@Parameter
	@Property
	private String question;

	@Parameter
	@Property
	private String answer;

}
