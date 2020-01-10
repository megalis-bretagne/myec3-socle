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

import org.myec3.socle.core.domain.dao.InseeGeoCodeDao;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link InseeGeoCode} objects. These methods complete or override parent
 * methods from {@link ResourceServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
@Service("inseeGeoCodeService")
public class InseeGeoCodeServiceImpl extends ResourceServiceImpl<InseeGeoCode, InseeGeoCodeDao>
		implements InseeGeoCodeService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InseeGeoCode> findByPostalCode(String postalCode) {
		Assert.notNull(postalCode, "postalCode is mandatory. null value is forbidden.");

		return this.dao.findByPostalCode(postalCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InseeGeoCode findByInseeCode(String inseeCode) {
		Assert.notNull(inseeCode, "inseeCode is mandatory. null value is forbidden.");

		return this.dao.findByInseeCode(inseeCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InseeGeoCode findByPostalCodeAndCity(String postalCode, String city) {
		Assert.notNull(postalCode, "postalCode is mandatory. null value is forbidden.");
		Assert.notNull(city, "city is mandatory. null value is forbidden.");

		return this.dao.findByPostalCodeAndCity(postalCode, city);
	}

}
