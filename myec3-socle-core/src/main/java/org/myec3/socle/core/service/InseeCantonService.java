package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.InseeCanton;

/**
 * Interface defining Business Services methods and providing
 * {@link InseeCanton} specific operations. This interface extends the common
 * {@link GenericProfileService} interface by adding new specific methods
 *
 * @author Matthieu Gaspard <matthieu.gaspard@worldline.com>
 */
public interface InseeCantonService extends IGenericService<InseeCanton> {

	public InseeCanton findCanton(Long regionId, String countyId, Long cantonId);

	/**
	 * Find the highest id from the canton table
	 *
	 * @return the highest id from the canton table Return null in case of error.
	 */
	public Long getCantonMaxId();
}
