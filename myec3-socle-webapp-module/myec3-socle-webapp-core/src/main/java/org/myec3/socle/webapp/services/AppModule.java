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
package org.myec3.socle.webapp.services;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.OrderedConfiguration;
import org.apache.tapestry5.http.services.HttpServletRequestFilter;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.Core;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.StackExtension;

/**
 *
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 *
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 *
 */
public class AppModule {

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		// check if env can proccess https
		ResourceBundle bundle = ResourceBundle.getBundle("webapp");
		String https = bundle.getString("https");
		String hmacPass = bundle.getString("tapestry.hmacPass");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "fr");
		configuration.add(SymbolConstants.EXCEPTION_REPORT_PAGE,
				"ExceptionReport");
		configuration.add("tapestry.page-pool.hard-limit", "50");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, hmacPass);
		configuration.add(SymbolConstants.ENABLE_PAGELOADING_MASK, "false");

		// in local and dev, no secure, no production mode
		if (https.equalsIgnoreCase("false")) {
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
			configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
			configuration.add(MetaDataConstants.SECURE_PAGE, "false");
		} else {
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "true");
			configuration.add(SymbolConstants.PRODUCTION_MODE, "true");
			configuration.add(MetaDataConstants.SECURE_PAGE, "true");
		}

	}

	@Contribute(JavaScriptStack.class)
	@Core
	public static void addAppModules(OrderedConfiguration<StackExtension> configuration) {
		configuration.override("bootstrap.css", StackExtension.stylesheet("context:/static/css/main.css"));
		configuration.add("theme.css", StackExtension.stylesheet("context:/static/css/theme.css"));
	}

	/**
	 * Keep Tapestry from processing requests to the web service path.
	 *
	 * @param configuration {@link Configuration}
	 */
	public static void contributeIgnoredPathsFilter(
			final Configuration<String> configuration) {
		configuration.add("/oauth/.*");
		configuration.add("/sdmInit/.*");
		configuration.add("/pydioInit/.*");
		configuration.add("/health");
		configuration.add("/externe/.*");
	}

	public HttpServletRequestFilter buildUtf8Filter() {
		return new HttpServletRequestFilter() {
			@Override
			public boolean service(HttpServletRequest request, HttpServletResponse response,
					HttpServletRequestHandler handler) throws IOException {
				request.setCharacterEncoding("UTF-8");
				return handler.service(request, response);
			}
		};
	}

	public void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration,
			@InjectService("Utf8Filter") HttpServletRequestFilter utf8Filter) {
		configuration.add("Utf8Filter", utf8Filter, "before:MultipartFilter");
	}

}
