package org.myec3.socle.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.dao.ConnectionInfosDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.enums.MpsUpdateTypeValue;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ConnectionInfosService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("connectionInfosService")
public class ConnectionInfosServiceImpl extends AbstractGenericServiceImpl<ConnectionInfos, ConnectionInfosDao>
		implements ConnectionInfosService {

	private static final Log logger = LogFactory.getLog(ConnectionInfosServiceImpl.class);

	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Override
	@Transactional(readOnly = false)
	public ConnectionInfos update(ConnectionInfos conInfos, Long timestamp) {

		Assert.notNull(conInfos, "ConInfos cannot be null");

		Long newMeanTimeBetweenTwoConnections;

		// Si deuxième connection cas N0
		if (conInfos.getNbConnections() == 1) {
			conInfos.setMeanTimeBetweenTwoConnections(timestamp - conInfos.getLastConnectionDate().getTime());
			conInfos.setLastConnectionDate(new Date(timestamp));
			conInfos.setNbConnections(2);
		} else {
			// Sinon cas N+1
			newMeanTimeBetweenTwoConnections = conInfos.getMeanTimeBetweenTwoConnections()
					+ (((timestamp - conInfos.getLastConnectionDate().getTime())
							- conInfos.getMeanTimeBetweenTwoConnections()) / conInfos.getNbConnections());

			conInfos.setLastConnectionDate(new Date(timestamp));
			conInfos.setMeanTimeBetweenTwoConnections(newMeanTimeBetweenTwoConnections);
			conInfos.setNbConnections(conInfos.getNbConnections() + 1);
		}

		return update(conInfos);
	}

	public List<ConnectionInfos> getRecentConnectionInfos() {

		return this.dao.getRecentConnectionInfos();
	}

	public Company getConnectionInfosCompany(ConnectionInfos connectionInfos) {
		logger.debug("Get ConnectionInfos related Company");
		Assert.notNull(connectionInfos, "connectionInfos cannot be null");
		Long connectionInfosUserId = this.userService.getConnectionInfosRelatedUser(connectionInfos);
		Profile connectionInfosProfileId = this.profileService.findByUserId(connectionInfosUserId);
		if (connectionInfosProfileId == null) {
			logger.info("User has no profile : " + connectionInfosUserId);
			return null;
		}
		if (connectionInfosProfileId.getProfileType().getValue().getLabel()
				.equals(ProfileTypeValue.EMPLOYEE.getLabel())) {
			EmployeeProfile connectionInfosEmployeeProfile = this.employeeProfileService
					.findOne(connectionInfosProfileId.getId());
			if (connectionInfosEmployeeProfile == null) {
				logger.warn("Couldn't find EmployeeProfile from following connectionInfo : " + connectionInfos.getId());
				return null;
			}

			Establishment establishmentEmployeeProfile = connectionInfosEmployeeProfile.getEstablishment();

			if (establishmentEmployeeProfile == null) {
				logger.warn("Couldn't find Establishment from following connectionInfo : " + connectionInfos.getId());
				return null;
			}

			Company companyEmployeeProfile = establishmentEmployeeProfile.getCompany();

			if (companyEmployeeProfile == null) {
				logger.warn("Couldn't find Company from following connectionInfo : " + connectionInfos.getId());
				return null;
			}

			logger.debug("Get ConnectionInfos related Establishment OK");
			return companyEmployeeProfile;
		} else {
			logger.debug("Profile is not an Employee !");
			return null;
		}

	}

	public List<MpsUpdateJob> getUserCompanyToUpdate() {

		List<MpsUpdateJob> listToReturn = new ArrayList<MpsUpdateJob>();
		Boolean switchMachine = Boolean.FALSE;

		logger.debug("Starting ConnectionInfos getUserCompanyToUpdate");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getDays()));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		format.setCalendar(cal);

		// get the last connection informations
		List<ConnectionInfos> recentConnectionInfos = new ArrayList<ConnectionInfos>();
		recentConnectionInfos = this.getRecentConnectionInfos();

		// if we have some recent connections
		if (!(recentConnectionInfos.isEmpty())) {

			for (ConnectionInfos singleRecentConnectionInfos : recentConnectionInfos) {

				// get establishment related to ConnectionInfo
				// ConnectionInfo -> User -> Profile -> EmployeeProfile ->
				// Establishment
				Company companyEmployeeProfile = this.getConnectionInfosCompany(singleRecentConnectionInfos);
				if (companyEmployeeProfile == null) {
					logger.debug("companyEmployeeProfile is null for this connectionInfo : "
							+ singleRecentConnectionInfos.getId());
					continue;
				}

				// Check if last update is recent or not
				if (companyEmployeeProfile.getLastUpdate() == null
						|| companyEmployeeProfile.getLastUpdate().before(format.getCalendar().getTime())) {

					// Create new MPS update
					MpsUpdateJob CompanyMpsUpdateJobInsert = new MpsUpdateJob();
					CompanyMpsUpdateJobInsert.setId(companyEmployeeProfile.getId());
					CompanyMpsUpdateJobInsert.setPriority(MpsUpdateTypeValue.CONNECTIONINFO.getLabel());
					CompanyMpsUpdateJobInsert.setType(ResourceType.COMPANY.getLabel());
					if (switchMachine.booleanValue()) {
						CompanyMpsUpdateJobInsert.setMachineName(MyEc3MpsUpdateConstants.getHostname1());
						switchMachine = Boolean.FALSE;
					} else {
						CompanyMpsUpdateJobInsert.setMachineName(MyEc3MpsUpdateConstants.getHostname2());
						switchMachine = Boolean.TRUE;
					}
					logger.debug(
							"getUserCompanyToUpdate Create MpsUpdateJob : " + CompanyMpsUpdateJobInsert.toString());
					listToReturn.add(CompanyMpsUpdateJobInsert);

					listToReturn.addAll(establishmentService.getEstablishmentToUpdateByCompany(companyEmployeeProfile,
							MpsUpdateTypeValue.CONNECTIONINFO));

				} else {
					logger.debug("Establishment update < 5 days");
				}

			}
		}
		return listToReturn;
	}

}
