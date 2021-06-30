package org.myec3.socle.core.domain.model.validation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.OrganismINSEECat;
import org.myec3.socle.core.domain.model.enums.PrefComMedia;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AgentProfileValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidElected() throws Exception {

        AgentProfile agent = new AgentProfile();

        // Not null contstraint
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "elected", Default.class);
        Assert.assertEquals(
                "bean elected property should be invalid : elected cannot be null",
                0, constraintViolations.size());

        // valid member
        agent.setElected(true);
        constraintViolations = validator.validateProperty(agent, "elected",
                Default.class);
        Assert.assertEquals("bean elected property should be valid", 0,
                constraintViolations.size());

    }

    @Test
    public void testValidOrganismDepartment() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setOrganismDepartment(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "organismDepartment", Default.class);
        Assert.assertEquals(
                "bean organismDepartment property should be valid : department cannot be null",
                1, constraintViolations.size());

        // invalid organism department - name null
        OrganismDepartment organismDepartment = new OrganismDepartment();

        agent.setOrganismDepartment(organismDepartment);
        constraintViolations = validator.validateProperty(agent,
                "organismDepartment.organism", Default.class);
        Assert.assertEquals(
                "bean organismDepartment property should be invalid : organismDepartment invalid",
                1, constraintViolations.size());

        // invalid organism department - null organism
        organismDepartment.setName("organism department name");
        organismDepartment.setOrganism(null);

        agent.setOrganismDepartment(organismDepartment);
        constraintViolations = validator.validateProperty(agent,
                "organismDepartment.organism", Default.class);
        Assert.assertEquals(
                "bean organismDepartment property should be invalid : organismDepartment invalid",
                1, constraintViolations.size());

        // invalid organism department - invalid organism
        organismDepartment.setOrganism(new Organism());

        agent.setOrganismDepartment(organismDepartment);
        constraintViolations = validator.validate(agent, Default.class);
        constraintViolations = validator.validateProperty(agent,
                "organismDepartment.organism.name", Default.class);
        Assert.assertTrue(
                "bean organismDepartment property should be invalid : organismDepartment invalid",
                constraintViolations.size() > 0);

        // correct value
        Organism organism = new Organism();
        organism.setLegalCategory(OrganismINSEECat._4);
        organism.setMember(true);
        organism.setEmail("test@test.fr");
        organism.setAcronym("AAA");
        organism.setEnabled(true);

        Address address = new Address();
        address.setCity("test city");
        address.setPostalAddress("test address");
        address.setPostalCode("test postal code");
        organism.setAddress(address);

        organism.setName("organism Name");

        organismDepartment.setOrganism(organism);
        agent.setOrganismDepartment(organismDepartment);
        constraintViolations = validator.validateProperty(agent,
                "organismDepartment", Default.class);
        Assert.assertEquals("bean organismDepartment property should be valid", 0,
                constraintViolations.size());
    }

    // Inherited from Profile

    @Test
    public void testValidFunction() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setFunction(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "function", Default.class);
        Assert.assertEquals(
                "bean function property should be valid : function can be null",
                0, constraintViolations.size());

        // valid phone
        agent.setFunction("phone");
        constraintViolations = validator.validateProperty(agent, "function",
                Default.class);
        Assert.assertEquals("bean function property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidUser() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setUser(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "user", Default.class);
        Assert.assertEquals(
                "bean user property should be valid : user cannot be null", 1,
                constraintViolations.size());

        // invalid user - name null
        User user = new User();

        agent.setUser(user);
        constraintViolations = validator.validateProperty(agent, "user.name",
                Default.class);
        Assert.assertEquals(
                "bean user property should be invalid : organismDepartment invalid",
                1, constraintViolations.size());

        // correct value
        user.setFirstname("first name");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEnabled(true);

        agent.setUser(user);
        constraintViolations = validator.validateProperty(agent, "user",
                Default.class);
        Assert.assertEquals("bean user property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidPrefComMedia() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setPrefComMedia(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "prefComMedia", Default.class);
        Assert.assertEquals(
                "bean prefComMedia property should be valid : function can be null",
                0, constraintViolations.size());

        // valid phone
        agent.setPrefComMedia(PrefComMedia.EMAIL);
        constraintViolations = validator.validateProperty(agent,
                "prefComMedia", Default.class);
        Assert.assertEquals("bean prefComMedia property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidRoles() throws Exception {

        AgentProfile agent = new AgentProfile();

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "roles", Default.class);
        Assert.assertEquals("bean roles property should be valid : roles can be null",
                0, constraintViolations.size());

        // empty list
        agent.setRoles(new ArrayList<Role>());
        constraintViolations = validator.validateProperty(agent, "roles",
                Default.class);
        Assert.assertEquals(
                "bean roles property should be valid : roles can be empty", 0,
                constraintViolations.size());

        // correct value
        List<Role> roles = new ArrayList<Role>();
        roles.add(new Role());
        agent.setRoles(roles);
        constraintViolations = validator.validateProperty(agent, "roles",
                Default.class);
        Assert.assertEquals("bean roles property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidEmail() throws Exception {

        AgentProfile agent = new AgentProfile();

        // Not null constraint
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "email", Default.class);
        Assert.assertEquals(
                "bean email property should be invalid : email cannot be null",
                1, constraintViolations.size());

        // invalid email
        agent.setEmail("test@");
        constraintViolations = validator.validateProperty(agent, "email",
                Default.class);
        Assert.assertEquals("bean email property should be invalid : invalid email",
                1, constraintViolations.size());

        // valid email
        agent.setEmail("test@test.fr");
        constraintViolations = validator.validateProperty(agent, "email",
                Default.class);
        Assert.assertEquals("bean email property should be valid with value : "
                + agent.getEmail(), 0, constraintViolations.size());

        // valid email
        agent.setEmail("test@9test.fr");
        constraintViolations = validator.validateProperty(agent, "email",
                Default.class);
        Assert.assertEquals("bean email property should be valid with value : "
                + agent.getEmail(), 0, constraintViolations.size());
    }

    @Test
    public void testValidEnabled() throws Exception {

        AgentProfile agent = new AgentProfile();

        // valid member if attribute enabled is null the method must return true
        agent.setEnabled(null);
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "enabled", Default.class);
        Assert.assertEquals("bean enabled property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidAddress() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setAddress(null);

        // Not null constraint
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "address", Default.class);
        Assert.assertEquals(
                "bean address property should be invalid : address cannot be null",
                1, constraintViolations.size());

        // invalid address
        Address address = new Address();
        address.setCity("test city");
        address.setPostalAddress("test address");
        address.setPostalCode(null);

        agent.setAddress(address);
        constraintViolations = validator.validateProperty(agent,
                "address.postalCode", Default.class);
        Assert.assertEquals(
                "bean address property should be invalid : address invalid", 1,
                constraintViolations.size());

        // valid address
        address.setPostalCode("test postal code");
        agent.setAddress(address);
        constraintViolations = validator.validateProperty(agent, "address",
                Default.class);
        Assert.assertEquals("bean address property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidFax() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setFax(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "fax", Default.class);
        Assert.assertEquals("bean fax property should be alid : fax can be null", 0,
                constraintViolations.size());

        // valid fax
        agent.setFax("fax");
        constraintViolations = validator.validateProperty(agent, "fax",
                Default.class);
        Assert.assertEquals("bean fax property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidPhone() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setPhone(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "phone", Default.class);
        Assert.assertEquals("bean phone property should be alid : phone can be null",
                0, constraintViolations.size());

        // valid phone
        agent.setPhone("phone");
        constraintViolations = validator.validateProperty(agent, "phone",
                Default.class);
        Assert.assertEquals("bean phone property should be valid", 0,
                constraintViolations.size());
    }

    // Inherited from Resource

    @Test
    public void testValidName() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setName(null);

        // Not null constraint
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "name", Default.class);
        Assert.assertEquals(
                "bean name property should be invalid : name cannot be null",
                1, constraintViolations.size());

        // valid member
        agent.setName("name");
        constraintViolations = validator.validateProperty(agent, "name",
                Default.class);
        Assert.assertEquals("bean name property should be valid", 0,
                constraintViolations.size());

    }

    @Test
    public void testValidLabel() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setLabel(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "label", Default.class);
        Assert.assertEquals("bean label property should be alid : label can be null",
                0, constraintViolations.size());

        // valid workforce
        agent.setLabel("label");
        constraintViolations = validator.validateProperty(agent, "label",
                Default.class);
        Assert.assertEquals("bean label property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidId() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setId(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "id", Default.class);
        Assert.assertEquals("bean id property should be valid : id can be null", 0,
                constraintViolations.size());

        // valid id
        agent.setId(0L);
        constraintViolations = validator.validateProperty(agent, "id",
                Default.class);
        Assert.assertEquals("bean id property should be valid", 0,
                constraintViolations.size());
    }

    @Test
    public void testValidExternalId() throws Exception {

        AgentProfile agent = new AgentProfile();
        agent.setExternalId(null);

        // null allowed
        Set<ConstraintViolation<AgentProfile>> constraintViolations = validator
                .validateProperty(agent, "externalId", Default.class);
        Assert.assertEquals(
                "bean externalId property should be alid : externalId can be null",
                0, constraintViolations.size());

        // valid externalId
        agent.setExternalId(0L);
        constraintViolations = validator.validateProperty(agent, "externalId",
                Default.class);
        Assert.assertEquals("bean externalId property should be valid", 0,
                constraintViolations.size());
    }
}
