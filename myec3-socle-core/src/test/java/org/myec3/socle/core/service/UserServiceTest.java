package org.myec3.socle.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link org.myec3.socle.core.service.impl.UserServiceImpl}
 * 
*
 */
@Transactional
public class UserServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private OrganismService organismService;

	@Autowired
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AcronymsListService acronymsListService;

	@Autowired
	private StructureTypeService structureTypeService;

	private User user;

	public static final String ACRONYM = "p6p";
	public static final String ACRONYM_2 = "g4g";
	private static final String USER_NAME_1 = "User 1";
	private static final String USER_NAME_2 = "User 2";
	private static final String USER_NAME_3 = "USer 3";
	private static final String AGENT_NAME = "Agent 1";
	private static final String ROLE_NAME = "Administrateur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String APP_NAME = "GU "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_NAME = "Test Organism "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_DEPARTMENT_NAME = "Organisme de la région "
			+ Calendar.getInstance().getTimeInMillis();

	@Before
	public void setUp() {

		// create Acronym
		AcronymsList acronym0 = new AcronymsList();
		acronym0.setValue(ACRONYM);
		acronym0.setAvailable(true);
		acronymsListService.create(acronym0);

		AcronymsList acronym2 = new AcronymsList();
		acronym2.setValue(ACRONYM_2);
		acronym2.setAvailable(true);
		acronymsListService.create(acronym2);

		user = new User(USER_NAME_1 + "FirstName", USER_NAME_1 + "TestLastName");
		user.setName(USER_NAME_1);
		user.setExternalId(123L);
		user.setCivility(Civility.MR);
		user.setUsername("username" + Calendar.getInstance().getTimeInMillis());

		userService.create(user);
	}

	@After
	public void tearDown() {
		if (null != this.user) {
			userService.delete(this.user);
		}
	}

	@Test
	public void testPersist() throws Exception {

		User newUser = new User(USER_NAME_2 + "FirstName", USER_NAME_2
				+ "LastName");
		newUser.setExternalId(123L);
		newUser.setCivility(Civility.MR);
		newUser.setUsername("username2"
				+ Calendar.getInstance().getTimeInMillis());

		userService.create(newUser);

		Resource foundResource = userService.findByName(newUser.getName());
		assertNotNull(foundResource);
		assertEquals(newUser.getName(), foundResource.getName());

		userService.delete(newUser);
		foundResource = userService.findByName(newUser.getName());
		assertNull(foundResource);
	}

	@Test
	public void testPersistWithProfileAndRole() throws Exception {

		Application application = new Application(APP_NAME, APP_NAME + "Label");
		application.setUrl("http://url");
		applicationService.create(application);

		OrganismDepartment organismDepartment = new OrganismDepartment();

		organismDepartment.setName(ORGANISM_DEPARTMENT_NAME);
		organismDepartment.setLabel("OrganismDptLabel");

		// Init customer
		Customer customer = new Customer();
		customer.setName("CUSTOMER");
		customer.setLabel("label");
		customerService.create(customer);

		Address address = new Address();
		address.setCity("city");
		address.setPostalAddress("");
		address.setPostalCode("00000");
		address.setCountry(Country.FR);

		organismDepartment.setAcronym("p6p");
		organismDepartment.setDescription("");
		organismDepartment.setEmail("test-organism-dpt"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organismDepartment.setPhone("");
		organismDepartment.setAddress(address);
		organismDepartment.setParentDepartment(null);

		Organism newOrganism = new Organism();

		newOrganism.setName(ORGANISM_NAME);
		newOrganism.setLabel("OrganismLabel");
		newOrganism.setAcronym("c5r");
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(123L);
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		structureTypeService.create(structureType);
		StructureType structureTypeCreated = structureTypeService.findByValue(StructureTypeValue.ORGANISM);

		newOrganism.setStructureType(structureTypeCreated);

		newOrganism.setMember(true);
		newOrganism.setAcronym("g4g");
		newOrganism.setAddress(address);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);
		newOrganism.setCustomer(customer);

		organismDepartment.setOrganism(newOrganism);

		organismService.create(newOrganism);
		organismDepartmentService.create(organismDepartment);

		User newUser = new User(USER_NAME_3 + "FirstName", USER_NAME_3
				+ "LastName");
		newUser.setUsername("username3"
				+ Calendar.getInstance().getTimeInMillis());
		userService.create(newUser);

		AgentProfile userAgentProfile = new AgentProfile();
		userAgentProfile
				.setLabel("Profil : TestPersistFirstName TestPersistLastName");
		userAgentProfile.setName(AGENT_NAME);
		userAgentProfile.setEmail("test"
				+ Calendar.getInstance().getTimeInMillis() + "@test.com");
		userAgentProfile.setEnabled(Boolean.TRUE);
		userAgentProfile.setUser(newUser);
		userAgentProfile.setElected(false);
		userAgentProfile.setOrganismDepartment(organismDepartment);

		userAgentProfile.setAddress(address);

		Role role = new Role();
		role.setLabel("Role Agent Responsable sur GU");
		role.setName(ROLE_NAME);

		// set application gu
		role.setApplication(this.applicationService.findByName(APP_NAME));
		userAgentProfile.addRole(role);

		user.addProfile(userAgentProfile);

		userService.update(newUser);

		Resource foundResource = userService.findByName(newUser.getName());
		assertNotNull(foundResource);
		assertEquals(newUser.getName(), foundResource.getName());

		userService.delete(newUser);
		foundResource = userService.findByName(newUser.getName());
		assertNull(foundResource);

		organismDepartmentService.delete(organismDepartment);
		foundResource = organismDepartmentService.findByName(organismDepartment
				.getName());
		assertNull(foundResource);

		organismDepartmentService.delete(organismDepartmentService
				.findRootOrganismDepartment(newOrganism));
		organismService.delete(newOrganism);
		foundResource = organismService.findByName(newOrganism.getName());
		assertNull(foundResource);

		customerService.delete(customer);
		foundResource = customerService.findByName(customer.getName());
		assertNull(foundResource);

		applicationService.delete(application);
		foundResource = applicationService.findByName(application.getName());
		assertNull(foundResource);
	}

	@Test
	public void testMerge() throws Exception {
		User foundUser = userService.findByName(this.user.getName());
		foundUser.setName("Modified Test User");
		userService.update(foundUser);

		Resource mergedUser = userService.findByName(foundUser.getName());
		assertNotNull(mergedUser);
		assertEquals(foundUser.getName(), mergedUser.getName());
	}

	@Test
	public void testRemove() throws Exception {
		User foundUser = userService.findByName(this.user.getName());
		userService.delete(foundUser);

		this.user = userService.findByName(foundUser.getName());
		assertNull(this.user);
	}

	@Test
	public void testRemoveById() throws Exception {
		User foundUser = userService.findByName(this.user.getName());
		userService.delete(foundUser);

		this.user = userService.findByName(foundUser.getName());
		assertNull(this.user);
	}

	@Test
	public void testFindAll() throws Exception {
		List<User> userList = userService.findAll();
		assertEquals(true, userList.size() >= 1);
	}

	@Test
	public void testFindByName() throws Exception {
		User foundUser = userService.findByName(this.user.getName());
		assertNotNull(foundUser);
		assertEquals(this.user.getName(), foundUser.getName());
	}

}
