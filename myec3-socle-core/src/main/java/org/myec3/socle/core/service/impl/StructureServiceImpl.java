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
package org.myec3.socle.core.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.dao.StructureDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.service.StructureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link Structure} objects. These methods complete or override parent methods
 * from {@link ResourceServiceImpl}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("structureService")
public class StructureServiceImpl extends ResourceServiceImpl<Structure, StructureDao> implements StructureService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Structure> findAllStructureByApplication(Application application) {
		Assert.notNull(application, "application is mandatory. null value is forbidden");

		return this.dao.findAllStructureByApplication(application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Boolean isSirenValid(String siren) {
		if (null == siren) {
			return Boolean.FALSE;
		}

		try {
			int sum = 0;
			for (int i = 0; i < siren.length(); i++) {
				int value = Integer.valueOf(String.valueOf(siren.charAt(i)));
				// Warning : conditions are inversed tp check a SIRET (14
				// numeric characters)
				if (i % 2 == 0) {
					sum += value;
				} else {
					sum += 2 * value > 9 ? 2 * value - 9 : 2 * value;
				}
			}

			if (sum % 10 != 0) {
				return Boolean.FALSE;
			}
		} catch (RuntimeException e) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Structure> findAllChildStructuresByStructure(Structure structure) {
		Assert.notNull(structure, "structure is mandatory. null value is forbidden");
		return this.dao.findAllChildStructuresByStructure(structure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Structure> findAllParentStructuresByStructure(Structure structure) {
		Assert.notNull(structure, "structure is mandatory. null value is forbidden");
		return this.dao.findAllParentStructuresByStructure(structure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Structure findBySiren(String siren) {
		Assert.notNull(siren, "siren is mandatory. null value is forbidden");
		return this.dao.findBySiren(siren);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Structure> findAllByLabelOrSiren(String value) {
		return this.dao.findAllByLabelOrSiren(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean relationAlreadyExists(Structure structureToUpdate, Structure structureAdded) {
		Assert.notNull(structureToUpdate, "structureToUpdate cannot be null");
		Assert.notNull(structureAdded, "structureAdded cannot be null");

		// We retrieve parent and child structures of the structure to update
		List<Structure> parentStructuresList = this.findAllParentStructuresByStructure(structureToUpdate);
		List<Structure> childStructuresList = this.findAllChildStructuresByStructure(structureToUpdate);

		if (!(parentStructuresList.contains(structureAdded)) && !(childStructuresList.contains(structureAdded))) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}
}
