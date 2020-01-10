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

import org.myec3.socle.core.domain.dao.StructureTypeApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.myec3.socle.core.service.StructureTypeApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link StructureTypeApplication} objects. These methods complete or override
 * parent methods from {@link ResourceServiceImpl}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Service("structureTypeApplicationService")
public class StructureTypeApplicationServiceImpl
		extends AbstractGenericServiceImpl<StructureTypeApplication, StructureTypeApplicationDao>
		implements StructureTypeApplicationService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<StructureTypeApplication> findAllStructureTypeApplicationByApplication(Application application) {
		Assert.notNull(application, "application is mandatory. null value is forbidden");
		return this.dao.findAllStructureTypeApplicationByApplication(application);
	}

}
