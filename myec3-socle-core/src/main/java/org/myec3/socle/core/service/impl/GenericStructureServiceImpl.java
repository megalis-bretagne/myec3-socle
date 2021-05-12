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

import org.myec3.socle.core.domain.dao.GenericStructureDao;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.service.GenericStructureService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * This abstract implementation service provides commons methods that could be
 * used on {@link Structure} child classes
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T>
 *            concrete structure child class
 * @param <D>
 *            concrete structure child DAO
 */
public abstract class GenericStructureServiceImpl<T extends Structure, D extends GenericStructureDao<T>>
		extends ResourceServiceImpl<T, D> implements GenericStructureService<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllByCriteria(String label, String siren,
			String postalCode, String city) {
		return this.dao.findAllByCriteria(label, siren, postalCode, city);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findBySiren(String siren) {
		// validate parameters
		Assert.notNull(siren, "siren is mandatory. null value is forbidden");

		return this.dao.findBySiren(siren);
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
				int value = Integer.parseInt(String.valueOf(siren.charAt(i)));
				// Attention, les conditions sont inversés pour verifier un SIRET
				// (sur 14 chiffres)
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
	@Transactional(propagation = Propagation.SUPPORTS)
	public Boolean isSiretValid(String siren, String nic) {
		if (null == siren || null == nic) {
			return Boolean.FALSE;
		}

		int sum = 0;
		StringBuilder siret = new StringBuilder();
		siret.append(siren);
		siret.append(nic);

		for (int i = 0; i < siret.length(); i++) {
			int value = Integer.parseInt(String.valueOf(siret.charAt(i)));
			if (i % 2 != 0) {
				sum += value;
			} else {
				sum += 2 * value > 9 ? 2 * value - 9 : 2 * value;
			}
		}

		if (sum % 10 != 0) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findByAcronym(String acronym) {
		Assert.notNull(acronym, "acronym is mandatory. null value is forbidden");

		return this.dao.findByAcronym(acronym);
	}

}
