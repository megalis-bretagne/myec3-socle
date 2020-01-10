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
package org.myec3.socle.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * This class provides an application-wide access to the Spring
 * ApplicationContext! The ApplicationContext is injected in a static method of
 * the class "SpringApplicationContext".
 * 
 * Use SpringApplicationContext.getContext() to get access to all Spring Beans.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */

public class SpringApplicationContext implements ApplicationContextAware {

	/**
	 * Spring context, automatically injected by Spring
	 */
	private static ApplicationContext context = null;

	/**
	 * {@inheritDoc}
	 * 
	 * ApplicationContextAware method that will be called automatically by
	 * spring container.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	/**
	 * Static method to get the context
	 * 
	 * @return the spring context
	 */
	public static ApplicationContext getContext() {
		return context;
	}

}