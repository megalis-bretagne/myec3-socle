package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.CustomerDao;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link Customer} objects.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 */
@Repository("customerDao")
public class JpaCustomerDaoImpl extends JpaResourceDao<Customer> implements CustomerDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Customer findByAdminProfile(AdminProfile adminProfile) {
		this.getLog().debug("Finding Customer with adminProfile: " + adminProfile.getLabel());
		try {
			Query query = this.getEm().createQuery("SELECT c from " + this.getDomainClass().getSimpleName()
					+ " c inner join c.adminProfiles a WHERE a =:adminProfile");
			query.setParameter("adminProfile", adminProfile);
			Customer customer = (Customer) query.getSingleResult();
			this.getLog().debug("findByAdminProfile successfull.");
			return customer;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public Class<Customer> getType() {
		return Customer.class;
	}
}
