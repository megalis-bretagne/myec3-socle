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

import org.myec3.socle.core.domain.dao.AcronymsListDao;
import org.myec3.socle.core.domain.model.AcronymsList;
import org.myec3.socle.core.service.AcronymsListService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Servicve implementation providing specific methods to
 * {@link AcronymsList} objects. These methods complete or override parent
 * methods from {@link ProfileServiceImpl} or {@link ResourceServiceImpl}
 * services
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Service("acronymsListService")
public class AcronymsListServiceImpl extends AbstractGenericServiceImpl<AcronymsList, AcronymsListDao>
		implements AcronymsListService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AcronymsList findOneAvailableAcronym() {
		return this.dao.findOneAvailableAcronym();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AcronymsList findByValue(String value) {
		Assert.notNull(value, "value of acronym is mandatory. null value is forbidden");
		return this.dao.findByValue(value);
	}

}
