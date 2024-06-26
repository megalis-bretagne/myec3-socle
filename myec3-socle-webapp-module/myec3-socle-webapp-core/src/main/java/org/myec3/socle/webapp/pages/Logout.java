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
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.myec3.socle.webapp.constants.GuWebAppConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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

//	@Inject
//	private Cookies cookies;

	@InjectPage
	private Index index;

	@Inject
	private RequestGlobals requestGlobals;

	@OnEvent(EventConstants.ACTIVATE)
	public Index onActivate() {
		//TODO solution temporaire en attendant de voir comment on s'intègre vraiment avec le portail
		try{
			requestGlobals.getHTTPServletRequest().logout();
			return index;
//			cookies.removeCookieValue("mod_auth_openidc_session");
//			return new URL(GuWebAppConstants.KEYCLOAK_BASE_URL + "/auth/realms/megalis/protocol/openid-connect/logout?redirect_uri=" + GuWebAppConstants.MYEC3_BASE_URL);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
		} catch (ServletException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
