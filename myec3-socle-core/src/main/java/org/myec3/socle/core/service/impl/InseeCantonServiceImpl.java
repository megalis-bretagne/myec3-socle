package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.InseeCantonDao;
import org.myec3.socle.core.domain.model.InseeCanton;
import org.myec3.socle.core.service.InseeCantonService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link InseeCanton} objects. These methods complete or override parent
 * methods from profile or Resource services
 *
 * @author Matthieu Gaspard <matthieu.gaspard@atosorigin.com>
 */

@Service("inseeCantonService")
public class InseeCantonServiceImpl extends AbstractGenericServiceImpl<InseeCanton, InseeCantonDao>
		implements InseeCantonService {

	public InseeCanton findCanton(Long regionId, String countyId, Long cantonId) {
		Assert.notNull(regionId, "Region Id is mandatory, it cannot be null");
		Assert.notNull(countyId, "Department Id is mandatory, it cannot be null");
		Assert.notNull(cantonId, "Canton Id is mandatory, it cannot be null");
		return this.dao.findCanton(regionId, countyId, cantonId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getCantonMaxId() {
		return this.dao.getCantonMaxId();
	}
}