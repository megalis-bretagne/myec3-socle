package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Customer;

/**
 * Interface defining Business Services methods and providing {@link Customer}
 * specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 */
public interface CustomerService extends ResourceService<Customer> {

	/**
	 * Find {@link Customer} by the given {@link AdminProfile}
	 * 
	 * @param adminProfile
	 *            : the {@link AdminProfile} to search on
	 * @return the {@link Customer} associated at the given {@link AdminProfile}
	 */
	Customer findByAdminProfile(AdminProfile adminProfile);

	/**
	 * This method allows to populate customer's collections
	 * 
	 * @param agentProfile
	 *            : agentProfile to populate
	 * 
	 * @throws IllegalArgumentException
	 *             if agentProfile is null
	 */
	void populateCollections(Customer customer);
}
