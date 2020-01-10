package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Customer;

/**
 * DAO interface for {@link Customer} objects. This interface defines global
 * methods that could be called on {@link Customer}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 */
public interface CustomerDao extends ResourceDao<Customer> {

	/**
	 * Find {@link Customer} by the given {@link AdminProfile}
	 * 
	 * @param adminProfile
	 *            : the {@link AdminProfile} to search on
	 * @return the {@link Customer} associated at the given {@link AdminProfile}
	 */
	Customer findByAdminProfile(AdminProfile adminProfile);
}
