package org.myec3.socle.core.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.dao.CustomerDao;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link Customer} objects. These methods complete or override parent methods
 * from {@link ResourceServiceImpl}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 */
@Service("customerService")
public class CustomerServiceImpl extends ResourceServiceImpl<Customer, CustomerDao> implements CustomerService {

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Application} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Customer findByAdminProfile(AdminProfile adminProfile) {
		Assert.notNull(adminProfile, "adminProfile is mandatory. null value is forbidden");
		return this.dao.findByAdminProfile(adminProfile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(Customer customer) {
		Assert.notNull(customer, "Cannot populate collections of Customer : customer cannot be null");

		List<Application> applicationList = this.applicationService.findAllApplicationsByCustomer(customer);

		customer.setApplications(applicationList);
	}
}
