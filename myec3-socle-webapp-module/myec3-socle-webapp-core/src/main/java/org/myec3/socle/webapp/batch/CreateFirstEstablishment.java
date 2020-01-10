package org.myec3.socle.webapp.batch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AdministrativeState;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

/**
 * Class to create first establishment of a collection of companies
 * 
 * @author Ludovic LAVIGNE <ludovic.lavigne@worldline.com>
 */
public class CreateFirstEstablishment {

	/**
	 * Logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(CreateFirstEstablishment.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	private static CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Establishment} objects
	 */
	private static EstablishmentService establishmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	private static EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link CompanyDepartment} objects
	 */
	private static CompanyDepartmentService companyDepartmentService;

	public static void main(String[] args) {
		Long minCompanyId = new Long(0);
		Long maxCompanyId = new Long(0);
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-h")) {
					System.out.println("usage: CreateFirstEstablishment [-h -readonly]");
					System.out.println(" -h : affiche l'aide.");
					System.out.println(
							" -minCId<id> : identifiant minimum des entreprises à analyser (obligatoire si maxCId)");
					System.out.println(
							" -maxCId<id> : identifiant maximum des entreprises à analyser (obligatoire si minCId)");
					System.exit(0);
				}
				if (args[i].startsWith("-minCId")) {
					minCompanyId = Long.valueOf(args[i].substring(7));
				}
				if (args[i].startsWith("-maxCId")) {
					maxCompanyId = Long.valueOf(args[i].substring(7));
				}
			}
		}
		new CreateFirstEstablishment(minCompanyId, maxCompanyId);
	}

	/**
	 * Default constructor
	 */
	public CreateFirstEstablishment(Long minCompanyId, Long maxCompanyId) {
		if (this.init()) {
			logger.info("Début du traitement");
			List<Company> companies;

			// retrieve all companies or only several ...
			if (minCompanyId != 0 && maxCompanyId != 0) {
				companies = companyService.findByIntervalId(minCompanyId, maxCompanyId);
			} else {
				companies = companyService.findAll();
			}

			final Calendar calendar = Calendar.getInstance();
			Date executionDate = calendar.getTime();
			calendar.set(Calendar.YEAR, 2000);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.DATE, 1);
			Date initDate = calendar.getTime();

			AdministrativeState administrativeState = new AdministrativeState();
			administrativeState.setAdminStateLastUpdated(executionDate);
			administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);

			// for every company
			for (Company tempCompany : companies) {
				List<Establishment> tempEstablishments = establishmentService
						.findAllEstablishmentsByCompany(tempCompany);

				// check if company has establishments
				if (tempEstablishments.size() == 0) {
					logger.debug("Company with id " + tempCompany.getId() + " has no establishment yet");

					List<Establishment> companyEstablishments = new ArrayList<Establishment>();

					/** CREATE FIRST ESTABLISHMENT **/
					Establishment firstEstablishment = new Establishment();
					/** Address **/
					Address address = new Address();
					address.setCanton(tempCompany.getAddress().getCanton());
					address.setCity(tempCompany.getAddress().getCity());
					address.setCountry(tempCompany.getAddress().getCountry());
					address.setPostalAddress(tempCompany.getAddress().getPostalAddress());
					address.setPostalCode(tempCompany.getAddress().getPostalCode());

					firstEstablishment.setAddress(address);

					/** Administrative state **/
					firstEstablishment.setAdministrativeState(administrativeState);

					/** APE code **/
					firstEstablishment.setApeCode(tempCompany.getApeCode());

					/** APE code label **/
					firstEstablishment.setApeNafLabel(tempCompany.getApeNafLabel());

					/** Email **/
					firstEstablishment.setEmail(tempCompany.getEmail());

					/** FAX **/
					firstEstablishment.setFax(tempCompany.getFax());

					/** Foreign identifier **/
					firstEstablishment.setForeignIdentifier(tempCompany.getForeignIdentifier());

					/** Head office **/
					firstEstablishment.setIsHeadOffice(Boolean.FALSE);

					/** Label **/
					firstEstablishment.setLabel(tempCompany.getLabel().concat(" - Premier établissement"));

					/** Last update date **/
					firstEstablishment.setLastUpdate(initDate);

					/** Name **/
					firstEstablishment.setName(tempCompany.getLabel().concat(" - Premier établissement"));

					/** National ID **/
					firstEstablishment.setNationalID(tempCompany.getNationalID());

					/** NIC **/
					firstEstablishment.setNic(tempCompany.getNic());

					/** PHONE **/
					firstEstablishment.setPhone(tempCompany.getPhone());

					if (tempCompany.getSiren() != null && tempCompany.getNic() != null) {
						/** SIRET **/
						firstEstablishment.setSiret(tempCompany.getSiren() + tempCompany.getNic());
					}

					/** Establishment to Company **/
					firstEstablishment.setCompany(tempCompany);

					/** Company Administrative state **/
					tempCompany.setAdministrativeState(administrativeState);
					tempCompany.setLastUpdate(initDate);

					/** Add first establishment **/
					companyEstablishments.add(firstEstablishment);
					tempCompany.setEstablishments(companyEstablishments);

					// persist first establishment
					tempCompany = companyService.update(tempCompany);
					logger.info("Company with id " + tempCompany.getId() + " has establishments in database");

					/** RETRIEVE headOffice DATABASE object **/
					Establishment headOfficeDatabase = establishmentService.findAllEstablishmentsByCompany(tempCompany)
							.get(0);

					Assert.notNull(headOfficeDatabase, "Establishment cannot be null here");

					companyEstablishments.clear();
					companyEstablishments.add(headOfficeDatabase);

					Map<String, Long> knownNicCode = new HashMap<String, Long>();

					/** CHECK IF ADDITIONAL ESTABLISHMENTS NEEDED **/
					// retrieve all company's departments
					List<CompanyDepartment> companyDeparmentsOfCompany = companyDepartmentService
							.findAllDepartmentByCompany(tempCompany);

					for (CompanyDepartment tempCp : companyDeparmentsOfCompany) {
						// get all employees of current department
						List<EmployeeProfile> employeeProfiles = employeeProfileService
								.findAllEmployeeProfilesByCompanyDepartment(tempCp);
						logger.debug("Company Department " + tempCp.getId());

						Boolean isForeignCompany = tempCompany.getForeignIdentifier();
						// retrieve every employee profile
						for (EmployeeProfile tempEp : employeeProfiles) {
							logger.debug("Employee Profile " + tempEp.getId());

							if (isForeignCompany) {
								// case foreign company - one establishment and no more !
								tempEp.setEstablishment(headOfficeDatabase);
							} else {
								// if employee has custom establishment - create n establishments
								if (tempEp.getNic() == null || tempEp.getNic().equals("")
										|| (tempCompany.getNic() != null
												&& tempEp.getNic().equals(tempCompany.getNic()))) {

									tempEp.setEstablishment(headOfficeDatabase);
								} else {
									// check if we still have one establishment with this NIC
									Long tmpEstablishmentId = knownNicCode.get(tempEp.getNic());
									if (tmpEstablishmentId == null) {

										// TODO CHECK establishment not created yet
										Establishment customEstablishment = new Establishment();

										/** Address **/
										Address customAddress = new Address();
										customAddress.setCanton(tempEp.getAddress().getCanton());
										customAddress.setCity(tempEp.getAddress().getCity());
										customAddress.setCountry(tempEp.getAddress().getCountry());
										customAddress.setPostalAddress(tempEp.getAddress().getPostalAddress());
										customAddress.setPostalCode(tempEp.getAddress().getPostalCode());

										customEstablishment.setAddress(customAddress);

										/** Administrative state **/
										customEstablishment.setAdministrativeState(administrativeState);

										/** APE code **/
										customEstablishment.setApeCode(tempCompany.getApeCode());

										/** APE code label **/
										customEstablishment.setApeNafLabel(tempCompany.getApeNafLabel());

										/** Email **/
										customEstablishment.setEmail(tempCompany.getEmail());

										/** FAX **/
										customEstablishment.setFax(tempCompany.getFax());

										/** Foreign identifier **/
										customEstablishment.setForeignIdentifier(tempCompany.getForeignIdentifier());

										/** Head office **/
										customEstablishment.setIsHeadOffice(Boolean.FALSE);

										/** Label **/
										customEstablishment.setLabel(tempCompany.getLabel().concat(" - Etablissement"));

										/** Last update date **/
										customEstablishment.setLastUpdate(initDate);

										/** Name **/
										customEstablishment.setName(tempCompany.getLabel().concat(" - Etablissement"));

										/** National ID **/
										customEstablishment.setNationalID(tempCompany.getNationalID());

										/** NIC **/
										customEstablishment.setNic(tempEp.getNic());

										/** Phone **/
										customEstablishment.setPhone(tempCompany.getPhone());

										if (tempCompany.getSiren() != null && tempEp.getNic() != null) {
											/** SIRET **/
											customEstablishment.setSiret(tempCompany.getSiren() + tempEp.getNic());
										}

										/** Establishment to Company **/
										customEstablishment.setCompany(tempCompany);

										companyEstablishments.add(customEstablishment);
										tempCompany.setEstablishments(companyEstablishments);

										/** Company to establishments **/
										tempCompany = companyService.update(tempCompany);

										companyEstablishments.clear();
										companyEstablishments.addAll(
												establishmentService.findAllEstablishmentsByCompany(tempCompany));

										Establishment foundEstablishment = null;
										for (Establishment e : companyEstablishments) {
											if (e.getNic().equals(tempEp.getNic())) {
												foundEstablishment = e;
												break;
											}
										}
										Assert.notNull(foundEstablishment, "Found establishment cannot be null here");

										tempEp.setEstablishment(foundEstablishment);
										knownNicCode.put(foundEstablishment.getNic(), foundEstablishment.getId());

									} else {
										// associate with correct existing establishment
										Establishment retrievedEstablishment = establishmentService
												.findOne(tmpEstablishmentId);

										Assert.notNull(retrievedEstablishment,
												"RetrievedEstablishment cannot be null here");

										tempEp.setEstablishment(retrievedEstablishment);
									}
								}
							}
							tempEp = employeeProfileService.update(tempEp);
							Assert.notNull(tempEp.getEstablishment(),
									"Wrong situation, employee's establishment cannot be null here ...");
						}
					}
				} else {
					logger.debug("Company with id " + tempCompany.getId() + " has " + tempEstablishments.size()
							+ " establishment yet");
				}
			}
			logger.info("Fin du traitement");
		}
	}

	/**
	 * Initializes class
	 * 
	 * @return true if initialization was successful, false otherwise
	 */
	public boolean init() {
		boolean result = true;
		try {
			logger.info("Chargement des services");
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "classpath:coreMyec3Context.xml", "classpath*:coreMyec3Context.xml" });
			companyService = (CompanyService) applicationContext.getBean("companyService");
			establishmentService = (EstablishmentService) applicationContext.getBean("establishmentService");
			employeeProfileService = (EmployeeProfileService) applicationContext.getBean("employeeProfileService");
			companyDepartmentService = (CompanyDepartmentService) applicationContext
					.getBean("companyDepartmentService");
		} catch (Exception e) {
			logger.error("An error occured when initializing business services");
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
