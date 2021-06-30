package org.myec3.socle.core.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.exceptions.CompanyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;


@Transactional
public class CompanyServiceTest extends AbstractDbSocleUnitTest {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyDepartmentService companyDepartmentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AcronymsListService acronymsListService;

    private static Company company;
    private static Person person1, person2;
    private static Application application1, application2;
    private Address address;

    private static final String COMPANY_NAME = "Test Company "
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_NAME_2 = "Test Company 2 "
            + Calendar.getInstance().getTimeInMillis();
    private static final String PERSON_NAME_1 = "person 1"
            + Calendar.getInstance().getTimeInMillis();
    private static final String PERSON_NAME_2 = "person 2"
            + Calendar.getInstance().getTimeInMillis();
    private static final String APP_NAME_1 = "Salle des marchés "
            + Calendar.getInstance().getTimeInMillis();
    private static final String APP_NAME_2 = "Gestion documentaire "
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_DEPARTMENT_NAME = "Test Root Department "
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_LABEL = "CompanyLabel"
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_SIREN = "301165338";
    private static final String COMPANY_CITY = "Lyon";
    private static final String COMPANY_POSTAL_CODE = "69000";
    private static final String COMPANY_ACRONYM = "q7z";
    private static final String COMPANY_NIC = "00015";

    @Before
    public void setUp() {

        AcronymsList acronym0 = new AcronymsList();
        acronym0.setValue(COMPANY_ACRONYM);
        acronym0.setAvailable(true);
        acronymsListService.create(acronym0);

        address = new Address();
        address.setPostalAddress("10 boulevard Vivier Merle");
        address.setPostalCode(COMPANY_POSTAL_CODE);
        address.setCanton("Rhône");
        address.setCity(COMPANY_CITY);
        address.setCountry(Country.FR);

        person1 = new Person();
        person1.setName(PERSON_NAME_1);
        person1.setType("PP");
        person1.setCivility(Civility.MR);
        person1.setFirstname("Georges");
        person1.setLastname("Abitbol");

        person2 = new Person();
        person2.setName(PERSON_NAME_2);
        person2.setType("PM");
        person2.setCivility(Civility.MR);
        person2.setFirstname("Georges");
        person2.setLastname("Abitbol");

        List<Person> listPerson = new ArrayList<Person>();
        listPerson.add(person1);
        listPerson.add(person2);

        application1 = new Application();
        application1.setName(APP_NAME_1);
        application1.setUrl("http://urlsdm");
        applicationService.create(application1);

        application2 = new Application();
        application2.setName(APP_NAME_2);
        application2.setExternalId(new Long(1246));
        application2.setUrl("http://urlged");
        applicationService.create(application2);

        List<Application> listApplication = new ArrayList<Application>();
        listApplication.add(application1);
        listApplication.add(application2);

        company = new Company();
        company.setExternalId(new Long(1235));
        company.setName(COMPANY_NAME);
        company.setLabel(COMPANY_LABEL);
        company.setAddress(address);
        company.setDescription("Description");
        company.setEmail("company" + Calendar.getInstance().getTimeInMillis()
                + "@company.com");
        company.setEnabled(Boolean.TRUE);
        company.setPhone("+33(0)000000000");
        company.setFax("+33(0)000000000");
        company.setForeignIdentifier(null);
        company.setIconUrl("http://iconurl");
        company.setLogoUrl("http://logourl");
        company.setLegalCategory(CompanyINSEECat.SA);
        company.setWebsite("http://www.website.com");
        company.setSiren(COMPANY_SIREN);
        company.setNic(COMPANY_NIC);
        company.setApeCode("723Z");
        company.setResponsibles(listPerson);
        company.setRegistrationCountry(Country.FR);
        company.setApplications(listApplication);
        company.setAcronym(COMPANY_ACRONYM);
        company.setRM(Boolean.TRUE);
        company.setRCS(Boolean.TRUE);

        companyService.create(company);
    }

    @After
    public void tearDown() {

        if (null != application1) {
            applicationService.delete(application1);
        }

        if (null != application2) {
            applicationService.delete(application2);
        }

        if (null != company) {
            companyService.delete(company);
        }
    }

    @Test
    public void testfindAllPersonInCompany() throws Exception {
        List<Person> personList = this.companyService.findByName(
                company.getName()).getResponsibles();
        assertNotNull(personList);
        assertEquals(true, personList.size() >= 1);
    }

    @Test
    public void testfindAllApplicationInCompany() throws Exception {
        List<Application> applicationList = this.companyService.findByName(
                company.getName()).getApplications();
        assertNotNull(applicationList);
        assertEquals(true, applicationList.size() >= 1);
    }

    @Test(expected = CompanyCreationException.class)
    public void testErrorPersist() throws Exception {
        Company newCompany = new Company();
        newCompany.setName(COMPANY_NAME_2);
        newCompany.setAddress(address);
        newCompany.setDescription("Description de company");
        newCompany.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
        newCompany.setEnabled(Boolean.TRUE);
        newCompany.setPhone("+33(0)000000000");
        newCompany.setFax("+33(0)000000000");
        newCompany.setForeignIdentifier(null);
        newCompany.setIconUrl("http://iconurl");
        newCompany.setLogoUrl("http://logourl");
        newCompany.setLegalCategory(CompanyINSEECat.SA);
        newCompany.setWebsite("http://www.company.com");
        newCompany.setSiren("378901946");
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);
        newCompany.setAcronym(COMPANY_ACRONYM);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);

        // change id to simulate invalid object
        newCompany.setId(0L);

        companyService.create(newCompany);
    }

    @Test
    public void testPersistWithoutDepartment() throws Exception {
        Company newCompany = new Company();
        newCompany.setName(COMPANY_NAME_2);
        newCompany.setAddress(address);
        newCompany.setDescription("Description company");
        newCompany.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
        newCompany.setEnabled(Boolean.TRUE);
        newCompany.setPhone("+33(0)000000000");
        newCompany.setFax("+33(0)000000000");
        newCompany.setForeignIdentifier(null);
        newCompany.setIconUrl("http://iconurl");
        newCompany.setLogoUrl("http://logourl");
        newCompany.setLegalCategory(CompanyINSEECat.SA);
        newCompany.setWebsite("http://www.company.com");
        newCompany.setSiren("378901946");
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);
        newCompany.setAcronym(COMPANY_ACRONYM);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);

        companyService.create(newCompany);

        Company foundResource = companyService.findByName(COMPANY_NAME_2);
        assertNotNull("company should not be null", foundResource);
        assertEquals("invalid company name", newCompany.getName(),
                foundResource.getName());
        CompanyDepartment root = companyDepartmentService
                .findRootCompanyDepartmentByCompany(foundResource);
        assertNotNull("root department should not be null", root);

        companyDepartmentService.delete(companyDepartmentService
                .findRootCompanyDepartmentByCompany(newCompany));
        companyService.delete(newCompany);
        foundResource = companyService.findByName(newCompany.getName());
        assertNull("organism should be null", foundResource);
    }

    @Test
    public void testPersistWithDepartment() throws Exception {
        Company newCompany = new Company();
        newCompany.setExternalId(new Long(1235));
        newCompany.setName(COMPANY_NAME_2);
        newCompany.setAddress(address);
        newCompany.setDescription("Description company");
        newCompany.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
        newCompany.setEnabled(Boolean.TRUE);
        newCompany.setPhone("+33(0)149009862");
        newCompany.setFax("+33(0)149009800");
        newCompany.setForeignIdentifier(null);
        newCompany.setIconUrl("http://iconurl");
        newCompany.setLogoUrl("http://logourl");
        newCompany.setLegalCategory(CompanyINSEECat.SA);
        newCompany.setWebsite("http://www.company.com");
        newCompany.setSiren("378901946");
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);
        newCompany.setAcronym(COMPANY_ACRONYM);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);

        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setName(COMPANY_DEPARTMENT_NAME);
        companyDepartment.setDescription("");
        companyDepartment.setEmail("test-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        companyDepartment.setPhone("");
        companyDepartment.setAcronym(COMPANY_ACRONYM);
        companyDepartment.setAddress(address);
        companyDepartment.setParentDepartment(null);
        companyDepartment.setCompany(newCompany);

        newCompany.addDepartment(companyDepartment);

        companyService.create(newCompany);

        Company foundResource = companyService.findByName(COMPANY_NAME_2);
        assertNotNull("company should not be null", foundResource);
        assertEquals("invalid organism name", newCompany.getName(),
                foundResource.getName());
        CompanyDepartment root = companyDepartmentService
                .findRootCompanyDepartmentByCompany(foundResource);
        assertNotNull("root department should not be null", root);

        companyDepartmentService.delete(companyDepartmentService
                .findRootCompanyDepartmentByCompany(newCompany));
        companyService.delete(newCompany);
        foundResource = companyService.findByName(newCompany.getName());
        assertNull("organism should be null", foundResource);
    }

    @Test
    public void testFindAllCompanyByCriteriaNullLabel() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel("CompanyLabel 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setDescription("CompanyDescription");
        newCompany.setExternalId(new Long(123));
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService.findAllByCriteria(null,
                COMPANY_SIREN, COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());

    }

    @Test
    public void testFindAllCompanyByCriteriaPartialLabel() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel("PREFIX" + COMPANY_LABEL + "SUFFIX");
        newCompany.setDescription("CompanyDescription");
        newCompany.setExternalId(new Long(123));
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());

    }

    @Test
    public void testFindAllCompanyByCriteriaNullSiren() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren("123456456");
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService.findAllByCriteria(
                COMPANY_LABEL, null, COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaSuffixSiren() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN + "123");
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaPrefixSiren() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren("123" + COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 1", 1, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaNullPostalCode() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode("13000");
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService.findAllByCriteria(
                COMPANY_LABEL, COMPANY_SIREN, null, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaPrefixPostalCode() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode("13" + COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 1", 1, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaSuffixPostalCode() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setExternalId(new Long(123));
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE + "13");
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaNullCity() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity("Paris");
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService.findAllByCriteria(
                COMPANY_LABEL, COMPANY_SIREN, COMPANY_POSTAL_CODE, null);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaPrefixCity() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity("C" + COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 1", 1, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaSuffixCity() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY + "C");
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 1", 1, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaAll() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name 2"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService
                .findAllByCriteria(COMPANY_LABEL, COMPANY_SIREN,
                        COMPANY_POSTAL_CODE, COMPANY_CITY);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());
        assertEquals("organisms size should be exactly 2", 2, organisms.size());
    }

    @Test
    public void testFindAllCompanyByCriteriaAllNull() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);

        List<Company> organisms = this.companyService.findAllByCriteria(null,
                null, null, null);
        assertNotNull("organisms should not be null", organisms);
        assertFalse("organisms should not be empty", organisms.isEmpty());

        List<Company> all = this.companyService.findAll();

        assertEquals("organisms should contain all organisms", organisms, all);
    }

    @Test
    public void testIsSiretValidNullSiren() throws Exception {
        Boolean result = this.companyService.isSiretValid(null, COMPANY_NIC);
        assertFalse("siret should be invalid", result);
    }

    @Test
    public void testIsSiretValidNullNic() throws Exception {
        Boolean result = this.companyService.isSiretValid(COMPANY_SIREN, null);
        assertFalse("siret should be invalid", result);
    }

    @Test
    public void testIsSiretValid() throws Exception {
        Boolean result = this.companyService.isSiretValid(COMPANY_SIREN,
                COMPANY_NIC);
        assertTrue("siret should be valid", result);
    }

    @Test
    public void testIsSiretValidInvalidNic() throws Exception {
        Boolean result = this.companyService.isSiretValid(COMPANY_SIREN,
                COMPANY_NIC + "123456");
        assertFalse("siret should be invalid", result);
    }

    @Test
    public void testIsSiretValidInvalidSiren() throws Exception {
        Boolean result = this.companyService.isSiretValid(
                COMPANY_SIREN + "123", COMPANY_NIC);
        assertFalse("siret should be invalid", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByNullSiren() throws Exception {
        this.companyService.findBySiren(null);
    }

    @Test
    public void testEmptyFindBySiren() throws Exception {
        Company foundCompany = this.companyService.findBySiren("132456");
        assertNull("organism should be null", foundCompany);
    }

    @Test
    public void testFindBySiren() throws Exception {
        Company foundCompany = this.companyService.findBySiren(COMPANY_SIREN);
        assertNotNull("organism should be null", foundCompany);
        assertEquals("organism name is invalid", COMPANY_NAME,
                foundCompany.getName());
    }

    @Test
    public void testFindBySameSiren() throws Exception {
        Company newCompany = new Company();

        newCompany.setName("Company Name"
                + Calendar.getInstance().getTimeInMillis());
        newCompany.setLabel(COMPANY_LABEL);
        newCompany.setDescription("CompanyDescription");
        newCompany.setEmail("test-create-organism"
                + Calendar.getInstance().getTimeInMillis() + "@test.fr");
        newCompany.setPhone("0000000000");
        newCompany.setAcronym("o9o");
        newCompany.setSiren(COMPANY_SIREN);
        newCompany.setRM(Boolean.TRUE);
        newCompany.setRCS(Boolean.TRUE);
        newCompany.setNic(COMPANY_NIC);
        newCompany.setApeCode("723Z");
        newCompany.setRegistrationCountry(Country.FR);

        Address add = new Address();
        add.setPostalAddress("Address");
        add.setPostalCode(COMPANY_POSTAL_CODE);
        add.setCity(COMPANY_CITY);
        add.setCountry(Country.FR);

        newCompany.setAddress(add);
        newCompany.setLegalCategory(CompanyINSEECat.AUTRE);

        companyService.create(newCompany);
        Assert.assertNull(this.companyService.findBySiren(COMPANY_SIREN));
    }

    @Test
    public void testIsSirenNull() throws Exception {
        Boolean result = this.companyService.isSirenValid(null);
        assertFalse("Siren should be invalid", result);
    }

    @Test
    public void testIsSirenValid() throws Exception {
        Boolean result = this.companyService.isSirenValid(COMPANY_SIREN);
        assertTrue("Siren should be valid", result);
    }

    @Test
    public void testIsSirenInvalid() throws Exception {
        Boolean result = this.companyService.isSirenValid("123456");
        assertFalse("Siren should be invalid", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByNullAcronym() throws Exception {
        this.companyService.findByAcronym(null);
    }

    @Test
    public void testEmptyFindByAcronym() throws Exception {
        Company foundCompany = this.companyService.findByAcronym("132");
        assertNull("organism should be null", foundCompany);
    }

    @Test
    public void testFindByAcronym() throws Exception {
        Company foundCompany = this.companyService
                .findByAcronym(COMPANY_ACRONYM);
        assertNotNull("organism should be null", foundCompany);
        assertEquals("organism name is invalid", COMPANY_NAME,
                foundCompany.getName());
    }
}
