package org.myec3.socle.ws.batch.job;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.ConnectionInfosService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.MpsUpdateJobService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GenerateUpdateList {

	private static Logger logger = LoggerFactory.getLogger(GenerateUpdateList.class);

	@Autowired
	@Qualifier("mpsUpdateJobService")
	private MpsUpdateJobService mpsUpdateJobService;

	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	@Autowired
	@Qualifier("connectionInfosService")
	private ConnectionInfosService connectionInfosService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	public void populateUpdateResource() {
		logger.warn("Starting to populate MpsUpdateJob.");
		List<MpsUpdateJob> mpsUpdateJobInsert = new ArrayList<MpsUpdateJob>();

		// CASE AUTO UPDATE
		logger.info("Starting AUTO Update.");
		// add all the company that need update
		mpsUpdateJobInsert.addAll(this.companyService.getCompanyToUpdate());
		logger.debug("Add Companies done.");

		// add all the establishment that need update
		mpsUpdateJobInsert.addAll(this.establishmentService.getEstablishmentToUpdate());
		logger.debug("Add Establishment done.");

		logger.info("AUTO Update done.");

		// CASE CONNECTIONINFOS UPDATE
		logger.info("Starting CONNECTIONINFO Update.");
		mpsUpdateJobInsert.addAll(this.connectionInfosService.getUserCompanyToUpdate());
		logger.info("CONNECTIONINFO Update done.");

		for (MpsUpdateJob insertMpsUpdateJob : mpsUpdateJobInsert) {
			logger.debug("Trying to create the following MpsUpdateJob : " + insertMpsUpdateJob.toString());

			// We check if the MpsUpdateJob already exists
			MpsUpdateJob existingMpsUpdate = this.mpsUpdateJobService.findOne(insertMpsUpdateJob.getId());

			// if it exists, we check the priority
			// Update only if priority is higher (and getPriority is lower)
			if (existingMpsUpdate == null || (Integer
					.valueOf(existingMpsUpdate.getPriority()) > (Integer.valueOf(insertMpsUpdateJob.getPriority())))) {

				try {
					mpsUpdateJobService.create(insertMpsUpdateJob);
					logger.info("MpsUpdateJob Create with success : " + insertMpsUpdateJob.toString());
				} catch (Exception e) {
					logger.info("Error while creating MpsUpdateJob : " + e);
				}

			} else {
				logger.debug("MpsUpdateJob already exist and priority is not higher");
			}

		}

		logger.warn("MpsUpdateJob populate is done.");
	}

}
