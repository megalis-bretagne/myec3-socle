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

/**
 * Class of constants used by quartz during synchronization and handling errors
 * tasks
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3JobConstants {

	public static final String CREATION_JOB = "creationJob";
	public static final String DELETION_JOB = "deletionJob";
	public static final String UPDATE_JOB = "updateJob";
	public static final String COLLECTION_CREATE_JOB = "collectionCreateJob";
	public static final String COLLECTION_UPDATE_JOB = "collectionUpdateJob";
	
	public static final String ADMIN_JOB = "adminJob";
	public static final String AGENT_JOB = "agentJob";
	public static final String EMPLOYEE_JOB = "employeeJob";
	public static final String COMPANY_JOB = "companyJob";
	public static final String CUSTOMER_JOB = "customerJob";
	public static final String COMPANY_DEPARTMENT_JOB = "companyDepartmentJob";
	public static final String ORGANISM_JOB = "organismJob";
	public static final String ORGANISM_DEPARTMENT_JOB = "organismDepartmentJob";
	public static final String ESTABLISHMENT_JOB = "establishmentJob";

}
