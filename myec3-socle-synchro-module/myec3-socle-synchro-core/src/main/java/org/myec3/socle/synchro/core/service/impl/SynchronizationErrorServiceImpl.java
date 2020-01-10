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
import org.myec3.socle.synchro.core.domain.dao.SynchronizationErrorDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;
import org.myec3.socle.synchro.core.service.SynchronizationErrorService;
import org.springframework.stereotype.Service;

/**
 * package org.myec3.socle.synchro.core.service.impl;
 * 
 * import java.util.List;
 * 
 * import javax.inject.Inject; import javax.inject.Named;
 * 
 * import org.myec3.socle.synchro.core.domain.dao.SynchronizationErrorDao;
 * import org.myec3.socle.synchro.core.domain.model.SynchronizationError; import
 * org.myec3.socle.synchro.core.service.SynchronizationErrorService; import
 * org.resthub.core.service.GenericServiceImpl;
 * 
 * /** Concrete Service implementation providing methods specific to
 * {@link SynchronizationError} objects used for handling errors during
 * synchronization tasks.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationErrorService")
public class SynchronizationErrorServiceImpl
		extends AbstractGenericServiceImpl<SynchronizationError, SynchronizationErrorDao>
		implements SynchronizationErrorService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationError> findAllByResourceId(Long resourceId) {
		return dao.findAllByResourceId(resourceId);
	}

}
