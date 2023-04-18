package org.myec3.socle.core.service;

import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class ApplicationServiceTest extends AbstractDbSocleUnitTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StructureTypeApplicationService structureTypeApplicationService;

    @Autowired
    private StructureTypeService structureTypeService;

    @Autowired
    private CustomerService customerService;

    private Application application;
    private Customer customer;
    private Company company;
    private Application application3, application4;
    private Address address;
    private List<Application> listApplication;
    private StructureType structureType;
    private StructureTypeApplication structureTypeAppDefault;
    private StructureTypeApplication structureTypeAppSubscribable;

    private static final String APP_NAME_1 = "Salle des marchés "
            + Calendar.getInstance().getTimeInMillis();
    private static final String APP_NAME_2 = "Gestion documentaire "
            + Calendar.getInstance().getTimeInMillis();
    private static final String APP_NAME_3 = "Test App 3 "
            + Calendar.getInstance().getTimeInMillis();
    private static final String APP_NAME_4 = "Test App 4 "
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_NAME = "Test Company1"
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_LABEL = "CompanyLabel"
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_SIREN = "378901946";
    private static final String COMPANY_CITY = "Lyon";
    private static final String COMPANY_POSTAL_CODE = "69000";
    private static final String COMPANY_ACRONYM = "q7z";
    private static final String COMPANY_NIC = "00483";
    private static final String CUSTOMER_NAME = "Test Customer";

    @Before
    public void setUp() {
        address = new Address();
        address.setPostalAddress("10 bld Vivier Merle");
        address.setPostalCode(COMPANY_POSTAL_CODE);
        address.setCanton("Rhône");
        address.setCity(COMPANY_CITY);
        address.setCountry(Country.FR);

        structureType = new StructureType();
        structureType.setValue(StructureTypeValue.ORGANISM);
        structureTypeService.create(structureType);

        application3 = new Application();
        application3.setName(APP_NAME_3);
        application3.setUrl("http://urlsdm");
        applicationService.create(application3);

        application4 = new Application();
        application4.setName(APP_NAME_4);
        application4.setUrl("http://urlged");
        applicationService.create(application4);

        listApplication = new ArrayList<>();
        listApplication.add(application3);
        listApplication.add(application4);

        // Create customer
        customer = new Customer();
        customer.setName(CUSTOMER_NAME);
        customer.setLabel("label");
        customer.addApplication(application3);
        customer.addApplication(application4);
        customerService.create(customer);

        // default
        structureTypeAppDefault = new StructureTypeApplication();
        structureTypeAppDefault.setApplication(application3);
        structureTypeAppDefault.setStructureType(structureType);
        structureTypeAppDefault.setDefaultSubscription(Boolean.TRUE);
        structureTypeAppDefault.setSubscribable(Boolean.FALSE);
        structureTypeAppDefault.setMultipleRoles(Boolean.FALSE);

        this.structureTypeApplicationService.create(structureTypeAppDefault);

        // subscribable
        structureTypeAppSubscribable = new StructureTypeApplication();
        structureTypeAppSubscribable.setApplication(application4);
        structureTypeAppSubscribable.setStructureType(structureType);
        structureTypeAppSubscribable.setDefaultSubscription(Boolean.FALSE);
        structureTypeAppSubscribable.setSubscribable(Boolean.TRUE);

        this.structureTypeApplicationService
                .create(structureTypeAppSubscribable);

        company = new Company();
        company.setName(COMPANY_NAME);
        company.setLabel(COMPANY_LABEL);
        company.setAddress(address);
        company.setDescription("Description");
        company.setEmail("company" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
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
        company.setRegistrationCountry(Country.FR);
        company.setAcronym(COMPANY_ACRONYM);
        company.setRM(Boolean.TRUE);
        company.setRCS(Boolean.TRUE);

        companyService.create(company);

        application = new Application(APP_NAME_1, "ApplicationLabel");
        application.setUrl("http://test/application");
        applicationService.create(application);
    }

    @Test
    public void testPersist() throws Exception {
        Application newApplication = new Application(APP_NAME_2,
                "ApplicationCreateLabel");
        newApplication.setExternalId(123L);
        newApplication.setUrl("http://test/application");

        applicationService.create(newApplication);

        Resource foundResource = applicationService.findByName(APP_NAME_2);
        assertNotNull(foundResource);
        assertEquals(APP_NAME_2, foundResource.getName());

        applicationService.delete(newApplication);
        foundResource = applicationService.findByName(APP_NAME_2);
        assertNull(foundResource);
    }

    @Test
    public void testMerge() throws Exception {
        final String modifiedName = "Modified Test Resource "
                + Calendar.getInstance().getTimeInMillis();

        Application foundApplication = applicationService
                .findByName(APP_NAME_1);
        foundApplication.setName(modifiedName);
        applicationService.update(foundApplication);

        Application mergedApplication = applicationService
                .findByName(foundApplication.getName());
        assertNotNull(mergedApplication);
        assertEquals(foundApplication.getName(), mergedApplication.getName());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidMerge() throws Exception {
        Application foundApplication = applicationService
                .findByName(APP_NAME_1);

        assertNotNull(foundApplication);

        // remove to simulate invalid object
        this.applicationService.delete(foundApplication);

        this.applicationService.update(foundApplication);
    }

    @Test
    public void testInvalidRemove() throws Exception {
        Application foundApplication = applicationService
                .findByName(APP_NAME_1);

        assertNotNull(foundApplication);

        // change id to simulate invalid object
        foundApplication.setId(0L);
        this.applicationService.delete(foundApplication);

        Application foundDeletedApplication = applicationService
                .findByName(APP_NAME_1);
        assertNull(foundDeletedApplication);
    }

    @Test
    public void testRemove() throws Exception {
        Application foundApplication = applicationService
                .findByName(APP_NAME_1);
        applicationService.delete(foundApplication);

        this.application = applicationService.findByName(APP_NAME_1);
        assertNull(this.application);
    }

    @Test
    public void testRemoveById() throws Exception {
        Application foundApplication = applicationService
                .findByName(APP_NAME_1);
        applicationService.delete(foundApplication);

        this.application = applicationService.findByName(APP_NAME_1);
        assertNull(this.application);
    }

    @Test
    public void testFindAll() throws Exception {
        List<Application> applicationList = applicationService.findAll();
        assertEquals(true, applicationList.size() >= 1);
    }

    @Test
    public void testFindByName() throws Exception {
        Application foundApplication = applicationService
                .findByName(APP_NAME_1);
        assertNotNull(foundApplication);
        assertEquals(APP_NAME_1, foundApplication.getName());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationByStructure(org.myec3.socle.core.domain.model.Structure)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindAllApplicationByNullStructure() {
        this.applicationService.findAllApplicationByStructure(null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationByStructure(org.myec3.socle.core.domain.model.Structure)}
     * .
     */
    @Test
    public void testEmptyFindAllApplicationByStructure() {
        company.setApplications(null);
        this.companyService.update(company);
        List<Application> applications = this.applicationService
                .findAllApplicationByStructure(company);

        assertNotNull("applications should not be null", applications);
        assertTrue("applications should be empty", applications.isEmpty());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationByStructure(org.myec3.socle.core.domain.model.Structure)}
     * .
     */
    @Test(expected = RuntimeException.class)
    public void testErrorFindAllApplicationByStructure() {
        // change id to simulate invalid object
        company.setId(0L);

        this.applicationService.findAllApplicationByStructure(company);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationByStructure(org.myec3.socle.core.domain.model.Structure)}
     * .
     */
    @Test
    public void testFindAllApplicationByStructure() {
        company.setApplications(listApplication);
        this.companyService.update(company);

        List<Application> applications = this.applicationService
                .findAllApplicationByStructure(company);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should be empty", applications.isEmpty());
        assertTrue("applications size should be = 2", applications.size() == 2);
        assertTrue("applications should contain application3",
                applications.contains(application3));
        assertTrue("applications should contain application4",
                applications.contains(application4));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFindAllSubscribableDefaultApplicationByNullStructureType() {
        this.applicationService.findAllDefaultApplicationsByStructureType(null);
    }


    @Test
    public void testFindAllFalseSubscribableTrueDefaultApplicationByStructureType() {

        List<Application> applications = this.applicationService
                .findAllDefaultApplicationsByStructureType(structureType);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should not be empty", applications.isEmpty());
        assertTrue("applications size should be >= 1", applications.size() >= 1);
        assertTrue("applications should contain application3",
                applications.contains(application3));
        assertFalse("applications should not contain application4",
                applications.contains(application4));

    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindAllApplicationNullSubscribableByStructureType() {
        this.applicationService.findAllApplicationSubscribableByStructureType(
                null, Boolean.TRUE);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindAllApplicationSubscribableByNullStructureType() {
        this.applicationService.findAllApplicationSubscribableByStructureType(
                structureType, null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test
    public void testEmptyFindAllApplicationSubscribableByStructureType() {
        List<StructureTypeApplication> structureTypeApplications = this.structureTypeApplicationService
                .findAll();
        for (StructureTypeApplication application : structureTypeApplications) {
            this.structureTypeApplicationService.delete(application);
        }

        List<Application> applications = this.applicationService
                .findAllApplicationSubscribableByStructureType(structureType,
                        Boolean.TRUE);

        assertNotNull("applications should not be null", applications);
        assertTrue("applications should be empty", applications.isEmpty());
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test(expected = RuntimeException.class)
    public void testErrorFindAllApplicationSubscribableByStructureType() {

        // change id to simulate invalid object
        structureType.setId(0L);

        this.applicationService.findAllApplicationSubscribableByStructureType(
                structureType, Boolean.TRUE);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test
    public void testFindAllApplicationTrueSubscribableByStructureType() {

        List<Application> applications = this.applicationService
                .findAllApplicationSubscribableByStructureType(structureType,
                        Boolean.TRUE);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should not be empty", applications.isEmpty());
        assertTrue("applications size should be >= 1", applications.size() >= 1);
        assertFalse("applications should not contain application3",
                applications.contains(application3));
        assertTrue("applications should contain application4",
                applications.contains(application4));
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureType(org.myec3.socle.core.domain.model.meta.StructureType, java.lang.Boolean)}
     * .
     */
    @Test
    public void testFindAllApplicationFalseSubscribableByStructureType() {
        List<Application> applications = this.applicationService
                .findAllApplicationSubscribableByStructureType(structureType,
                        Boolean.FALSE);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should not be empty", applications.isEmpty());
        assertTrue("applications size should be >= 1", applications.size() >= 1);
        assertTrue("applications should contain application3",
                applications.contains(application3));
        assertFalse("applications should not contain application4",
                applications.contains(application4));
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllDefaultApplicationsByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test
    public void findAllDefaultApplicationsByStructureTypeAndCustomer() {
        List<Application> applications = this.applicationService
                .findAllDefaultApplicationsByStructureTypeAndCustomer(
                        structureType, customer);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should not be empty", applications.isEmpty());
        assertTrue("applications size should be >= 1", applications.size() >= 1);
        assertTrue("applications should contain application3",
                applications.contains(application3));
        assertFalse("applications should not contain application4",
                applications.contains(application4));
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllDefaultApplicationsByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllDefaultApplicationsByStructureTypeAndCustomerNull() {
        this.applicationService
                .findAllDefaultApplicationsByStructureTypeAndCustomer(
                        structureType, null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllDefaultApplicationsByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllDefaultApplicationsByStructureTypeNullAndCustomer() {
        this.applicationService
                .findAllDefaultApplicationsByStructureTypeAndCustomer(null,
                        customer);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllDefaultApplicationsByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllDefaultApplicationsByStructureTypeNullAndCustomerNull() {
        this.applicationService
                .findAllDefaultApplicationsByStructureTypeAndCustomer(null,
                        null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test
    public void findAllApplicationSubscribableByStructureTypeAndCustomer() {
        List<Application> applications = this.applicationService
                .findAllApplicationSubscribableByStructureTypeAndCustomer(
                        structureType, customer);

        assertNotNull("applications should not be null", applications);
        assertFalse("applications should not be empty", applications.isEmpty());
        assertTrue("applications size should be >= 1", applications.size() >= 1);
        assertTrue("applications should contain application4",
                applications.contains(application4));
        assertFalse("applications should not contain application3",
                applications.contains(application3));
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllApplicationSubscribableByStructureTypeNullAndCustomer() {
        this.applicationService
                .findAllApplicationSubscribableByStructureTypeAndCustomer(null,
                        customer);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllApplicationSubscribableByStructureTypeAndCustomerNull() {
        this.applicationService
                .findAllApplicationSubscribableByStructureTypeAndCustomer(
                        structureType, null);
    }

    /**
     * Test method for
     * {@link org.myec3.socle.core.service.ApplicationService#findAllApplicationSubscribableByStructureTypeAndCustomer(org.myec3.socle.core.domain.model.meta.StructureType, org.myec3.socle.core.domain.model.Customer)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public void findAllApplicationSubscribableByStructureTypeNullAndCustomerNull() {
        this.applicationService
                .findAllApplicationSubscribableByStructureTypeAndCustomer(null,
                        null);
    }
}
