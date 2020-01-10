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

import java.util.List;

import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationInitialDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SynchronizationInitialService;
import org.springframework.stereotype.Service;

/**
 * Concrete Service implementation providing methods specific to
 * {@link SynchronizationInitial} objects.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationInitialService")
public class SynchronizationInitialServiceImpl
		extends AbstractGenericServiceImpl<SynchronizationInitial, SynchronizationInitialDao>
		implements SynchronizationInitialService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SynchronizationInitial findBySynchronizationLog(SynchronizationLog synchronizationLog) {
		return this.dao.findBySynchronizationLog(synchronizationLog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationLog> findAllSynchronizationLogByInitialSynchronizationId(Long initialSynchronizationId) {
		return this.dao.findAllSynchronizationLogByInitialSynchronizationId(initialSynchronizationId);
	}
}
