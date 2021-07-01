package org.myec3.socle.core.domain.model.validation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ApplicationValidationTest {
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUrl() throws Exception {

        Application application = new Application();

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "url",
                Default.class);
        Assert.assertEquals("bean url property should be valid : url can be null", 0, constraintViolations.size());

        // correct value
        application.setUrl("url");
        constraintViolations = validator.validateProperty(application, "url", Default.class);
        Assert.assertEquals("bean url property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidRoles() throws Exception {

        Application application = new Application();

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "roles",
                Default.class);
        Assert.assertEquals("bean roles property should be valid : roles can be null", 0, constraintViolations
                .size());

        // empty list
        application.setRoles(new ArrayList<Role>());
        constraintViolations = validator.validateProperty(application, "roles", Default.class);
        Assert.assertEquals("bean roles property should be valid : roles can be empty", 0, constraintViolations
                .size());

        // correct value
        List<Role> roles = new ArrayList<Role>();
        roles.add(new Role());
        application.setRoles(roles);
        constraintViolations = validator.validateProperty(application, "roles", Default.class);
        Assert.assertEquals("bean roles property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidStructures() throws Exception {

        Application application = new Application();

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "structures",
                Default.class);
        Assert.assertEquals("bean structures property should be valid : structures can be null", 0, constraintViolations
                .size());

        // empty list
        application.setStructures(new ArrayList<Structure>());
        constraintViolations = validator.validateProperty(application, "structures", Default.class);
        Assert.assertEquals("bean structures property should be valid : structures can be empty", 0, constraintViolations
                .size());

        // correct value
        List<Structure> structures = new ArrayList<Structure>();
        structures.add(new Organism());
        application.setStructures(structures);
        constraintViolations = validator.validateProperty(application, "structures", Default.class);
        Assert.assertEquals("bean structures property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidStructureTypes() throws Exception {

        Application application = new Application();

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "structureTypes",
                Default.class);
        Assert.assertEquals("bean structureTypes property should be valid : structureTypes can be null", 0, constraintViolations
                .size());

        // empty list
        application.setStructureTypes(new ArrayList<StructureTypeApplication>());
        constraintViolations = validator.validateProperty(application, "structureTypes", Default.class);
        Assert.assertEquals("bean structureTypes property should be valid : structureTypes can be empty", 0, constraintViolations
                .size());

        // correct value
        List<StructureTypeApplication> structureTypes = new ArrayList<StructureTypeApplication>();
        structureTypes.add(new StructureTypeApplication());
        application.setStructureTypes(structureTypes);
        constraintViolations = validator.validateProperty(application, "structures", Default.class);
        Assert.assertEquals("bean structureTypes property should be valid", 0, constraintViolations.size());
    }

    // Inherited from Resource

    @Test
    public void testValidName() throws Exception {

        Application application = new Application();
        application.setName(null);

        // Not null constraint
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "name",
                Default.class);
        Assert.assertEquals("bean name property should be invalid : name cannot be null", 1, constraintViolations.size());

        // valid member
        application.setName("name");
        constraintViolations = validator.validateProperty(application, "name", Default.class);
        Assert.assertEquals("bean name property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidLabel() throws Exception {

        Application application = new Application();
        application.setLabel(null);

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "label",
                Default.class);
        Assert.assertEquals("bean label property should be alid : label can be null", 0, constraintViolations.size());

        // valid workforce
        application.setLabel("label");
        constraintViolations = validator.validateProperty(application, "label", Default.class);
        Assert.assertEquals("bean label property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidId() throws Exception {

        Application application = new Application();
        application.setId(null);

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "id",
                Default.class);
        Assert.assertEquals("bean id property should be alid : id can be null", 0, constraintViolations.size());

        // valid id
        application.setId(0L);
        constraintViolations = validator.validateProperty(application, "id", Default.class);
        Assert.assertEquals("bean id property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidExternalId() throws Exception {

        Application application = new Application();
        application.setExternalId(null);

        // null allowed
        Set<ConstraintViolation<Application>> constraintViolations = validator.validateProperty(application, "externalId",
                Default.class);
        Assert.assertEquals("bean externalId property should be alid : externalId can be null", 0, constraintViolations.size());

        // valid externalId
        application.setExternalId(0L);
        constraintViolations = validator.validateProperty(application, "externalId", Default.class);
        Assert.assertEquals("bean externalId property should be valid", 0, constraintViolations.size());
    }

}
