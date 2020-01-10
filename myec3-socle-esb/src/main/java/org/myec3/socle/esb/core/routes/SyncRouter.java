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
package org.myec3.socle.esb.core.routes;

import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Default route used to synchronize resources
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atos.net>
 * 
 */
@Component
public class SyncRouter extends SpringRouteBuilder {

	/**
	 * Default logger used by this class
	 */
	private static final Logger logger = LogManager.getLogger(SyncRouter.class);

	@Override
	public void configure() throws Exception {
		try {
			from("jms:queue:" + MyEc3EsbConstants.getSyncJmsIn()).to(
					"jms:queue:" + MyEc3EsbConstants.getSyncJmsOut());

			logger.info("SyncRouter has been successfully created.");
		} catch (Exception e) {
			logger.error("Exception in configure : " + e);
		}
	}
}
