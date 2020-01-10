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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.dao.EstablishmentDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.domain.model.enums.MpsUpdateTypeValue;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link Establishment} objects. These methods complete or override parent
 * methods from {@link ResourceService} and {@link ResourceServiceImpl}
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
@Service("establishmentService")
public class EstablishmentServiceImpl extends ResourceServiceImpl<Establishment, EstablishmentDao>
		implements EstablishmentService {

	private static final Log logger = LogFactory.getLog(EstablishmentServiceImpl.class);

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Company} objects. The concrete service implementation is injected by
	 * Spring container
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Establishment> findAllEstablishmentsByCompany(Company company) {
		Assert.notNull(company, "Company is mandatory, it cannot be null");
		return this.dao.findAllEstablishments(company);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Establishment findHeadOfficeEstablishmentByCompany(Company company) {
		Assert.notNull(company, "Company is mandatory, it cannot be null");
		return this.dao.findHeadOfficeEstablishment(company);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(Establishment establishment) {
		Assert.notNull(establishment, "companyDepartment is null. null value is forbiden");

		logger.info("Populating Establishment's collections...");

		Company clonedCompany = null;

		try {
			clonedCompany = (Company) this.companyService.findOne(establishment.getCompany().getId()).clone();
			this.companyService.populateCollections(clonedCompany);
			establishment.setCompany(clonedCompany);
		} catch (CloneNotSupportedException e) {
			logger.error("Canno't clone Company " + establishment.getCompany(), e);
			// we try to perfom operation directly on the object.
			// FIXME: is the clone operation still usefull ?
			this.companyService.populateCollections(clonedCompany);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(Establishment establishment) {
		Assert.notNull(establishment, "establishment is null. null value is forbiden");
		logger.info("Cleaning collections of establishment...");

		this.companyService.cleanCollections(establishment.getCompany());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public Establishment update(Establishment resource) {
		logger.info("Updating establishment...");
		try {
			if (resource.getIsHeadOffice().equals(Boolean.TRUE)) {
				Company company = resource.getCompany();
				logger.info("Updating Company " + company);
				Assert.notNull(company,
						"resource Establishment has no Company attached. An Establishment must have an associated Company.");
				company.setAddress(resource.getAddress());
				company.setPhone(resource.getPhone());
				company.setFax(resource.getFax());
				company.setEmail(resource.getEmail());
				company.setNic(resource.getNic());
				companyService.update(company);
			}
			return super.update(resource);
		} catch (RuntimeException re) {
			logger.error("Establishment could not be updated", re);
			return null;
		}
	}

	public List<MpsUpdateJob> getEstablishmentToUpdate() {
		// object to return
		List<MpsUpdateJob> updateQueueToReturn = new ArrayList<MpsUpdateJob>();
		Boolean switchMachine = Boolean.FALSE;

		List<Establishment> EstablishmentListToUpdate = new ArrayList<Establishment>();

		// get all the companies that need update
		EstablishmentListToUpdate = this.dao.getEstablishmentToUpdate();

		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
		SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		formatForBdd.setCalendar(cal);

		if (!(EstablishmentListToUpdate.isEmpty())) {
			logger.info("Numbers of MpsUpdateJob to populate : " + EstablishmentListToUpdate.size());

			for (Establishment findEstablishment : EstablishmentListToUpdate) {

				if ((findEstablishment.getLastUpdate() == null)
						|| (findEstablishment.getLastUpdate().before(formatForBdd.getCalendar().getTime()))) {
					MpsUpdateJob tmpUpdateQueue = new MpsUpdateJob();
					tmpUpdateQueue.setId(findEstablishment.getId());
					tmpUpdateQueue.setType(ResourceType.ESTABLISHMENT.getLabel());
					tmpUpdateQueue.setPriority(MpsUpdateTypeValue.AUTOMATIC.getLabel());
					if (switchMachine.booleanValue()) {
						tmpUpdateQueue.setMachineName(MyEc3MpsUpdateConstants.getHostname1());
						switchMachine = Boolean.FALSE;
					} else {
						tmpUpdateQueue.setMachineName(MyEc3MpsUpdateConstants.getHostname2());
						switchMachine = Boolean.TRUE;
					}
					logger.info("getEstablishmentToUpdate Create (but not persist) MpsUpdateJob : "
							+ tmpUpdateQueue.toString());
					updateQueueToReturn.add(tmpUpdateQueue);
				}

			}
		}
		logger.debug("getEstablishmentToUpdate Success !");
		return updateQueueToReturn;
	}

	public List<MpsUpdateJob> getEstablishmentToUpdateByCompany(Company company, MpsUpdateTypeValue updateType) {
		// object to return
		List<MpsUpdateJob> updateQueueToReturn = new ArrayList<MpsUpdateJob>();
		Boolean switchMachine = Boolean.FALSE;

		List<Establishment> EstablishmentListToUpdate = new ArrayList<Establishment>();

		// get all the companies that need update
		if (updateType == MpsUpdateTypeValue.MANUAL) {
			EstablishmentListToUpdate = this.dao.getEstablishmentManualUpdateByCompany(company);
		} else {
			EstablishmentListToUpdate = this.dao.getEstablishmentToUpdateByCompany(company);
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
		SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		formatForBdd.setCalendar(cal);

		if (!(EstablishmentListToUpdate.isEmpty())) {

			for (Establishment findEstablishment : EstablishmentListToUpdate) {

				MpsUpdateJob tmpUpdateQueue = new MpsUpdateJob();
				tmpUpdateQueue.setId(findEstablishment.getId());
				tmpUpdateQueue.setType(ResourceType.ESTABLISHMENT.getLabel());
				if (updateType.equals(MpsUpdateTypeValue.AUTOMATIC)) {
					tmpUpdateQueue.setPriority(MpsUpdateTypeValue.AUTOMATIC.getLabel());
				} else if (updateType.equals(MpsUpdateTypeValue.CONNECTIONINFO)) {
					tmpUpdateQueue.setPriority(MpsUpdateTypeValue.CONNECTIONINFO.getLabel());
				} else if (updateType.equals(MpsUpdateTypeValue.MANUAL)) {
					tmpUpdateQueue.setPriority(MpsUpdateTypeValue.MANUAL.getLabel());
				}
				if (switchMachine.booleanValue()) {
					tmpUpdateQueue.setMachineName(MyEc3MpsUpdateConstants.getHostname1());
					switchMachine = Boolean.FALSE;
				} else {
					tmpUpdateQueue.setMachineName(MyEc3MpsUpdateConstants.getHostname2());
					switchMachine = Boolean.TRUE;
				}
				logger.debug("getEstablishmentToUpdateByCompany Create MpsUpdateJob : " + tmpUpdateQueue.toString());
				updateQueueToReturn.add(tmpUpdateQueue);
			}

		}
		logger.debug("getEstablishmentToUpdateByCompany Success !");
		return updateQueueToReturn;
	}

	public Establishment findLastForeignCreated(String nationalId) {
		return this.dao.findLastForeignCreatedDao(nationalId);
	}

	public Establishment findByNic(String siren, String nic) {
		return this.dao.findByNic(siren, nic);
	}
}
