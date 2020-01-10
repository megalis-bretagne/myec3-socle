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

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;

/**
 * Configure into securityMyec3Context.xml, redirect to openssoLogout after
 * springsecurity logout<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/Logout.tml
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * 
 */
public class Logout extends AbstractPage {
		
	/*@Inject
    private ReloadableResourceBundleMessageSource messageSource;
	
	@OnEvent(EventConstants.ACTIVATE)
	public URL onActivate() { 
		
		SecurityContextHolder.clearContext();
		
		URL redirect = null;
		try {
			redirect = new URL(this.messageSource.getMessage("logoutURL", null, Locale.FRENCH));
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (NoSuchMessageException e) {			
			e.printStackTrace();
		}
		
		return redirect; 
	}*/
	
	@OnEvent(EventConstants.ACTIVATE)
	public void onActivate() {		
	}
}
