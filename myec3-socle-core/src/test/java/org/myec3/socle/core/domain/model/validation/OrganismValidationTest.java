package org.myec3.socle.core.domain.model.validation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Article;
import org.myec3.socle.core.domain.model.enums.OrganismINSEECat;
import org.myec3.socle.core.domain.model.meta.StructureType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganismValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidMember() throws Exception {

        Organism organism = new Organism();

        // Not null contstraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "member",
                Default.class);
        Assert.assertEquals("bean member property should be invalid : member cannot be null", 1, constraintViolations.size());

        // valid member
        organism.setMember(true);
        constraintViolations = validator.validateProperty(organism, "member", Default.class);
        Assert.assertEquals("bean member property should be valid", 0, constraintViolations.size());

    }

    @Test
    public void testValidLegalCategory() throws Exception {

        Organism organism = new Organism();

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "legalCategory",
                Default.class);
        Assert.assertEquals("bean legalCategory property should be invalid : legalCategory cannot be null", 1,
                constraintViolations.size());

        // valid category
        organism.setLegalCategory(OrganismINSEECat._4);
        constraintViolations = validator.validateProperty(organism, "legalCategory", Default.class);
        Assert.assertEquals("bean legalCategory property should be valid", 0, constraintViolations.size());

    }

    @Test
    public void testValidArticle() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "article",
                Default.class);
        Assert.assertEquals("bean article property should be valid : article can be null", 0, constraintViolations.size());

        // correct value
        organism.setArticle(Article.LE);
        constraintViolations = validator.validateProperty(organism, "article", Default.class);
        Assert.assertEquals("bean article property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidBudget() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "budget",
                Default.class);
        Assert.assertEquals("bean budget property should be valid : budget can be null", 0, constraintViolations.size());

        // correct value
        organism.setBudget(0);
        constraintViolations = validator.validateProperty(organism, "budget", Default.class);
        Assert.assertEquals("bean budget property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidCollege() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "college",
                Default.class);
        Assert.assertEquals("bean college property should be valid : college can be null", 0, constraintViolations.size());

        // correct value
        organism.setCollege("A_DEFINIR");
        constraintViolations = validator.validateProperty(organism, "college", Default.class);
        Assert.assertEquals("bean college property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidContributionAmount() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism,
                "contributionAmount", Default.class);
        Assert.assertEquals("bean contributionAmount property should be valid : contributionAmount can be null", 0,
                constraintViolations.size());

        // correct value
        organism.setContributionAmount(0);
        constraintViolations = validator.validateProperty(organism, "contributionAmount", Default.class);
        Assert.assertEquals("bean contributionAmount property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidDepartments() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "departments",
                Default.class);
        Assert.assertEquals("bean departments property should be valid : departments can be null", 0, constraintViolations
                .size());

        // empty list
        organism.setDepartments(new ArrayList<OrganismDepartment>());
        constraintViolations = validator.validateProperty(organism, "departments", Default.class);
        Assert.assertEquals("bean departments property should be valid : departments can be empty", 0, constraintViolations
                .size());

        // correct value
        List<OrganismDepartment> departments = new ArrayList<OrganismDepartment>();
        departments.add(new OrganismDepartment());
        organism.setDepartments(departments);
        constraintViolations = validator.validateProperty(organism, "departments", Default.class);
        Assert.assertEquals("bean departments property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidOrganismStatus() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "organismStatus",
                Default.class);
        Assert.assertEquals("bean organismStatus property should be valid : organismStatus can be null", 0, constraintViolations
                .size());

        // empty set
        organism.setOrganismStatus(new HashSet<OrganismStatus>());
        constraintViolations = validator.validateProperty(organism, "organismStatus", Default.class);
        Assert.assertEquals("bean organismStatus property should be valid : organismStatus can be empty", 0, constraintViolations
                .size());

        // correct value
        HashSet<OrganismStatus> organismStatus = new HashSet<OrganismStatus>();
        organismStatus.add(new OrganismStatus());
        organism.setOrganismStatus(organismStatus);
        constraintViolations = validator.validateProperty(organism, "organismStatus", Default.class);
        Assert.assertEquals("bean organismStatus property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidOfficialPopulation() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism,
                "officialPopulation", Default.class);
        Assert.assertEquals("bean officialPopulation property should be valid : officialPopulation can be null", 0,
                constraintViolations.size());

        // correct value
        organism.setOfficialPopulation(0);
        constraintViolations = validator.validateProperty(organism, "officialPopulation", Default.class);
        Assert.assertEquals("bean officialPopulation property should be valid", 0, constraintViolations.size());
    }

    // Inherited from Structure

    @Test
    public void testValidEmail() throws Exception {

        Organism organism = new Organism();

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "email",
                Default.class);
        Assert.assertEquals("bean email property should be invalid : email cannot be null", 1, constraintViolations.size());

        // invalid email
        organism.setEmail("test@");
        constraintViolations = validator.validateProperty(organism, "email", Default.class);
        Assert.assertEquals("bean email property should be invalid : invalid email", 1, constraintViolations.size());

        // valid email
        organism.setEmail("test@test.fr");
        constraintViolations = validator.validateProperty(organism, "email", Default.class);
        Assert.assertEquals("bean email property should be valid with value : " + organism.getEmail(), 0, constraintViolations
                .size());

        // valid email
        organism.setEmail("test@9test.fr");
        constraintViolations = validator.validateProperty(organism, "email", Default.class);
        Assert.assertEquals("bean email property should be valid with value : " + organism.getEmail(), 0, constraintViolations
                .size());
    }

    @Test
    public void testValidAcronym() throws Exception {

        Organism organism = new Organism();

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "acronym",
                Default.class);
        Assert.assertEquals("bean acronym property should be invalid : acronym cannot be null", 1, constraintViolations.size());

        // invalid acronym
        organism.setAcronym("AAAA");
        constraintViolations = validator.validateProperty(organism, "acronym", Default.class);
        Assert.assertEquals("bean acronym property should be invalid : acronym length cannot be > 3", 1, constraintViolations
                .size());

        // valid acronym
        organism.setAcronym("AAA");
        constraintViolations = validator.validateProperty(organism, "acronym", Default.class);
        Assert.assertEquals("bean acronym property should be valid with value : " + organism.getEmail(), 0,
                constraintViolations.size());

    }

    @Test
    public void testValidEnabled() throws Exception {

        Organism organism = new Organism();
        organism.setEnabled(null);

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "enabled",
                Default.class);
        Assert.assertEquals("bean enabled property should be invalid : enabled cannot be null", 1, constraintViolations.size());

        // valid member
        organism.setEnabled(true);
        constraintViolations = validator.validateProperty(organism, "enabled", Default.class);
        Assert.assertEquals("bean enabled property should be valid", 0, constraintViolations.size());

    }

    @Test
    public void testValidAddress() throws Exception {

        Organism organism = new Organism();
        organism.setAddress(null);

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "address",
                Default.class);
        Assert.assertEquals("bean address property should be invalid : address cannot be null", 1, constraintViolations.size());

        // invalid address
        Address address = new Address();
        address.setCity("test city");
        address.setPostalAddress("test address");
        address.setPostalCode(null);

        organism.setAddress(address);
        constraintViolations = validator.validateProperty(organism, "address.postalCode", Default.class);
        Assert.assertEquals("bean address property should be invalid : address invalid", 1, constraintViolations.size());

        // valid address
        address.setPostalCode("test postal code");
        organism.setAddress(address);
        constraintViolations = validator.validateProperty(organism, "address", Default.class);
        Assert.assertEquals("bean address property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidApplications() throws Exception {

        Organism organism = new Organism();

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "applications",
                Default.class);
        Assert.assertEquals("bean applications property should be valid : applications can be null", 0, constraintViolations
                .size());

        // empty list
        organism.setApplications(new ArrayList<Application>());
        constraintViolations = validator.validateProperty(organism, "applications", Default.class);
        Assert.assertEquals("bean applications property should be valid : applications can be empty", 0, constraintViolations
                .size());

        // correct value
        List<Application> applications = new ArrayList<Application>();
        applications.add(new Application());
        organism.setContributionAmount(0);
        constraintViolations = validator.validateProperty(organism, "applications", Default.class);
        Assert.assertEquals("bean applications property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidDescription() throws Exception {

        Organism organism = new Organism();
        organism.setDescription(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "description",
                Default.class);
        Assert.assertEquals("bean description property should be valid : description can be null", 0, constraintViolations
                .size());

        // valid description
        organism.setDescription("description");
        constraintViolations = validator.validateProperty(organism, "description", Default.class);
        Assert.assertEquals("bean description property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidFax() throws Exception {

        Organism organism = new Organism();
        organism.setFax(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "fax",
                Default.class);
        Assert.assertEquals("bean fax property should be alid : fax can be null", 0, constraintViolations.size());

        // valid fax
        organism.setFax("fax");
        constraintViolations = validator.validateProperty(organism, "fax", Default.class);
        Assert.assertEquals("bean fax property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidIconUrl() throws Exception {

        Organism organism = new Organism();
        organism.setIconUrl(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "iconUrl",
                Default.class);
        Assert.assertEquals("bean iconUrl property should be alid : iconUrl can be null", 0, constraintViolations.size());

        // valid icon url
        organism.setIconUrl("iconUrl");
        constraintViolations = validator.validateProperty(organism, "iconUrl", Default.class);
        Assert.assertEquals("bean iconUrl property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidLogoUrl() throws Exception {

        Organism organism = new Organism();
        organism.setLogoUrl(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "logoUrl",
                Default.class);
        Assert.assertEquals("bean logoUrl property should be alid : logoUrl can be null", 0, constraintViolations.size());

        // valid logoUrl
        organism.setLogoUrl("logoUrl");
        constraintViolations = validator.validateProperty(organism, "logoUrl", Default.class);
        Assert.assertEquals("bean logoUrl property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidPhone() throws Exception {

        Organism organism = new Organism();
        organism.setPhone(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "phone",
                Default.class);
        Assert.assertEquals("bean phone property should be alid : phone can be null", 0, constraintViolations.size());

        // valid phone
        organism.setPhone("phone");
        constraintViolations = validator.validateProperty(organism, "phone", Default.class);
        Assert.assertEquals("bean phone property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidSiren() throws Exception {

        Organism organism = new Organism();
        organism.setSiren(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "siren",
                Default.class);
        Assert.assertEquals("bean siren property should be alid : siren can be null", 0, constraintViolations.size());

        // valid siren
        organism.setSiren("siren");
        constraintViolations = validator.validateProperty(organism, "siren", Default.class);
        Assert.assertEquals("bean siren property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidStructureType() throws Exception {

        Organism organism = new Organism();
        organism.setStructureType(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "structureType",
                Default.class);
        Assert.assertEquals("bean structureType property should be alid : structureType can be null", 0, constraintViolations.size());

        // valid structureType
        organism.setStructureType(new StructureType());
        constraintViolations = validator.validateProperty(organism, "structureType", Default.class);
        Assert.assertEquals("bean structureType property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidWebsite() throws Exception {

        Organism organism = new Organism();
        organism.setWebsite(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "website",
                Default.class);
        Assert.assertEquals("bean website property should be alid : website can be null", 0, constraintViolations.size());

        // valid website
        organism.setWebsite("website");
        constraintViolations = validator.validateProperty(organism, "website", Default.class);
        Assert.assertEquals("bean website property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidWorkforce() throws Exception {

        Organism organism = new Organism();
        organism.setWorkforce(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "workforce",
                Default.class);
        Assert.assertEquals("bean workforce property should be alid : workforce can be null", 0, constraintViolations.size());

        // valid workforce
        organism.setWorkforce(0);
        constraintViolations = validator.validateProperty(organism, "workforce", Default.class);
        Assert.assertEquals("bean workforce property should be valid", 0, constraintViolations.size());
    }

    // Inherited from Resource

    @Test
    public void testValidName() throws Exception {

        Organism organism = new Organism();
        organism.setName(null);

        // Not null constraint
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "name",
                Default.class);
        Assert.assertEquals("bean name property should be invalid : name cannot be null", 1, constraintViolations.size());

        // valid member
        organism.setName("name");
        constraintViolations = validator.validateProperty(organism, "name", Default.class);
        Assert.assertEquals("bean name property should be valid", 0, constraintViolations.size());

    }

    @Test
    public void testValidLabel() throws Exception {

        Organism organism = new Organism();
        organism.setLabel(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "label",
                Default.class);
        Assert.assertEquals("bean label property should be alid : label can be null", 0, constraintViolations.size());

        // valid workforce
        organism.setLabel("label");
        constraintViolations = validator.validateProperty(organism, "label", Default.class);
        Assert.assertEquals("bean label property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidId() throws Exception {

        Organism organism = new Organism();
        organism.setId(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "id",
                Default.class);
        Assert.assertEquals("bean id property should be alid : id can be null", 0, constraintViolations.size());

        // valid id
        organism.setId(0L);
        constraintViolations = validator.validateProperty(organism, "id", Default.class);
        Assert.assertEquals("bean id property should be valid", 0, constraintViolations.size());
    }

    @Test
    public void testValidExternalId() throws Exception {

        Organism organism = new Organism();
        organism.setExternalId(null);

        // null allowed
        Set<ConstraintViolation<Organism>> constraintViolations = validator.validateProperty(organism, "externalId",
                Default.class);
        Assert.assertEquals("bean externalId property should be alid : externalId can be null", 0, constraintViolations.size());

        // valid externalId
        organism.setExternalId(0L);
        constraintViolations = validator.validateProperty(organism, "externalId", Default.class);
        Assert.assertEquals("bean externalId property should be valid", 0, constraintViolations.size());
    }
}
