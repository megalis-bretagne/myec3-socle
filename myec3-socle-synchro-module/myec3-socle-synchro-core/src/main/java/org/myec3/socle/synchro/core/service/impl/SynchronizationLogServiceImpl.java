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
package org.myec3.socle.synchro.core.service.impl;

import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationLogDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.springframework.stereotype.Service;

/**
 * Concrete Service implementation providing methods specific to
 * {@link SynchronizationLog} objects used to save in database the response sent
 * by distants applications during the synchronization process.
 * 
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationLogService")
public class SynchronizationLogServiceImpl extends AbstractGenericServiceImpl<SynchronizationLog, SynchronizationLogDao>
		implements SynchronizationLogService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationLog> findAllSynchronizationLogByResourceId(Long resourceId) {
		return this.dao.findAllSynchronizationLogByResourceId(resourceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationLog> findAllSynchronizationLogBySynchronizationLogId(Long synchronizationLogId) {
		return this.dao.findAllSynchronizationLogBySynchronizationLogId(synchronizationLogId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationLog> findAllSynchronizationLogByInitialSynchronizationId(Long initialSynchronizationId) {
		return this.dao.findAllSynchronizationLogByInitialSynchronizationId(initialSynchronizationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationLog> findAllSynchronizationLogByCriteria(Date startDate, Date endDate,
			Application application, String resourceType, String httpStatus, String synchronizationType,
			String methodType, String statut, Boolean isFinal) {
		return this.dao.findAllSynchronizationLogByCriteria(startDate, endDate, application, resourceType, httpStatus,
				synchronizationType, methodType, statut, isFinal);
	}
}
