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
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.myec3.socle.core.domain.dao.ResourceDao;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.ResourceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * This class implements methods that provide business behaviours for all
 * entities inherited from {@link Resource}
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> : type of the entity, derived from {@link Resource}
 * @param <D> : type of the Dao, derived from {@link ResourceDao}
 */
@Transactional(readOnly = true)
public class ResourceServiceImpl<T extends Resource, D extends ResourceDao<T>> extends AbstractGenericServiceImpl<T, D>
		implements ResourceService<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findByIntervalId(Long minId, Long maxId) {
		// validate parameters
		Assert.notNull(minId, "minId is mandatory. null value is forbiden");
		Assert.notNull(maxId, "maxId is mandatory. null value is forbiden");
		Assert.isTrue(minId <= maxId, "minId must be inferior to maxId");

		return dao.findByIntervalId(minId, maxId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findByName(String name) {
		// validate parameters
		Assert.notNull(name, "name is mandatory. null value is forbiden");

		return dao.findByName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllByLabel(String label) {
		// validate parameters
		Assert.notNull(label, "label is mandatory. null value is forbiden");

		return dao.findAllByLabel(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllByPartialLabel(String label) {
		// validate parameters
		Assert.notNull(label, "label is mandatory. null value is forbiden");

		return dao.findAllByPartialLabel(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findByExternalId(Long externalId) {
		// validate parameters
		Assert.notNull(externalId, "externalId is mandatory. null value is forbiden");

		return dao.findByExternalId(externalId);
	}

	/**
	 * Validate a resource regardings its bean validation configuration
	 * 
	 * @param resource : resource to validate
	 * @throws IllegalArgumentException if resource is null
	 * @throws RuntimeException         if validation fails
	 */
	protected void validateResource(T resource) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator beanValidator = factory.getValidator();

		// exception is thrown if the bean is invalid
		Set<ConstraintViolation<T>> constraintViolations = beanValidator.validate(resource, Default.class);
		if (constraintViolations.size() != 0) {
			throw new RuntimeException("Invalid resource !" + constraintViolations);
		}
	}

}
