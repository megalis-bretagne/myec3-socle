package org.myec3.socle.core.service;

import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link org.myec3.socle.core.service.impl.ProfileServiceImpl}
 * 
*
 * 
 */
@Transactional
public class ProfileServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private AgentProfileService agentProfileService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private OrganismService organismService;

	@Autowired
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmployeeProfileService employeeProfileService;

	@Autowired
	private CompanyDepartmentService companyDepartmentService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProfileTypeService profileTypeService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AcronymsListService acronymsListService;

	@Autowired
	private StructureTypeService structureTypeService;

	private Application application;
	private Role role1;
	private Role role2;
	private AgentProfile agent;
	private User user;
	private OrganismDepartment organismDepartment;
	private Organism organism;
	private Customer customer;
	private CompanyDepartment companyDepartment;
	private EmployeeProfile employee;
	private Company company;
	private Person person;

	private static final String ORGANISM_NAME = "Test Organism "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_DEPARTMENT_NAME = "Organisme de la région "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String USER_NAME = "Jean Dupont";
	private static String USER_FINAL_NAME;

	private static final String APPLICATION_NAME = "Gestion documentaire "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ROLE_NAME_1 = "Administrateur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ROLE_NAME_2 = "Contributeur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String AGENT_NAME = "Jean Bob";
	private static String AGENT_FINAL_NAME;

	private static final String COMPANY_NAME = "Test Organism "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String PERSON_NAME_1 = "Jean Bob"
			+ Calendar.getInstance().getTimeInMillis();
	private static final String COMPANY_DEPARTMENT_NAME = "Direction "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String EMPLOYEE_NAME = "Billy Bob";
	private static String EMPLOYEE_FINAL_NAME;
	private static final String EMAIL_AGENT = "agent"
			+ Calendar.getInstance().getTimeInMillis() + "@test.com";
	private static final String EMAIL_EMPLOYEE = "employee"
			+ Calendar.getInstance().getTimeInMillis() + "@test.com";
	private static final String CUSTOMER_NAME = "Test Customer";

	private static final String ACRONYM0 = "a9z";
	private static final String ACRONYM1 = "q7q";
	private static final String ACRONYM2 = "p7p";

	/**
	 * Initialization
	 * 
	 * @throws Exception
	 *             in case of error
	 */
	@Before
	public void setUp() throws Exception {

		AcronymsList acronym0 = new AcronymsList();
		acronym0.setValue(ACRONYM0);
		acronym0.setAvailable(true);
		acronymsListService.create(acronym0);

		AcronymsList acronym1 = new AcronymsList();
		acronym1.setValue(ACRONYM1);
		acronym1.setAvailable(true);
		acronymsListService.create(acronym1);

		AcronymsList acronym2 = new AcronymsList();
		acronym2.setValue(ACRONYM2);
		acronym2.setAvailable(true);
		acronymsListService.create(acronym2);

		// Create StructureType
		StructureType organismType = new StructureType();
		organismType.setValue(StructureTypeValue.ORGANISM);
		this.structureTypeService.create(organismType);

		StructureType companyType = new StructureType();
		companyType.setValue(StructureTypeValue.COMPANY);
		this.structureTypeService.create(companyType);

		// Create ProfileType
		ProfileType agentType = new ProfileType();
		agentType.setValue(ProfileTypeValue.AGENT);
		this.profileTypeService.create(agentType);

		ProfileType employeeType = new ProfileType();
		employeeType.setValue(ProfileTypeValue.EMPLOYEE);
		this.profileTypeService.create(employeeType);

		ProfileType adminType = new ProfileType();
		adminType.setValue(ProfileTypeValue.ADMIN);
		this.profileTypeService.create(adminType);

		// Init Commons

		Address address = new Address();
		address.setPostalAddress("17 boulevard de la Trémouille - BP 1602");
		address.setPostalCode("21035");
		address.setCanton("Bourgogne");
		address.setCity("Dijon");
		address.setCountry(Country.FR);

		user = new User();
		user.setName(USER_NAME);
		user.setFirstname("Jean");
		user.setLastname("Dupont");
		user.setCivility(Civility.MR);
		user.setEnabled(Boolean.TRUE);
		user.setPassword("password");
		user.setUsername("jean.dupont "
				+ Calendar.getInstance().getTimeInMillis());
		userService.create(user);
		USER_FINAL_NAME = user.getName();

		// test User creation
		user = userService.findByName(user.getName());
		assertNotNull("User should not be null", user);
		assertEquals("User Name should be equals to " + USER_FINAL_NAME,
				USER_FINAL_NAME, user.getName());

		application = new Application();
		application.setName(APPLICATION_NAME);
		application.setUrl("http://urlged");

		applicationService.create(application);

		// test Application creation
		application = applicationService.findByName(APPLICATION_NAME);
		assertNotNull("Application should not be null", application);
		assertEquals(
				"Application Name should be equals to " + APPLICATION_NAME,
				APPLICATION_NAME, application.getName());

		// Init customer
		customer = new Customer();
		customer.setName(CUSTOMER_NAME);
		customer.setLabel("label");
		customer.addApplication(application);
		customerService.create(customer);

		// Init Organism
		organism = new Organism();
		organism.setName(ORGANISM_NAME);
		organism.setLabel("OrganismLabel");
		organism.setDescription("OrganismDescription");
		organism.setPhone("0000000000");
		organism.setArticle(Article.LE);
		organism.setMember(true);
		organism.setLegalCategory(OrganismINSEECat._4_1_10);
		organism.setEmail("organisme"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organism.setPhone("+33(0)000000000");
		organism.setEnabled(Boolean.TRUE);
		organism.setAddress(address);
		organism.setAcronym("a9z");
		organism.setCustomer(customer);

		organismService.create(organism);

		// test Organism creation
		organism = organismService.findByName(ORGANISM_NAME);
		assertNotNull("Organism should not be null", organism);
		assertEquals("Organism Name should be equals to " + ORGANISM_NAME,
				ORGANISM_NAME, organism.getName());

		organismDepartment = new OrganismDepartment();
		organismDepartment.setName(ORGANISM_DEPARTMENT_NAME);
		organismDepartment.setAddress(address);
		organismDepartment.setEmail("organisme"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organismDepartment.setDescription("Description de l'organisme.");
		organismDepartment.setFax("+33(0)000000000");
		organismDepartment.setPhone("+33(0)000000000");
		organismDepartment.setSiren("732829320");
		organismDepartment.setWebsite("azerty@myDomain.fr");
		organismDepartment.setOrganism(organism);
		organismDepartment.setParentDepartment(organismDepartmentService
				.findRootOrganismDepartment(organism));

		organismDepartmentService.create(organismDepartment);

		// test OrganismDepartment creation
		organismDepartment = organismDepartmentService
				.findByName(ORGANISM_DEPARTMENT_NAME);
		assertNotNull("OrganismDepartment should not be null",
				organismDepartment);
		assertEquals("OrganismDepartment Name should be equals to "
				+ ORGANISM_DEPARTMENT_NAME, ORGANISM_DEPARTMENT_NAME,
				organismDepartment.getName());

		role1 = new Role();
		role1.setName(ROLE_NAME_1);
		role1.setApplication(application);

		roleService.create(role1);

		// test role creation
		role1 = roleService.findByName(ROLE_NAME_1);
		assertNotNull("Role should not be null", role1);
		assertEquals("Role Name should be equals to " + ROLE_NAME_1,
				ROLE_NAME_1, role1.getName());

		role2 = new Role();
		role2.setName(ROLE_NAME_2);
		role2.setApplication(application);

		roleService.create(role2);

		// test role creation
		role2 = roleService.findByName(ROLE_NAME_2);
		assertNotNull("Role should not be null", role2);
		assertEquals("Role Name should be equals to " + ROLE_NAME_2,
				ROLE_NAME_2, role2.getName());

		List<Role> listRole = new ArrayList<Role>();
		listRole.add(role1);
		listRole.add(role2);

		agent = new AgentProfile();
		agent.setName(AGENT_NAME);
		agent.setAddress(address);
		agent.setEmail(EMAIL_AGENT);
		agent.setEnabled(Boolean.TRUE);
		agent.setPhone("+33(0)000000000");
		agent.setFax("+33(0)000000000");
		agent.setElected(Boolean.FALSE);
		agent.setOrganismDepartment(organismDepartment);
		agent.setUser(user);
		agent.setFunction("Agent responsable");
		agent.setPrefComMedia(PrefComMedia.EMAIL);
		agent.setRoles(listRole);

		agentProfileService.create(agent);
		AGENT_FINAL_NAME = agent.getName();

		// Test if Agent Profile was successfull
		Resource foundResource = agentProfileService
				.findByName(AGENT_FINAL_NAME);
		assertNotNull("Agent should not be null", foundResource);
		assertEquals("Agent Name should be equals to " + AGENT_FINAL_NAME,
				AGENT_FINAL_NAME, foundResource.getName());

		// Init Employee Profile

		person = new Person();
		person.setName(PERSON_NAME_1);
		person.setCivility(Civility.MR);
		person.setFirstname("Jean");
		person.setLastname("Bob");
		person.setType("PP");

		company = new Company();
		company.setName(COMPANY_NAME);
		company.setAddress(address);
		company.setDescription("Description de la companie.");
		company.setEmail("dircom-dg" + Calendar.getInstance().getTimeInMillis()
				+ "@test.com");
		company.setEnabled(Boolean.TRUE);
		company.setPhone("+33(0)000000000");
		company.setFax("+33(0)000000000");
		company.setForeignIdentifier(null);
		company.setIconUrl("http://iconurl");
		company.setLogoUrl("http://logourl");
		company.setLegalCategory(CompanyINSEECat.SA);
		company.setWebsite("http://www.myWebsite.com");
		company.setSiren("378901946");
		company.setNic("00483");
		company.setApeCode("723Z");
		company.addResponsible(person);
		company.setRegistrationCountry(Country.FR);
		company.setAcronym("q7q");
		company.setRM(Boolean.TRUE);
		company.setRCS(Boolean.TRUE);

		companyService.create(company);

		// test company creation
		company = companyService.findByName(COMPANY_NAME);
		assertNotNull("Company should not be null", company);
		assertEquals("Company Name should be equals to " + COMPANY_NAME,
				COMPANY_NAME, company.getName());

		companyDepartment = new CompanyDepartment();
		companyDepartment.setName(COMPANY_DEPARTMENT_NAME);
		companyDepartment.setAddress(address);
		companyDepartment.setEmail("fi"
				+ Calendar.getInstance().getTimeInMillis() + "@myDomain.com");
		companyDepartment.setDescription("Description de FI.");
		companyDepartment.setFax("+33(0)000000000");
		companyDepartment.setPhone("+33(0)000000000");
		companyDepartment.setSiren("378901946");
		companyDepartment.setAcronym("p7p");
		companyDepartment.setNic("00483");
		companyDepartment.setWebsite("www.fi.fr");
		companyDepartment.setCompany(company);

		companyDepartmentService.create(companyDepartment);

		// test company Department creation
		companyDepartment = companyDepartmentService
				.findByName(COMPANY_DEPARTMENT_NAME);
		assertNotNull("Company Department should not be null",
				companyDepartment);
		assertEquals("Company Department Name should be equals to "
				+ COMPANY_DEPARTMENT_NAME, COMPANY_DEPARTMENT_NAME,
				companyDepartment.getName());

		employee = new EmployeeProfile();
		employee.setName(EMPLOYEE_NAME);
		employee.setAddress(address);
		employee.setEmail(EMAIL_EMPLOYEE);
		employee.setEnabled(Boolean.TRUE);
		employee.setPhone("+33(0)000000000");
		employee.setFax("+33(0)000000000");
		employee.setFunction("Responsable d'application");
		employee.setPrefComMedia(PrefComMedia.EMAIL);
		employee.setUser(user);
		employee.setCompanyDepartment(companyDepartment);
		employee.setRoles(listRole);

		employeeProfileService.create(employee);
		EMPLOYEE_FINAL_NAME = employee.getName();

		// Test if Employee Profile was successfull
		employee = employeeProfileService.findByName(EMPLOYEE_FINAL_NAME);
		assertNotNull("Employee should not be null", employee);
		assertEquals(
				"Employee Name should be equals to " + EMPLOYEE_FINAL_NAME,
				EMPLOYEE_FINAL_NAME, employee.getName());

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test
	public void testFindAllProfilesByUser() {
		List<Profile> resultList = profileService.findAllProfilesByUser(user);

		assertNotNull("result profiles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result profiles list should not be empty",
				resultList.isEmpty());

		// test if there is exactly two result
		assertTrue("result profiles list should contain exactly two result",
				resultList.size() == 2);

		for (Profile profile : resultList) {
			if (profile.isAgent()) {
				assertEquals("profile name should be equals to "
						+ AGENT_FINAL_NAME, AGENT_FINAL_NAME, profile.getName());
				continue;
			}
			if (profile.isEmployee()) {
				assertEquals("profile name should be equals to "
						+ EMPLOYEE_FINAL_NAME, EMPLOYEE_FINAL_NAME,
						profile.getName());
				continue;
			}
			fail("Profile should be Agent or Employee");
		}

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllProfilesByUser() {
		this.profileService.findAllProfilesByUser(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test
	public void testEmptyFindAllProfilesByUser() {
		User testUser = user;
		// reset id to simulate an invalid user
		testUser.setId(null);
		List<Profile> resultList = this.profileService
				.findAllProfilesByUser(testUser);
		assertNotNull("list of profiles should not be null", resultList);
		assertTrue("list of profiles should be empty", resultList.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test
	public void testFindAllProfilesByRole() {
		List<Profile> resultList = profileService.findAllProfilesByRole(role1);

		assertNotNull("result roles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result roles list should not be empty",
				resultList.isEmpty());

		// test if there is exactly two result
		assertTrue("result roles list should contain exactly two result",
				resultList.size() == 2);

		for (Profile profile : resultList) {
			if (profile.isAgent()) {
				assertEquals("role name should be equals to "
						+ AGENT_FINAL_NAME, AGENT_FINAL_NAME, profile.getName());
				continue;
			}
			if (profile.isEmployee()) {
				assertEquals("role name should be equals to "
						+ EMPLOYEE_FINAL_NAME, EMPLOYEE_FINAL_NAME,
						profile.getName());
				continue;
			}
			fail("Profile should be Agent or Employee");
		}
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllProfilesByRole() {
		this.profileService.findAllProfilesByRole(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test
	public void testEmptyFindAllProfilesByRole() {
		Role testRole = role1;
		// reset id to simulate an invalid role
		testRole.setId(null);
		List<Profile> resultList = this.profileService
				.findAllProfilesByRole(testRole);
		assertNotNull("list of profiles should not be null", resultList);
		assertTrue("list of profiles should be empty", resultList.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findByEmail(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindByEmail() {
		Profile profile = profileService.findByEmail(EMAIL_AGENT);

		assertNotNull("result profile should not be null", profile);

		// test if there is the profile is correct
		assertEquals("result profile should be equals to " + agent, agent,
				profile);

		employee.setEmail(EMAIL_AGENT);
		employeeProfileService.update(employee);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findByEmail(java.lang.String)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorFindByEmail() {

		employee.setEmail(EMAIL_AGENT);
		employeeProfileService.update(employee);

		profileService.findByEmail(EMAIL_AGENT);

		// test with two profiles with the same email
		profileService.findByEmail(EMAIL_AGENT);

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findByEmail(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindByEmail() {
		this.profileService.findByEmail(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#emailAlreadyExists(java.lang.String, org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test
	public void testEmailAlreadyExists() {
		Boolean exists = profileService.emailAlreadyExists(EMAIL_AGENT, agent);

		// only the current agent hold this email
		assertFalse("email should not exist", exists);

		exists = profileService.emailAlreadyExists(EMAIL_AGENT, employee);

		// email is already hold by another profile (agent)
		assertTrue("email should already exist", exists);

		employee.setEmail(EMAIL_AGENT);
		employeeProfileService.update(employee);

		profileService.emailAlreadyExists(EMAIL_AGENT, agent);

		// email is already hold by the employee AND the agent
		// FIXME : should be a normal case ?
		assertTrue("email should already exist", exists);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#emailAlreadyExists(java.lang.String, org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsEmail() {
		this.profileService.emailAlreadyExists(null, employee);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#emailAlreadyExists(java.lang.String, org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsProfile() {
		this.profileService.emailAlreadyExists(EMAIL_AGENT, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSofDelete() {
		this.profileService.softDelete(agent);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSofDeleteId() {
		this.profileService.softDelete(0L);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#emailAlreadyExists(java.lang.String, org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsBoth() {
		this.profileService.emailAlreadyExists(null, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByProfileType(org.myec3.socle.core.domain.model.meta.ProfileType)}
	 * .
	 */
	@Test
	public void testFindAllProfilesByProfileType() {

		ProfileType agentType = profileTypeService
				.findByValue(ProfileTypeValue.AGENT);
		ProfileType employeeType = profileTypeService
				.findByValue(ProfileTypeValue.EMPLOYEE);

		List<Profile> resultList = profileService
				.findAllProfilesByProfileType(agentType);

		assertNotNull("result profiles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result profiles list should not be empty",
				resultList.isEmpty());

		// test if there at least one result
		assertTrue("result profiles list should contain exactly two result",
				resultList.size() >= 1);

		// test if the class is correct
		assertTrue("profile should be an Agent", resultList.get(0).getClass()
				.equals(AgentProfile.class));

		resultList = profileService.findAllProfilesByProfileType(employeeType);

		assertNotNull("result profiles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result profiles list should not be empty",
				resultList.isEmpty());

		// test if there at least one result
		assertTrue("result profiles list should contain exactly two result",
				resultList.size() >= 1);

		// test if the class is correct
		assertTrue("profile should be an employee", resultList.get(0)
				.getClass().equals(EmployeeProfile.class));
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.ProfileServiceImpl#findAllProfilesByProfileType(org.myec3.socle.core.domain.model.meta.ProfileType)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllProfilesByProfileType() {
		this.profileService.findAllProfilesByProfileType(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllByEmail() throws Exception {
		profileService.findAllByEmail(null);
	}

	@Test
	public void testFindAllByEmail() throws Exception {
		List<Profile> profileList = profileService.findAllByEmail(employee
				.getEmail());

		// check if there is at least one result
		assertNotNull("List of employee profiles should not be null",
				profileList);
		assertFalse("List of employee profiles should not be empty",
				profileList.isEmpty());
		assertEquals(
				"List of employee profiles should contain at least one element",
				true, profileList.size() >= 1);
		assertNotNull(
				"List of employee profiles should contain at least one not null element",
				profileList.get(0));
		assertTrue(
				"List of employee profiles should contain at least one EmployeeProfile element",
				profileList.get(0).getClass().equals(EmployeeProfile.class));
		assertTrue(
				"List of employee profiles should contain at least one not EmployeeProfile element with email"
						+ employee.getEmail(), profileList.get(0).getEmail()
						.equals(employee.getEmail()));
	}

}
