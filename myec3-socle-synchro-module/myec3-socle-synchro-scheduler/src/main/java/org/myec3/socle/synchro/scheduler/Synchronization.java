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
package org.myec3.socle.synchro.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class to use if you want to luch synchronization module using a JAR packaging
 * instead of a webapp
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 */
public class Synchronization {

	private static Logger logger = LoggerFactory
			.getLogger(Synchronization.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		@SuppressWarnings("unused")
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "classpath:coreMyec3Context.xml",
						"classpath:synchroMyec3Context.xml",
						"jmsMyec3Context.xml", "classpath:wsClientMyec3Context.xml" });

		logger.info("Begin Synchronization...");
	}

}
