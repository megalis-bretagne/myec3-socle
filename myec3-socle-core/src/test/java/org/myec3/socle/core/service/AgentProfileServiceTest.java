package org.myec3.socle.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Transactional
public class AgentProfileServiceTest extends AbstractDbSocleUnitTest {

    private static final String EMAIL_AGENT = "agent.email"
            + Calendar.getInstance().getTimeInMillis() + "@company.fr";

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
    private CustomerService customerService;

    @Autowired
    private AcronymsListService acronymsListService;

    @Autowired
    private StructureTypeService structureTypeService;

    @Autowired
    private ProfileTypeService profileTypeService;

    private Application application1;
    private Role role1;
    private Role role2;
    private AgentProfile agent;
    private User user;
    private Organism organism;
    private Address address;
    private Customer customer;

    private static final String ORGANISM_NAME = "Test Organism";
    private static String ORGANISM_FINAL_NAME;
    private static final String USER_NAME = "User";
    private static String USER_FINAL_NAME;
    private static final String APPLICATION_NAME = "Gestion documentaire "
            + Calendar.getInstance().getTimeInMillis();
    private static final String ROLE_NAME_1 = "Administrateur "
            + Calendar.getInstance().getTimeInMillis();
    private static final String ROLE_NAME_2 = "Contributeur "
            + Calendar.getInstance().getTimeInMillis();
    private static final String AGENT_NAME = "Agent";
    private static final String CUSTOMER_NAME = "Test Customer Agent profile service test";

    private static String AGENT_FINAL_NAME;

    private static final String ACRONYM = "a9z";

    @Before
    public void setUp() {

        // create Acronym
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

        // Create application
        application1 = new Application();
        application1.setName(APPLICATION_NAME);
        application1.setUrl("http://urlged");
        applicationService.create(application1);

        // test application creation
        application1 = applicationService.findByName(APPLICATION_NAME);
        Assert.assertNotNull("Application should not be null", application1);
        Assert.assertEquals(
                "Application Name should be equals to " + APPLICATION_NAME,
                APPLICATION_NAME, application1.getName());

        // Create customer
        customer = new Customer();
        customer.setName(CUSTOMER_NAME);
        customer.setLabel("label");
        customer.addApplication(application1);
        customerService.create(customer);

        // test customer creation
        customer = customerService.findByName(CUSTOMER_NAME);
        Assert.assertNotNull("Customer should not be null", customer);
        Assert.assertEquals("Customer Name should be equals to " + CUSTOMER_NAME,
                CUSTOMER_NAME, customer.getName());

        address = new Address();
        address.setPostalAddress("17 boulevard de la Trémouille - BP 1602");
        address.setPostalCode("21035");
        address.setCanton("Bourgogne");
        address.setCity("Dijon");
        address.setCountry(Country.FR);

        organism = new Organism();
        organism.setName(ORGANISM_NAME);
        organism.setLabel("OrganismLabel");
        organism.setDescription("OrganismDescription");
        organism.setExternalId(123L);
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
        //organism.setTenantIdentifier(".megalis");

        organismService.create(organism);
        ORGANISM_FINAL_NAME = organism.getName();

        // test organism creation
        organism = organismService.findByName(ORGANISM_FINAL_NAME);
        Assert.assertNotNull("Organism should not be null", organism);
        Assert.assertEquals("Organism Name should be equals to " + ORGANISM_NAME,
                ORGANISM_NAME, organism.getName());

        user = new User();
        user.setName(USER_NAME);
        user.setFirstname("Bernard");
        user.setLastname("Dupont");
        user.setCivility(Civility.MR);
        user.setEnabled(Boolean.TRUE);
        user.setPassword("password");
        user.setUsername("a88888 " + Calendar.getInstance().getTimeInMillis());
        userService.create(user);
        USER_FINAL_NAME = user.getName();

        // test user creation
        user = userService.findByUsername(user.getUsername());
        Assert.assertNotNull("User should not be null", user);
        Assert.assertEquals("User Name should be equals to " + USER_FINAL_NAME,
                USER_FINAL_NAME, user.getName());

        // Create role
        role1 = new Role();
        role1.setName(ROLE_NAME_1);
        role1.setApplication(application1);

        roleService.create(role1);

        // test role creation
        role1 = roleService.findByName(ROLE_NAME_1);
        Assert.assertNotNull("Role should not be null", role1);
        Assert.assertEquals("Role Name should be equals to " + ROLE_NAME_1,
                ROLE_NAME_1, role1.getName());

        role2 = new Role();
        role2.setName(ROLE_NAME_2);
        role2.setApplication(application1);

        roleService.create(role2);

        // test role creation
        role2 = roleService.findByName(ROLE_NAME_2);
        Assert.assertNotNull("Role should not be null", role2);
        Assert.assertEquals("Role Name should be equals to " + ROLE_NAME_2,
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
        Assert.assertNotNull("Agent should not be null", foundResource);
        Assert.assertEquals("Agent Name should be equals to " + AGENT_FINAL_NAME,
                AGENT_FINAL_NAME, foundResource.getName());

        // test if the retrieved object is an AgentProfile object
        Assert.assertTrue(
                "result profiles list should contain an AgentProfile object",
                foundResource.getClass().equals(AgentProfile.class));

    }

    @Test
    public void testBooleanMethods() {
        Assert.assertTrue("isAgent should return true",
                ((AgentProfile) agent).isAgent());
        Assert.assertFalse("isEmployee should return false",
                ((AgentProfile) agent).isEmployee());
        Assert.assertFalse("isAdmin should return false",
                ((AgentProfile) agent).isAdmin());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#create(org.myec3.socle.core.domain.model.AgentProfile)}
     * .
     */
    @Test(expected = ProfileCreationException.class)
    public void testInvalidCreate() {
        AgentProfile testAgent = null;
        try {
            testAgent = (AgentProfile) agent.clone();
        } catch (CloneNotSupportedException e1) {
            Assert.fail("Cannot clone Agent");
        }
        // reset id to simulate new object
        testAgent.setId(null);
        // test to create an invalid agent
        testAgent.setEmail(null);
        this.agentProfileService.create(testAgent);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#create(org.myec3.socle.core.domain.model.AgentProfile)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullCreate() {
        this.agentProfileService.create(null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#create(org.myec3.socle.core.domain.model.AgentProfile)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDetachedCreate() {
        this.agentProfileService.create(agent);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#update(org.myec3.socle.core.domain.model.AgentProfile)}
     * .
     */
    @Test(expected = ProfileUpdateException.class)
    public void testInvalidUpdate() {
        // test to create an invalid agent
        agent.setEmail(null);
        this.agentProfileService.update(agent);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#update(org.myec3.socle.core.domain.model.AgentProfile)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullUpdate() {
        this.agentProfileService.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSoftDeleteId() {
        this.agentProfileService.softDelete(Long.valueOf(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSoftDelete() {
        AgentProfile agent = null;
        this.agentProfileService.softDelete(agent);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#softDelete(Long)}
     * .
     */
    @Test(expected = ProfileDeleteException.class)
    public void testInvalidSoftDeleteId() {
        this.agentProfileService.softDelete(0L);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullSoftDeleteProfile() {
        AgentProfile testAgent = null;
        this.agentProfileService.softDelete(testAgent);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullSoftDeleteProfileId() {
        AgentProfile testAgent = null;
        try {
            testAgent = (AgentProfile) agent.clone();
        } catch (CloneNotSupportedException e1) {
            Assert.fail("Cannot clone Agent");
        }
        // reset id to simulate new object
        testAgent.setId(null);

        this.agentProfileService.softDelete(testAgent);
    }


    @Test(expected = ProfileDeleteException.class)
    public void testInvalidSoftDeleteProfile() {
        AgentProfile testAgent = null;
        try {
            testAgent = (AgentProfile) agent.clone();
        } catch (CloneNotSupportedException e1) {
            Assert.fail("Cannot clone Agent");
        }
        // reset id to simulate an invalid object
        testAgent.setId(0L);

        this.agentProfileService.softDelete(testAgent);
    }

    @Test
    public void testFindAllRoleByProfile() throws Exception {
        List<Role> roleList = roleService.findAllRoleByProfile(agent);

        Assert.assertNotNull("result roles list should not be null", roleList);

        // test if the list is not empty
        Assert.assertFalse("result roles list should not be empty", roleList.isEmpty());

        // test if there is exactly one result
        Assert.assertTrue("result roles list should contain exactly two result",
                roleList.size() == 2);

    }

    @Test
    public void testSoftDelete() throws Exception {
        agentProfileService.softDelete(agent);
        AgentProfile resultAgent = agentProfileService.findOne(agent.getId());

        // test if email has been correctly changed
        Assert.assertNotNull("Agent should not be null", resultAgent);
        Assert.assertTrue(
                "Agent email should start by "
                        + agentProfileService.getSoftDeletePrefix(),
                resultAgent.getEmail().startsWith(
                        agentProfileService.getSoftDeletePrefix()));
        Assert.assertTrue(
                "Agent email should finish by "
                        + agentProfileService.getSoftDeleteSuffix(),
                resultAgent.getEmail().endsWith(
                        agentProfileService.getSoftDeleteSuffix()));

        // test if the user email has been changed
        Assert.assertNotNull("Agent user should not be null",
                resultAgent.getUser());
        Assert.assertTrue(
                "Agent username should start by "
                        + agentProfileService.getSoftDeletePrefix(),
                resultAgent.getUser().getUsername()
                        .startsWith(agentProfileService.getSoftDeletePrefix()));
        Assert.assertTrue(
                "Agent username should finish by "
                        + agentProfileService.getSoftDeleteSuffix(),
                resultAgent.getUser().getUsername()
                        .endsWith(agentProfileService.getSoftDeleteSuffix()));

        // test if the profile has been deactivated
        Assert.assertFalse("Employee should be disabled",
                resultAgent.isEnabled());

        // test if the organsim department has been correctly changed : should
        // be the Root organism department
        OrganismDepartment departmentRoot = this.organismDepartmentService
                .findRootOrganismDepartment(resultAgent.getOrganismDepartment()
                        .getOrganism());
        Assert.assertNotNull("Agent department should not be null", departmentRoot);
        Assert.assertEquals("Agent department should be the root department",
                departmentRoot, resultAgent.getOrganismDepartment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFindAllByEmail() throws Exception {
        agentProfileService.findAllByEmail(null);
    }

    @Test
    public void testFindAllByEmail() throws Exception {
        List<AgentProfile> employeeProfileList = agentProfileService
                .findAllByEmail(agent.getEmail());

        // check if there is at least one result
        Assert.assertNotNull("List of agent profiles should not be null",
                employeeProfileList);
        Assert.assertFalse("List of agent profiles should not be empty",
                employeeProfileList.isEmpty());
        Assert.assertEquals(
                "List of agent profiles should contain at least one element",
                true, employeeProfileList.size() >= 1);
        Assert.assertNotNull(
                "List of agent profiles should contain at least one not null element",
                employeeProfileList.get(0));
        Assert.assertTrue(
                "List of agent profiles should contain at least one AgentProfile element",
                employeeProfileList.get(0).getClass()
                        .equals(AgentProfile.class));
        Assert.assertTrue(
                "List of agent profiles should contain at least one not AgentProfile element with email"
                        + agent.getEmail(), employeeProfileList.get(0)
                        .getEmail().equals(agent.getEmail()));
    }

    @Test
    public void testFindAllAgentProfilesByOrganismDepartment() throws Exception {
        List<AgentProfile> resultList = this.agentProfileService
                .findAllAgentProfilesByOrganismDepartment(this.organismDepartmentService
                        .findRootOrganismDepartment(this.organism));

        Assert.assertNotNull("result profiles list should not be null", resultList);

        // test if the list is not empty
        Assert.assertFalse("result profiles list should not be empty",
                resultList.isEmpty());

        // test if there is exactly one result
        Assert.assertTrue("result profiles list should contain exactly one result",
                resultList.size() == 1);

        // test if the retrieved object is an AgentProfile object
        Assert.assertTrue(
                "result profiles list should contain an AgentProfile object",
                resultList.get(0).getClass().equals(AgentProfile.class));

        // test if the retrieved profile is correct
        Assert.assertTrue("result profiles list should contain the original profile",
                resultList.get(0).equals(agent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFindAllAgentProfilesByOrganismDepartment()
            throws Exception {
        this.agentProfileService.findAllAgentProfilesByOrganismDepartment(null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
     * .
     */
    @Test
    public void testFindAllProfilesByUser() {
        List<AgentProfile> resultList = agentProfileService
                .findAllProfilesByUser(user);

        Assert.assertNotNull("result profiles list should not be null", resultList);

        // test if the list is not empty
        Assert.assertFalse("result profiles list should not be empty",
                resultList.isEmpty());

        // test if there is exactly two result
        Assert.assertTrue("result profiles list should contain exactly one result",
                resultList.size() == 1);

        for (AgentProfile profile : resultList) {
            if (profile.isAgent()) {
                Assert.assertEquals("profile name should be equals to "
                        + AGENT_FINAL_NAME, AGENT_FINAL_NAME, profile.getName());
                continue;
            }
            Assert.fail("Profile should be Agent");
        }

    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullFindAllProfilesByUser() {
        this.agentProfileService.findAllProfilesByUser(null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByUser(org.myec3.socle.core.domain.model.User)}
     * .
     */

    @Test
    public void testEmptyFindAllProfilesByUser() {
        User testUser = user;
        // reset id to simulate an invalid user
        testUser.setId(null);
        List<AgentProfile> resultList = this.agentProfileService
                .findAllProfilesByUser(testUser);
        Assert.assertNotNull("list of profiles should not be null", resultList);
        Assert.assertTrue("list of profiles should be empty", resultList.isEmpty());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
     * .
     */
    @Test
    public void testFindAllProfilesByRole() {
        List<AgentProfile> resultList = agentProfileService
                .findAllProfilesByRole(role1);

        Assert.assertNotNull("result roles list should not be null", resultList);

        // test if the list is not empty
        Assert.assertFalse("result roles list should not be empty",
                resultList.isEmpty());

        // test if there is exactly two result
        Assert.assertTrue("result roles list should contain exactly one result",
                resultList.size() == 1);

        for (AgentProfile profile : resultList) {
            if (profile.isAgent()) {
                Assert.assertEquals("role name should be equals to "
                        + AGENT_FINAL_NAME, AGENT_FINAL_NAME, profile.getName());
                continue;
            }
            Assert.fail("Profile should be Agent");
        }
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullFindAllProfilesByRole() {
        this.agentProfileService.findAllProfilesByRole(null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findAllProfilesByRole(org.myec3.socle.core.domain.model.Role)}
     * .
     */
    @Test
    public void testEmptyFindAllProfilesByRole() {
        Role testRole = role1;
        // reset id to simulate an invalid role
        testRole.setId(null);
        List<AgentProfile> resultList = this.agentProfileService
                .findAllProfilesByRole(testRole);
        Assert.assertNotNull("list of profiles should not be null", resultList);
        Assert.assertTrue("list of profiles should be empty", resultList.isEmpty());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findByEmail(java.lang.String)}
     * .
     */
    @Test
    public void testFindByEmail() {
        AgentProfile profile = agentProfileService.findByEmail(EMAIL_AGENT);

        Assert.assertNotNull("result profile should not be null", profile);

        // test if there is the profile is correct
        Assert.assertEquals("result profile should be equals to " + agent, agent,
                profile);

        // test with empty profile
        profile = agentProfileService.findByEmail("null@null.null");

        // result should be null
        Assert.assertNull("result profile should be null", profile);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.AgentProfileServiceImpl#findByEmail(java.lang.String)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullFindByEmail() {
        this.agentProfileService.findByEmail(null);
    }


    @Test
    public void testEmailAlreadyExists() {
        Boolean exists = agentProfileService.emailAlreadyExists(EMAIL_AGENT,
                agent);

        // only the current agent hold this email
        Assert.assertFalse("email should not exist", exists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEmailAlreadyExistsEmail() {
        this.agentProfileService.emailAlreadyExists(null, agent);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullEmailAlreadyExistsProfile() {
        this.agentProfileService.emailAlreadyExists(EMAIL_AGENT, null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.GenericProfileServiceImpl#emailAlreadyExists(String, org.myec3.socle.core.domain.model.Profile)}
     * .
     */
    @Test
    public void testIdNullEmailAlreadyExistsProfile() {
        AgentProfile newAgent = new AgentProfile();
        newAgent.setExternalId(1240L);
        newAgent.setName("new" + AGENT_NAME);
        newAgent.setAddress(address);
        newAgent.setEmail(EMAIL_AGENT);
        newAgent.setEnabled(Boolean.TRUE);
        newAgent.setPhone("+33(0)000000000");
        newAgent.setFax("+33(0)000000000");
        newAgent.setElected(Boolean.FALSE);
        newAgent.setOrganismDepartment(this.organismDepartmentService
                .findRootOrganismDepartment(this.organism));
        newAgent.setUser(user);
        newAgent.setFunction("Agent responsable");
        newAgent.setPrefComMedia(PrefComMedia.EMAIL);

        Boolean exists = this.agentProfileService.emailAlreadyExists(
                newAgent.getEmail(), newAgent);

        // agent is not already created. email should already exist but no error
        // occured
        Assert.assertTrue("email should exist", exists);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.impl.GenericProfileServiceImpl#emailAlreadyExists(String, org.myec3.socle.core.domain.model.Profile)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullEmailAlreadyExistsBoth() {
        this.agentProfileService.emailAlreadyExists(null, null);
    }

    @Test(expected = AssertionError.class)
    public void testAgentSameUsername() throws Exception {
        User newUser = new User();
        newUser.setName(USER_NAME);
        newUser.setFirstname("Georges");
        newUser.setLastname("Abitbol");
        newUser.setCivility(Civility.MR);
        newUser.setEnabled(Boolean.TRUE);
        newUser.setPassword("password");
        newUser.setUsername("a77777 "
                + Calendar.getInstance().getTimeInMillis());
        userService.create(newUser);

        AgentProfile newAgent = new AgentProfile();
        newAgent.setExternalId(1240L);
        newAgent.setName("new" + AGENT_NAME);
        newAgent.setAddress(address);
        newAgent.setEmail(EMAIL_AGENT);
        newAgent.setEnabled(Boolean.TRUE);
        newAgent.setPhone("+33(0)000000000");
        newAgent.setFax("+33(0)000000000");
        newAgent.setElected(Boolean.FALSE);
        newAgent.setOrganismDepartment(this.organismDepartmentService
                .findRootOrganismDepartment(this.organism));
        newAgent.setUser(newUser);
        newAgent.setFunction("Agent responsable");
        newAgent.setPrefComMedia(PrefComMedia.EMAIL);

        agentProfileService.create(newAgent);

        Assert.assertNotNull(agent);
        Assert.assertNotNull(newAgent);
        newAgent.getUser().setUsername(agent.getUser().getUsername());

        Assert.assertFalse(agent.getUser().getUsername()
                .equals(newAgent.getUser().getUsername()));
    }

    @Test
    public void testAgentSameMail() throws Exception {
        AgentProfile newAgent = new AgentProfile();
        newAgent.setExternalId(1240L);
        newAgent.setName("new" + AGENT_NAME);
        newAgent.setAddress(address);
        newAgent.setEmail(agent.getEmail());
        newAgent.setEnabled(Boolean.TRUE);
        newAgent.setPhone("+33(0)000000000");
        newAgent.setFax("+33(0)000000000");
        newAgent.setElected(Boolean.FALSE);
        newAgent.setOrganismDepartment(this.organismDepartmentService
                .findRootOrganismDepartment(this.organism));
        newAgent.setUser(user);
        newAgent.setFunction("Agent responsable");
        newAgent.setPrefComMedia(PrefComMedia.EMAIL);

        agentProfileService.create(newAgent);

    }
}
