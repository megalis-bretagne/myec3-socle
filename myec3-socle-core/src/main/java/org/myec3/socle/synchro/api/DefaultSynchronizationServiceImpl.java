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
package org.myec3.socle.synchro.api;

import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationType;

/**
 * Default empty implementation of synchronization services. This class does not
 * perform any task and all its methods are empty. This service is only used to
 * provide a defaut synchronization implementation and reduce coupling.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
public class DefaultSynchronizationServiceImpl implements
		SynchronizationService {

	/**
	 * {@inheritDoc} - Default empty implementation
	 */
	@Override
	public void propagateCreation(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// Default empty method
	}

	/**
	 * {@inheritDoc} - Default empty implementation
	 */
	@Override
	public void propagateDeletion(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// Default empty method
	}

	/**
	 * {@inheritDoc} - Default empty implementation
	 */
	@Override
	public void propagateUpdate(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// Default empty method
	}

	/**
	 * {@inheritDoc} - Default empty implementation
	 */
	@Override
	public void propagateCollectionUpdate(Resource resource,
			String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication) {
		// Default empty method
	}

	/**
	 * {@inheritDoc} - Default empty implementation
	 */
	@Override
	public void propagateCollectionCreate(Resource resource,
			String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// Default empty method
	}
}
