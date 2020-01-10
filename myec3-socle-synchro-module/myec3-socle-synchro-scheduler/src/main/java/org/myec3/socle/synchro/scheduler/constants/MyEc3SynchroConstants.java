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
package org.myec3.socle.synchro.scheduler.constants;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class of constants used for synchronization and handling errors
 * synchronization tasks
 * 
 * Your propertie file must be named "synchronization.properties"
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3SynchroConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(MyEc3SynchroConstants.class);;

	public static final String BUNDLE_NAME = "synchronization";
	public static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	// synchronization delay
	public static final Long DELAY = new Long(
			BUNDLE.getString("synchronization.delay"));

	// synchronization timeout
	public static final Long TIMEOUT = new Long(
			BUNDLE.getString("synchronization.timeout"));

	// synchronizationError
	public static final int MAX_ATTEMPTS = Integer.parseInt(BUNDLE
			.getString("synchronization.maxNbAttempts"));

	// ERROR HTTP 404 with error code 404
	public static Long[] HTTP_CODE_404_ERROR_CODE_404_DELAY = new Long[MAX_ATTEMPTS];
	public static final Long[] DEFAULT_HTTP_CODE_404_ERROR_CODE_404_DELAY = {
			new Long(20000), new Long(180000), new Long(500000),
			new Long(900000), new Long(2000000) };
	static {
		String tempSynchronization400ErrorCode004 = BUNDLE
				.getString("synchronization.codeHttp400.errorCode004.delay");

		String[] tempSynchronization400ErrorCode004Tab = tempSynchronization400ErrorCode004
				.replaceAll("\\s", "").split(",");

		try {
			for (int i = 0; i < tempSynchronization400ErrorCode004Tab.length; i++) {
				HTTP_CODE_404_ERROR_CODE_404_DELAY[i] = Long
						.parseLong(tempSynchronization400ErrorCode004Tab[i]);
			}
		} catch (NumberFormatException ex) {
			logger.error(ex.getMessage());
			HTTP_CODE_404_ERROR_CODE_404_DELAY = new Long[5];
			HTTP_CODE_404_ERROR_CODE_404_DELAY = DEFAULT_HTTP_CODE_404_ERROR_CODE_404_DELAY;
		}
	}

	// ERROR HTTP 400
	public static Long[] HTTP_CODE_400_DELAY = new Long[MAX_ATTEMPTS];
	public static final Long[] DEFAULT_HTTP_CODE_400_DELAY = { new Long(2000),
			new Long(120000), new Long(300000), new Long(600000),
			new Long(1800000) };

	static {
		String tempSynchronization400 = BUNDLE
				.getString("synchronization.codeHttp400.delay");

		String[] tempSynchronizationTab400 = tempSynchronization400.replaceAll(
				"\\s", "").split(",");

		try {
			for (int i = 0; i < tempSynchronizationTab400.length; i++) {
				HTTP_CODE_400_DELAY[i] = Long
						.parseLong(tempSynchronizationTab400[i]);
			}
		} catch (NumberFormatException ex) {
			logger.error(ex.getMessage());
			HTTP_CODE_400_DELAY = new Long[5];
			HTTP_CODE_400_DELAY = DEFAULT_HTTP_CODE_400_DELAY;
		}
	}

	// ERRO HTTP 500
	public static Long[] HTTP_CODE_500_DELAY = new Long[MAX_ATTEMPTS];
	public static final Long[] DEFAULT_HTTP_CODE_500_DELAY = { new Long(50000),
			new Long(100000), new Long(500000), new Long(10000000),
			new Long(20000000) };

	static {
		String tempSynchronization500 = BUNDLE
				.getString("synchronization.tab.codeHttp500.delay");

		String[] tempSynchronizationTab = tempSynchronization500.replaceAll(
				"\\s", "").split(",");

		try {
			for (int i = 0; i < tempSynchronizationTab.length; i++) {
				HTTP_CODE_500_DELAY[i] = Long
						.parseLong(tempSynchronizationTab[i]);
			}
		} catch (NumberFormatException ex) {
			logger.error(ex.getMessage());
			HTTP_CODE_500_DELAY = new Long[5];
			HTTP_CODE_500_DELAY = DEFAULT_HTTP_CODE_500_DELAY;
		}
	}
}