package org.myec3.socle.core.service;

import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for
 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl}
 * 
*
 */
@Transactional
public class EmployeeProfileServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private EmployeeProfileService employeeProfileService;

	@Autowired

	private CompanyDepartmentService companyDepartmentService;

	@Autowired

	private UserService userService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ProfileTypeService profileTypeService;

	@Autowired
	private AcronymsListService acronymsListService;

	private EmployeeProfile employee1;
	private User user1;
	private Company company;
	private Person person;
	private Address address1;
	private Application application1;
	private Role role1;

	private static final String COMPANY_NAME = "Test Company Name"
			+ Calendar.getInstance().getTimeInMillis();
	private static final String PERSON_NAME_1 = "person1name"
			+ Calendar.getInstance().getTimeInMillis();
	private static final String USER_NAME = "employee1username"
			+ Calendar.getInstance().getTimeInMillis();
	private static String USER_FINAL_NAME;
	private static final String EMPLOYEE_NAME_1 = "employee1 name";
	private static String EMPLOYEE_FINAL_NAME_1;
	private static final String APPLICATION_NAME = "Gestion documentaire "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ROLE_NAME_1 = "Administrateur "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String EMAIL_EMPLOYEE_1 = "employee1@test.com";

	private static final String ACRONYM = "q7q";

	@Before
	public void setUp() {

		// create Acronym
		AcronymsList acronym0 = new AcronymsList();
		acronym0.setValue(ACRONYM);
		acronym0.setAvailable(true);
		acronymsListService.create(acronym0);

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

		address1 = new Address();
		address1.setPostalAddress("107 -108 boulevard Vivier Merle");
		address1.setPostalCode("69003");
		address1.setCanton("Rhône");
		address1.setCity("Lyon");
		address1.setCountry(Country.FR);

		person = new Person();
		person.setName(PERSON_NAME_1);
		person.setType("PP");
		person.setCivility(Civility.MR);
		person.setFirstname("Georges");
		person.setLastname("Abitbol");

		company = new Company();
		company.setName(COMPANY_NAME);
		company.setAddress(address1);
		company.setDescription("Description.");
		company.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
				+ "@test.com");
		company.setEnabled(Boolean.TRUE);
		company.setPhone("+33(0)000000000");
		company.setFax("+33(0)000000000");
		company.setForeignIdentifier(null);
		company.setIconUrl("http://iconurl");
		company.setLogoUrl("http://logourl");
		company.setLegalCategory(CompanyINSEECat.SA);
		company.setWebsite("http://www.company.com");
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

		user1 = new User();
		user1.setName(USER_NAME);
		user1.setFirstname("Georges");
		user1.setLastname("Abitbol");
		user1.setCivility(Civility.MR);
		user1.setEnabled(Boolean.TRUE);
		user1.setPassword("password");
		user1.setUsername("username" + Calendar.getInstance().getTimeInMillis());
		userService.create(user1);
		USER_FINAL_NAME = user1.getName();

		// test user ceration
		user1 = userService.findByName(USER_FINAL_NAME);
		assertNotNull("User should not be null", user1);
		assertEquals("User Name should be equals to " + USER_FINAL_NAME,
				USER_FINAL_NAME, user1.getName());

		application1 = new Application();
		application1.setName(APPLICATION_NAME);
		application1.setUrl("http://urlged");

		applicationService.create(application1);

		// test application creation
		application1 = applicationService.findByName(APPLICATION_NAME);
		assertNotNull("Application should not be null", application1);
		assertEquals(
				"Application Name should be equals to " + APPLICATION_NAME,
				APPLICATION_NAME, application1.getName());

		role1 = new Role();
		role1.setName(ROLE_NAME_1);
		role1.setApplication(application1);

		roleService.create(role1);

		// test role creation
		role1 = roleService.findByName(ROLE_NAME_1);
		assertNotNull("Role should not be null", role1);
		assertEquals("Role Name should be equals to " + ROLE_NAME_1,
				ROLE_NAME_1, role1.getName());

		List<Role> listRole = new ArrayList<Role>();
		listRole.add(role1);

		employee1 = new EmployeeProfile();
		employee1.setName(EMPLOYEE_NAME_1);
		employee1.setAddress(address1);
		employee1.setEmail(EMAIL_EMPLOYEE_1);
		employee1.setEnabled(Boolean.TRUE);
		employee1.setPhone("+33(0)000000000");
		employee1.setFax("+33(0)000000000");
		employee1.setFunction("Responsable d'application");
		employee1.setPrefComMedia(PrefComMedia.EMAIL);
		employee1.setUser(user1);
		employee1.setCompanyDepartment(this.companyDepartmentService
				.findRootCompanyDepartmentByCompany(company));
		employee1.setRoles(listRole);
		employeeProfileService.create(employee1);

		EMPLOYEE_FINAL_NAME_1 = employee1.getName();

		// test employee creation
		employee1 = employeeProfileService.findByName(EMPLOYEE_FINAL_NAME_1);
		assertNotNull("Employee should not be null", employee1);
		assertEquals("Employee Name should be equals to "
				+ EMPLOYEE_FINAL_NAME_1, EMPLOYEE_FINAL_NAME_1,
				employee1.getName());

		List<EmployeeProfile> employeeList = new ArrayList<EmployeeProfile>();
		employeeList.add(employee1);
	}

	@Test
	public void testBooleanMethods() {
		assertTrue("isEmployee should return true",
				((EmployeeProfile) employee1).isEmployee());
		assertFalse("isAgent should return false",
				((EmployeeProfile) employee1).isAgent());
		assertFalse("isAdmin should return false",
				((EmployeeProfile) employee1).isAdmin());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#create(org.myec3.socle.core.domain.model.EmployeeProfile)}
	 * .
	 */
	@Test(expected = ProfileCreationException.class)
	public void testInvalidCreate() {
		EmployeeProfile testEmployee = null;
		try {
			testEmployee = (EmployeeProfile) employee1.clone();
		} catch (CloneNotSupportedException e1) {
			fail("Cannot clone Agent");
		}
		// reset id to simulate new object
		testEmployee.setId(null);
		// test to create an invalid agent
		testEmployee.setEmail(null);
		this.employeeProfileService.create(testEmployee);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#create(org.myec3.socle.core.domain.model.EmployeeProfile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullCreate() {
		this.employeeProfileService.create(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#create(org.myec3.socle.core.domain.model.EmployeeProfile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDetachedCreate() {
		this.employeeProfileService.create(employee1);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#update(org.myec3.socle.core.domain.model.EmployeeProfile)}
	 * .
	 */
	@Test(expected = ProfileUpdateException.class)
	public void testInvalidUpdate() {
		// test to create an invalid agent
		employee1.setEmail(null);
		this.employeeProfileService.update(employee1);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#update(org.myec3.socle.core.domain.model.EmployeeProfile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullUpdate() {
		this.employeeProfileService.update(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullSoftDeleteId() {
		this.employeeProfileService.softDelete(Long.valueOf(null));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullSoftDelete() {
		EmployeeProfile employee = null;
		this.employeeProfileService.softDelete(employee);
	}

	@Test(expected = ProfileDeleteException.class)
	public void testInvalidSoftDeleteId() {
		this.employeeProfileService.softDelete(0L);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullSoftDeleteProfile() {
		EmployeeProfile testEmployee = null;
		this.employeeProfileService.softDelete(testEmployee);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullSoftDeleteProfileId() {
		EmployeeProfile testEmployee = null;
		try {
			testEmployee = (EmployeeProfile) employee1.clone();
		} catch (CloneNotSupportedException e1) {
			fail("Cannot clone Emmployee");
		}
		// reset id to simulate new object
		testEmployee.setId(null);

		this.employeeProfileService.softDelete(testEmployee);
	}


	@Test(expected = ProfileDeleteException.class)
	public void testInvalidSoftDeleteProfile() {
		EmployeeProfile testEmployee = null;
		try {
			testEmployee = (EmployeeProfile) employee1.clone();
		} catch (CloneNotSupportedException e1) {
			fail("Cannot clone employee");
		}
		// reset id to simulate an invalid object
		testEmployee.setId(0L);

		this.employeeProfileService.softDelete(testEmployee);
	}

	@Test
	public void testSoftDelete() throws Exception {
		employeeProfileService.softDelete(employee1);
		EmployeeProfile resultEmployee = employeeProfileService
				.findOne(employee1.getId());

		// check if employee profile email has been correctly modified
		assertNotNull("Employee should not be null", resultEmployee);
		assertTrue(
				"Employee email should start by "
						+ employeeProfileService.getSoftDeletePrefix(),
				resultEmployee.getEmail().startsWith(
						employeeProfileService.getSoftDeletePrefix()));
		assertTrue(
				"Employee email should finish by "
						+ employeeProfileService.getSoftDeleteSuffix(),
				resultEmployee.getEmail().endsWith(
						employeeProfileService.getSoftDeleteSuffix()));

		// check if employee profile username has been correctly modified
		assertNotNull("Employee should not be null", resultEmployee.getUser());
		assertTrue(
				"Employee username should start by "
						+ employeeProfileService.getSoftDeletePrefix(),
				resultEmployee
						.getUser()
						.getUsername()
						.startsWith(
								employeeProfileService.getSoftDeletePrefix()));
		assertTrue(
				"Employee username should finish by "
						+ employeeProfileService.getSoftDeleteSuffix(),
				resultEmployee.getUser().getUsername()
						.endsWith(employeeProfileService.getSoftDeleteSuffix()));

		// check if employee profile email has been correctly disabled
		assertFalse("Employee should be disabled", resultEmployee.isEnabled());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSoftDelete() throws Exception {

		EmployeeProfile employee = new EmployeeProfile();
		employee.setExternalId(new Long(1234));
		employee.setName(EMPLOYEE_NAME_1);
		employee.setAddress(address1);
		employee.setEmail(EMAIL_EMPLOYEE_1);
		employee.setEnabled(Boolean.TRUE);
		employee.setPhone("+33(0)000000000");
		employee.setFax("+33(0)000000000");
		employee.setFunction("Responsable d'application");
		employee.setPrefComMedia(PrefComMedia.EMAIL);
		employee.setUser(user1);
		employee.setCompanyDepartment(this.companyDepartmentService
				.findRootCompanyDepartmentByCompany(company));

		// should raise an exception
		employeeProfileService.softDelete(employee);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllByEmail() throws Exception {
		employeeProfileService.findAllByEmail(null);
	}

	@Test
	public void testFindAllByEmail() throws Exception {
		List<EmployeeProfile> employeeProfileList = employeeProfileService
				.findAllByEmail(employee1.getEmail());

		// check if there is at least one result
		assertNotNull("List of employee profiles should not be null",
				employeeProfileList);
		assertFalse("List of employee profiles should not be empty",
				employeeProfileList.isEmpty());
		assertEquals(
				"List of employee profiles should contain at least one element",
				true, employeeProfileList.size() >= 1);
		assertNotNull(
				"List of employee profiles should contain at least one not null element",
				employeeProfileList.get(0));
		assertTrue(
				"List of employee profiles should contain at least one EmployeeProfile element",
				employeeProfileList.get(0).getClass()
						.equals(EmployeeProfile.class));
		assertTrue(
				"List of employee profiles should contain at least one not EmployeeProfile element with email"
						+ employee1.getEmail(), employeeProfileList.get(0)
						.getEmail().equals(employee1.getEmail()));
	}

	@Test
	public void testEmailAlreadyExists() throws Exception {
		Boolean result;

		// test if the only found email is the email of the current employee
		result = this.employeeProfileService.emailAlreadyExists(
				employee1.getEmail(), employee1);
		assertFalse("email " + employee1.getEmail()
				+ " should not already exist", result);
	}

	@Test
	public void testFindAllByCompanyDepartment() throws Exception {
		List<EmployeeProfile> resultList = this.employeeProfileService
				.findAllEmployeeProfilesByCompanyDepartment(this.companyDepartmentService
						.findRootCompanyDepartmentByCompany(company));

		assertNotNull("result profiles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result profiles list should not be empty",
				resultList.isEmpty());

		// test if there is exactly one result
		assertTrue("result profiles list should contain exactly one result",
				resultList.size() == 1);

		// test if the retrieved object is an employeeProfile object
		assertTrue(
				"result profiles list should contain an employeeProfile object",
				resultList.get(0).getClass().equals(EmployeeProfile.class));

		// test if the retrieved profile is correct
		assertTrue("result profiles list should contain the original profile",
				resultList.get(0).equals(employee1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllByCompanyDepartment() throws Exception {
		this.employeeProfileService
				.findAllEmployeeProfilesByCompanyDepartment(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test
	public void testFindAllProfilesByUser() {
		List<EmployeeProfile> resultList = employeeProfileService
				.findAllProfilesByUser(user1);

		assertNotNull("result profiles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result profiles list should not be empty",
				resultList.isEmpty());

		// test if there is exactly two result
		assertTrue("result profiles list should contain exactly one result",
				resultList.size() == 1);

		for (EmployeeProfile profile : resultList) {
			if (profile.isEmployee()) {
				assertEquals("profile name should be equals to "
						+ EMPLOYEE_FINAL_NAME_1, EMPLOYEE_FINAL_NAME_1,
						profile.getName());
				continue;
			}
			fail("Profile should be Employee");
		}

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllProfilesByUser() {
		this.employeeProfileService.findAllProfilesByUser(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
	 * .
	 */
	@Test
	public void testEmptyFindAllProfilesByUser() {
		User testUser = user1;
		// reset id to simulate an invalid user
		testUser.setId(null);
		List<EmployeeProfile> resultList = this.employeeProfileService
				.findAllProfilesByUser(testUser);
		assertNotNull("list of profiles should not be null", resultList);
		assertTrue("list of profiles should be empty", resultList.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test
	public void testFindAllProfilesByRole() {
		List<EmployeeProfile> resultList = employeeProfileService
				.findAllProfilesByRole(role1);

		assertNotNull("result roles list should not be null", resultList);

		// test if the list is not empty
		assertFalse("result roles list should not be empty",
				resultList.isEmpty());

		// test if there is exactly two result
		assertTrue("result roles list should contain exactly one result",
				resultList.size() == 1);

		for (EmployeeProfile profile : resultList) {
			if (profile.isEmployee()) {
				assertEquals("role name should be equals to "
						+ EMPLOYEE_FINAL_NAME_1, EMPLOYEE_FINAL_NAME_1,
						profile.getName());
				continue;
			}
			fail("EmployeeProfile should be Employee");
		}
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindAllProfilesByRole() {
		this.employeeProfileService.findAllProfilesByRole(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
	 * .
	 */
	@Test
	public void testEmptyFindAllProfilesByRole() {
		Role testRole = role1;
		// reset id to simulate an invalid role
		testRole.setId(null);
		List<EmployeeProfile> resultList = this.employeeProfileService
				.findAllProfilesByRole(testRole);
		assertNotNull("list of profiles should not be null", resultList);
		assertTrue("list of profiles should be empty", resultList.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findByEmail(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindByEmail() {
		EmployeeProfile profile = employeeProfileService
				.findByEmail(EMAIL_EMPLOYEE_1);

		assertNotNull("result profile should not be null", profile);
		// test if there is the profile is correct
		assertEquals("result profile should be equals to " + employee1,
				employee1, profile);

	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.EmployeeProfileServiceImpl#findByEmail(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullFindByEmail() {
		this.employeeProfileService.findByEmail(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsEmail() {
		this.employeeProfileService.emailAlreadyExists(null, employee1);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsProfile() {
		this.employeeProfileService.emailAlreadyExists(EMAIL_EMPLOYEE_1, null);
	}


	@Test
	public void testIdNullEmailAlreadyExistsProfile() {
		EmployeeProfile newEmployee = new EmployeeProfile();
		newEmployee.setExternalId(new Long(1234));
		newEmployee.setName("new" + EMPLOYEE_NAME_1);
		newEmployee.setAddress(address1);
		newEmployee.setEmail(EMAIL_EMPLOYEE_1);
		newEmployee.setEnabled(Boolean.TRUE);
		newEmployee.setPhone("+33(0)000000000");
		newEmployee.setFax("+33(0)000000000");
		newEmployee.setFunction("Responsable d'application");
		newEmployee.setPrefComMedia(PrefComMedia.EMAIL);
		newEmployee.setUser(user1);
		newEmployee.setCompanyDepartment(this.companyDepartmentService
				.findRootCompanyDepartmentByCompany(company));

		Boolean exists = this.employeeProfileService.emailAlreadyExists(
				newEmployee.getEmail(), newEmployee);

		// agent is not already created. email should already exist but no error
		// occured
		assertTrue("email should exist", exists);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testNullEmailAlreadyExistsBoth() {
		this.employeeProfileService.emailAlreadyExists(null, null);
	}

}
