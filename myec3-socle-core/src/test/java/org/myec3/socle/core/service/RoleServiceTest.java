/**
 * 
 */
package org.myec3.socle.core.service;

import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link org.myec3.socle.core.service.impl.RoleServiceImpl}
 * 
*
 * 
 */
@Transactional
public class RoleServiceTest extends AbstractDbSocleUnitTest {

	private static final String EMAIL_AGENT = "agent1"
			+ Calendar.getInstance().getTimeInMillis() + "@test.fr";

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
	private ProfileTypeService profileTypeService;

	@Autowired
	private ProfileTypeRoleService profileTypeRoleService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private StructureTypeService structureTypeService;

	@Autowired
	private AcronymsListService acronymsListService;

	private Application application;
	private Role roleAdmin;
	private Role roleBasic;
	private AgentProfile agent;
	private User user;
	private Organism organism;
	private ProfileTypeRole profileTypeRoleAdmin;
	private ProfileTypeRole profileTypeRoleBasic;
	private ProfileType profileType;
	private Address address;
	private Customer customer;

	private static final String ORGANISM_NAME = "Test Organism "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String USER_NAME = "bernard dupond";
	private static String USER_FINAL_NAME;
	private static final String APPLICATION_NAME = "Gestion documentaire "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ROLE_NAME_ADMIN = "Administrateur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ROLE_NAME_BASIC = "Contributeur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String AGENT_NAME = "Agent bernard dupond";
	private static String AGENT_FINAL_NAME;
	private static final String CUSTOMER_NAME = "Test Customer";

	private static final String ACRONYM = "a9z";

	@Before
	public void setUp() throws Exception {

		AcronymsList acronym0 = new AcronymsList();
		acronym0.setValue(ACRONYM);
		acronym0.setAvailable(true);
		acronymsListService.create(acronym0);

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

		address = new Address();
		address.setPostalAddress("17 boulevard de la Trémouille - BP 1602");
		address.setPostalCode("21035");
		address.setCanton("company");
		address.setCity("Dijon");
		address.setCountry(Country.FR);

		// Init customer
		customer = new Customer();
		customer.setName(CUSTOMER_NAME);
		customer.setLabel("label");
		customer.addApplication(application);
		customerService.create(customer);

		organism = new Organism();
		organism.setName(ORGANISM_NAME);
		organism.setLabel("OrganismLabel");
		organism.setDescription("OrganismDescription");
		organism.setExternalId(new Long(123));
		organism.setPhone("0000000000");
		organism.setArticle(Article.LE);
		organism.setMember(true);
		organism.setLegalCategory(OrganismINSEECat._4_1_10);
		organism.setEmail("organisme"
				+ Calendar.getInstance().getTimeInMillis() + "@company.fr");
		organism.setPhone("+33(0)000000000");
		organism.setEnabled(Boolean.TRUE);
		organism.setAddress(address);
		organism.setAcronym("a9z");
		organism.setCustomer(customer);

		organismService.create(organism);

		// test organism creation
		organism = organismService.findByName(ORGANISM_NAME);
		assertNotNull("Organism should not be null", organism);
		assertEquals("Organism Name should be equals to " + ORGANISM_NAME,
				ORGANISM_NAME, organism.getName());

		user = new User();
		user.setName(USER_NAME);
		user.setFirstname("bernard");
		user.setLastname("dupond");
		user.setCivility(Civility.MR);
		user.setEnabled(Boolean.TRUE);
		user.setPassword("password");
		user.setUsername("a209070 " + Calendar.getInstance().getTimeInMillis());
		userService.create(user);
		USER_FINAL_NAME = user.getName();

		// test user creation
		user = userService.findByName(user.getName());
		assertNotNull("User should not be null", user);
		assertEquals("User Name should be equals to " + USER_FINAL_NAME,
				USER_FINAL_NAME, user.getName());

		application = new Application();
		application.setName(APPLICATION_NAME);
		application.setExternalId(new Long(1246));
		application.setUrl("http://urlged");

		applicationService.create(application);

		// test application creation
		application = applicationService.findByName(APPLICATION_NAME);
		assertNotNull("Application should not be null", application);
		assertEquals(
				"Application Name should be equals to " + APPLICATION_NAME,
				APPLICATION_NAME, application.getName());

		roleAdmin = new Role();
		roleAdmin.setExternalId(new Long(1242));
		roleAdmin.setName(ROLE_NAME_ADMIN);
		roleAdmin.setApplication(application);

		roleService.create(roleAdmin);

		// test role creation
		roleAdmin = roleService.findByName(ROLE_NAME_ADMIN);
		assertNotNull("Role should not be null", roleAdmin);
		assertEquals("Role Name should be equals to " + ROLE_NAME_ADMIN,
				ROLE_NAME_ADMIN, roleAdmin.getName());

		profileType = profileTypeService.findByValue(ProfileTypeValue.AGENT);

		profileTypeRoleAdmin = new ProfileTypeRole();
		profileTypeRoleAdmin.setProfileType(profileType);
		profileTypeRoleAdmin.setDefaultAdmin(true);
		profileTypeRoleAdmin.setDefaultBasic(false);
		profileTypeRoleAdmin.setRole(roleAdmin);

		profileTypeRoleService.create(profileTypeRoleAdmin);

		roleBasic = new Role();
		roleBasic.setExternalId(new Long(1243));
		roleBasic.setName(ROLE_NAME_BASIC);
		roleBasic.setApplication(application);

		roleService.create(roleBasic);

		// test role creation
		roleBasic = roleService.findByName(ROLE_NAME_BASIC);
		assertNotNull("Role should not be null", roleBasic);
		assertEquals("Role Name should be equals to " + ROLE_NAME_BASIC,
				ROLE_NAME_BASIC, roleBasic.getName());

		profileTypeRoleBasic = new ProfileTypeRole();
		profileTypeRoleBasic.setProfileType(profileType);
		profileTypeRoleBasic.setDefaultBasic(true);
		profileTypeRoleBasic.setDefaultAdmin(false);
		profileTypeRoleBasic.setRole(roleBasic);

		profileTypeRoleService.create(profileTypeRoleBasic);

		List<Role> listRole = new ArrayList<Role>();
		listRole.add(roleAdmin);
		listRole.add(roleBasic);

		agent = new AgentProfile();
		agent.setExternalId(new Long(1240));
		agent.setName(AGENT_NAME);
		agent.setAddress(address);
		agent.setEmail(EMAIL_AGENT);
		agent.setEnabled(Boolean.TRUE);
		agent.setPhone("+33(0)000000000");
		agent.setFax("+33(0)000000000");
		agent.setElected(Boolean.FALSE);
		agent.setOrganismDepartment(this.organismDepartmentService
				.findRootOrganismDepartment(this.organism));
		agent.setUser(user);
		agent.setFunction("Agent responsable");
		agent.setPrefComMedia(PrefComMedia.EMAIL);
		agent.setRoles(listRole);

		agentProfileService.create(agent);
		AGENT_FINAL_NAME = agent.getName();
		// test Agent creation
		Resource foundResource = agentProfileService
				.findByName(AGENT_FINAL_NAME);
		assertNotNull("Agent should not be null", foundResource);
		assertEquals("Agent Name should be equals to " + AGENT_FINAL_NAME,
				AGENT_FINAL_NAME, foundResource.getName());

		// test if the retrieved object is an AgentProfile object
		assertTrue(
				"result profiles list should contain an AgentProfile object",
				foundResource.getClass().equals(AgentProfile.class));

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test
	public void testFindAllRoleByProfile() {
		List<Role> roles = this.roleService.findAllRoleByProfile(agent);

		assertNotNull("Roles should not be null", roles);
		assertFalse("Roles should not be empty", roles.isEmpty());
		assertEquals("Size of roles should be exactly 2", roles.size(), 2);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllRoleByProfile() {
		this.roleService.findAllRoleByProfile(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorEmptyFindAllRoleByProfile() {
		AgentProfile testAgent = agent;
		// reset id to simulate an invalid agent
		testAgent.setId(0L);
		this.roleService.findAllRoleByProfile(testAgent);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test
	public void testEmptyFindAllRoleByProfile() {
		AgentProfile testAgent = agent;
		testAgent.setRoles(new ArrayList<Role>());

		agentProfileService.update(testAgent);
		List<Role> roles = this.roleService.findAllRoleByProfile(testAgent);

		assertNotNull("Roles should not be null", roles);
		assertTrue("Roles should be empty", roles.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByApplication(org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testFindAllRoleByApplication() {
		List<Role> roles = this.roleService.findAllRoleByProfile(agent);

		assertNotNull("Roles should not be null", roles);
		assertFalse("Roles should not be empty", roles.isEmpty());
		assertEquals("Size of roles should be exactly 2", roles.size(), 2);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllRoleByApplication() {
		this.roleService.findAllRoleByApplication(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorEmptyFindAllRoleByApplication() {
		Application testApplication = application;
		// reset id to simulate an invalid application
		testApplication.setId(0L);
		this.roleService.findAllRoleByApplication(testApplication);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfile(org.myec3.socle.core.domain.model.Profile)}
	 * .
	 */
	@Test
	public void testEmptyFindAllRoleByApplication() {
		Application testApplication = new Application();
		testApplication.setName("Test Application "
				+ Calendar.getInstance().getTimeInMillis());
		testApplication.setExternalId(new Long(1246));
		testApplication.setUrl("http://urlged");

		applicationService.create(testApplication);

		List<Role> roles = this.roleService
				.findAllRoleByApplication(testApplication);

		assertNotNull("Roles should not be null", roles);
		assertTrue("Roles should be empty", roles.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByNullProfileTypeAndApplication() {
		roleService.findAdminRoleByProfileTypeAndApplication(null, application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByProfileTypeAndNullApplication() {
		roleService.findAdminRoleByProfileTypeAndApplication(
				profileTypeService.findByValue(ProfileTypeValue.AGENT), null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByNullProfileTypeAndNullApplication() {
		roleService.findAdminRoleByProfileTypeAndApplication(null, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testFindAllRoleByProfileTypeAndApplication() {
		List<Role> roles = this.roleService
				.findAllRoleByProfileTypeAndApplication(profileType,
						application);

		assertNotNull("Roles should not be null", roles);
		assertFalse("Roles should not be empty", roles.isEmpty());
		assertTrue("Size of roles should be greater than 1", roles.size() >= 1);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorFindAllRoleByProfileTypeAndApplication() {

		Application testApplication = application;
		// reset id to simulate an invalid application
		testApplication.setId(0L);

		this.roleService.findAllRoleByProfileTypeAndApplication(profileType,
				application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testEmptyFindAllRoleByProfileTypeAndApplication() {
		ProfileType profileType = profileTypeService
				.findByValue(ProfileTypeValue.AGENT);

		Application testApplication = new Application();
		testApplication.setName("Test Application "
				+ Calendar.getInstance().getTimeInMillis());
		testApplication.setExternalId(new Long(1246));
		testApplication.setUrl("http://urlged");

		applicationService.create(testApplication);

		List<Role> roles = this.roleService
				.findAllRoleByProfileTypeAndApplication(profileType,
						testApplication);

		assertNotNull("Roles should not be null", roles);
		assertTrue("Roles should be empty", roles.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAdminRoleByNullProfileTypeAndApplication() {
		this.roleService.findAdminRoleByProfileTypeAndApplication(null,
				application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAdminRoleByProfileTypeAndNullApplication() {
		this.roleService.findAdminRoleByProfileTypeAndApplication(profileType,
				null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAdminRoleByNullProfileTypeAndNullApplication() {
		this.roleService.findAdminRoleByProfileTypeAndApplication(null, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testEmptyFindAdminRoleByProfileTypeAndApplication() {
		ProfileType profileType = profileTypeService
				.findByValue(ProfileTypeValue.AGENT);

		Application testApplication = new Application();
		testApplication.setName("Test Application "
				+ Calendar.getInstance().getTimeInMillis());
		testApplication.setExternalId(new Long(1246));
		testApplication.setUrl("http://urlged");

		applicationService.create(testApplication);

		Role role = this.roleService.findAdminRoleByProfileTypeAndApplication(
				profileType, testApplication);

		assertNull("Role should be null", role);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorFindAdminRoleByProfileTypeAndApplication() {
		Application testApplication = application;
		// reset id to simulate an invalid application
		testApplication.setId(0L);

		this.roleService.findAdminRoleByProfileTypeAndApplication(profileType,
				testApplication);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAdminRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testFindAdminRoleByProfileTypeAndApplication() {
		Role role = this.roleService.findAdminRoleByProfileTypeAndApplication(
				profileType, application);

		assertNotNull("Role should not be null", role);
		assertEquals("role name should be " + ROLE_NAME_ADMIN, ROLE_NAME_ADMIN,
				role.getName());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindBasicRoleByNullProfileTypeAndApplication() {
		roleService.findBasicRoleByProfileTypeAndApplication(null, application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindBasicRoleByProfileTypeAndNullApplication() {
		roleService.findBasicRoleByProfileTypeAndApplication(profileType, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindBasicRoleByNullProfileTypeAndNullApplication() {
		roleService.findBasicRoleByProfileTypeAndApplication(null, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorFindBasicRoleByProfileTypeAndApplication() {
		Application testApplication = application;
		// reset id to simulate an invalid application
		testApplication.setId(0L);

		roleService.findBasicRoleByProfileTypeAndApplication(profileType,
				application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testEmptyFindBasicRoleByProfileTypeAndApplication() {
		ProfileType profileType = profileTypeService
				.findByValue(ProfileTypeValue.AGENT);

		Application testApplication = new Application();
		testApplication.setName("Test Application "
				+ Calendar.getInstance().getTimeInMillis());
		testApplication.setExternalId(new Long(1246));
		testApplication.setUrl("http://urlged");

		applicationService.create(testApplication);

		Role role = this.roleService.findBasicRoleByProfileTypeAndApplication(
				profileType, testApplication);

		assertNull("Roles should be null", role);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findBasicRoleByProfileTypeAndApplication(org.myec3.socle.core.domain.model.meta.ProfileType, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testFindBasicRoleByProfileTypeAndApplication() {
		Role role = this.roleService.findBasicRoleByProfileTypeAndApplication(
				profileType, application);

		assertNotNull("Role should not be null", role);
		assertEquals("role name should be " + ROLE_NAME_BASIC, ROLE_NAME_BASIC,
				role.getName());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByNullProfileAndApplication() {
		this.roleService.findAllRoleByProfileAndApplication(null, application);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByProfileAndNullApplication() {
		this.roleService.findAllRoleByProfileAndApplication(agent, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindAllRoleByNullProfileAndNullApplication() {
		this.roleService.findAllRoleByProfileAndApplication(null, null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test(expected = RuntimeException.class)
	public void testErrorFindAllRoleByProfileAndApplication() {
		Application testApplication = application;
		// reset id to simulate an invalid application
		testApplication.setId(0L);

		this.roleService.findAllRoleByProfileAndApplication(agent,
				testApplication);

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testEmptyFindAllRoleByProfileAndApplication() {
		Application testApplication = new Application();
		testApplication.setName("Test Application "
				+ Calendar.getInstance().getTimeInMillis());
		testApplication.setExternalId(new Long(1246));
		testApplication.setUrl("http://urlged");

		applicationService.create(testApplication);

		List<Role> roles = this.roleService.findAllRoleByProfileAndApplication(
				agent, testApplication);

		assertNotNull("Roles should not be null", roles);
		assertTrue("Roles should be empty", roles.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.RoleService#findAllRoleByProfileAndApplication(org.myec3.socle.core.domain.model.Profile, org.myec3.socle.core.domain.model.Application)}
	 * .
	 */
	@Test
	public void testFindAllRoleByProfileAndApplication() {
		List<Role> roles = this.roleService.findAllRoleByProfileAndApplication(
				agent, application);

		assertNotNull("Roles should not be null", roles);
		assertFalse("Roles should not be empty", roles.isEmpty());
		assertEquals("Size of roles should be exactly 2", roles.size(), 2);
	}

}
