package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;

public interface ServiceManager {

	/**
	 * @param resourceType
	 *            : the type of the resource
	 * @return correct resource service depending on the resource type
	 */
	ResourceService<?> getResourceServiceByResourceType(
			ResourceType resourceType);

	/**
	 * @param resource
	 *            : the resource used
	 * @return correct resource service depending on the resource class
	 */
	ResourceService<?> getResourceServiceByClassName(Resource resource);
}
