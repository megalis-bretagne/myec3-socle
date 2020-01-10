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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.OrganismStatusDao;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.OrganismStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("organismStatusService")
public class OrganismStatusServiceImpl extends AbstractGenericServiceImpl<OrganismStatus, OrganismStatusDao>
		implements OrganismStatusService {

	private static final Log logger = LogFactory.getLog(OrganismStatusServiceImpl.class);

	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	@Autowired
	@Qualifier("organismStatusDao")
	private OrganismStatusDao organismStatusDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OrganismStatus> findAllOrganismStatusByOrganism(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");
		return this.organismStatusDao.findAllOrganismStatusByOrganism(organism);
	}

}
