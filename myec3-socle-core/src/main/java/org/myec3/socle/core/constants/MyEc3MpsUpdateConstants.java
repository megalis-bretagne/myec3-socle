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
package org.myec3.socle.core.constants;

import java.util.ResourceBundle;

/**
 * Constants class used to manage MpsUpdate
 * 
 * @see org.myec3.socle.core.service.impl.CompanyServiceImpl
 * @see org.myec3.socle.core.service.impl.EstablishmentServiceImpl
 * 
 * @author Matthieu Gaspard <matthieu.gaspard@atosorigin.com>
 */
public final class MyEc3MpsUpdateConstants {

    private static final String MPSUPDATE_BUNDLE_NAME = "mpsUpdate";
    private static final ResourceBundle MPSUPDATE_BUNDLE = ResourceBundle
	    .getBundle(MPSUPDATE_BUNDLE_NAME);

    /**
     * @return the number of month do define if resource needs to be updated
     */
    public static String getMonth() {
	return MPSUPDATE_BUNDLE.getString("mpsUpdate.month");
    }

    /**
     * @return the number of days do define if resource needs to be updated
     */
    public static String getDays() {
        return MPSUPDATE_BUNDLE.getString("mpsUpdate.days");
    }

    /**
     * @return the Hostname that will handle the update
     */
    public static String getHostname1() {
	return MPSUPDATE_BUNDLE.getString("mpsUpdate.machineName1");
    }

    /**
     * @return the Hostname that will handle the update
     */
    public static String getHostname2() {
	return MPSUPDATE_BUNDLE.getString("mpsUpdate.machineName2");
    }

    /**
     * @return the interval from current day from which we should check the
     *         connection
     */
    public static String getConnectionInfoDay() {
	return MPSUPDATE_BUNDLE.getString("mpsUpdate.connectionInfoDay");
    }

}
