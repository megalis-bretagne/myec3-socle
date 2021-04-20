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
package org.myec3.socle.synchro.core.domain.dao;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;

import java.util.Date;
import java.util.List;

/**
 * This interface define methods to perform specific queries on
 * {@link SynchronizationLog} objects). It only defines new specific methods and
 * inherits methods from {@link GenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationLogDao extends
		GenericSynchronizationDao<SynchronizationLog> {

	/**
	 * Find all {@link SynchronizationLog} associated directly to an resource.
	 * 
	 * @param resourceId
	 *            : the ID of a resource
	 * 
	 * @return List of {@link SynchronizationLog} associated to this resource.
	 *         return an empty list if no result or null in case of error.
	 * 
	 */
	List<SynchronizationLog> findAllSynchronizationLogByResourceId(
			Long resourceId);

	/**
	 * Find all {@link SynchronizationLog} associated directly to an
	 * synchronizationLog.
	 * 
	 * @param synchronizationLogId
	 *            : the ID of a synchronizationLog
	 * 
	 * @return List of {@link SynchronizationLog} associated to this
	 *         synchronization log. return an empty list if no result or null in
	 *         case of error.
	 * 
	 */
	List<SynchronizationLog> findAllSynchronizationLogBySynchronizationLogId(
			Long synchronizationLogId);

	/**
	 * Find all {@link SynchronizationLog} associated directly to an
	 * {@link SynchronizationInitial}.
	 * 
	 * @param initialSynchronizationId
	 *            : the ID of a SynchronizationInitial to filter on
	 * 
	 * @return List of {@link SynchronizationLog} associated to this
	 *         synchronization initial id. return an empty list if no result or
	 *         null in case of error.
	 * 
	 */
	List<SynchronizationLog> findAllSynchronizationLogByInitialSynchronizationId(
			Long initialSynchronizationId);

	/**
	 * Retrive all {@link SynchronizationLog} matching given criteria. All null
	 * valued parameter is ignored.
	 * 
	 * @param startDate
	 *            : the begin date of synchronization
	 * @param endDate
	 *            : the end date of synchronization
	 * @param application
	 *            : the application (@see Application) synchronized
	 * @param resourceType
	 *            : the type of the resource synchronized (@see ResourceType)
	 * @param httpStatus
	 *            : the http status returned during the synchronization
	 * @param synchronizationType
	 *            : The type of the synchronization used (@see
	 *            SynchronizationType)
	 * @param methodType
	 *            : the method used to synchronize the resource (@see
	 *            MethodType)
	 * @param statut
	 *            : the statut of the synchronization
	 * @param isFinal
	 *            : boolean used to know if the synchronization is finished or
	 *            not
	 * 
	 * @return a list of {@link SynchronizationLog} corresponding at given
	 *         criteria. Return an empty list if no results or null in case of
	 *         error.
	 */
	List<SynchronizationLog> findAllSynchronizationLogByCriteria(
			Date startDate, Date endDate, Application application,
			String resourceType, String httpStatus, String synchronizationType,
			String methodType, String statut, Boolean isFinal);

	/**
	 *
	 * Retrive all synchroLog associated to an organism
	 * List contains Log for Organism, OrganismeDepartement and AgentProfile Associated
	 * @param id	the organism Id
	 * @return	list of {@link SynchronizationLog}
	 */
	List<SynchronizationLogDTO> findAllSynchronizationLogByOrganism(Long id);

	/**
	 *
	 * Retrive all synchroLog associated to a Company
	 * List contains Log for Company, Establishment and EmployeeProfile Associated
	 * @param id	the Company Id
	 * @return	list of {@link SynchronizationLog}
	 */
    List<SynchronizationLogDTO> findAllSynchronizationLogByCompany(Long id);
}
