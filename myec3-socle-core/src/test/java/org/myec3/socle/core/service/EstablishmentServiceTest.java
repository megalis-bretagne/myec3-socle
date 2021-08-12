package org.myec3.socle.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link org.myec3.socle.core.service.impl.EstablishmentServiceImpl}
 *
*
 */

@Transactional
public class EstablishmentServiceTest extends AbstractDbSocleUnitTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AcronymsListService acronymsListService;

    private static Company company;
    private static Establishment establishment;
    private Address address;

    private static final String COMPANY_NAME = "Test Company "
            + Calendar.getInstance().getTimeInMillis();
    private static final String COMPANY_NAME_2 = "Test Company 2 "
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

        Date date = new Date();
        AdministrativeState administrativeState = new AdministrativeState();
        administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_ACTIVE);
        administrativeState.setAdminStateLastUpdated(date);

        company = new Company();
        company.setExternalId(1235L);
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
        company.setRegistrationCountry(Country.FR);
        company.setAcronym(COMPANY_ACRONYM);
        company.setRM(Boolean.TRUE);
        company.setRCS(Boolean.TRUE);

        companyService.create(company);

        establishment = new Establishment();
        establishment.setAddress(address);
        establishment.setAdministrativeState(administrativeState);
        establishment.setApeCode("6311Z");
        establishment.setApeNafLabel("Traitement de données, hébergement et activités connexes");
        establishment.setCompany(company);
        establishment.setEmail("establishment" + Calendar.getInstance().getTimeInMillis()
                + "@establishment.com");
        establishment.setFax("+33(0)000000000");
        establishment.setForeignIdentifier(Boolean.FALSE);
        establishment.setIsHeadOffice(Boolean.TRUE);
        establishment.setLastUpdate(date);
        establishment.setNic(COMPANY_NIC);
        establishment.setSiret(COMPANY_SIREN + COMPANY_NIC);
        establishment.setLabel(COMPANY_LABEL);
        establishment.setName(COMPANY_NAME);
        establishment.setDiffusableInformations(Boolean.TRUE);

        establishmentService.create(establishment);

    }

    @After
    public void tearDown() {

        if (null != company) {
            companyService.delete(company);
        }

        if (null != establishment) {
            establishmentService.delete(establishment);
        }
    }

    @Test
    public void testfindAllEstablishmentsByCompany() throws Exception {
        List<Establishment> establishmentList = this.establishmentService.findAllEstablishmentsByCompany(
                company);
        assertNotNull(establishmentList);
        assertEquals(true, establishmentList.size() >= 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindAllEstablishmentsByCompanyNull() throws Exception {
        List<Establishment> establishmentList = this.establishmentService.findAllEstablishmentsByCompany(
                null);
        assertNull(establishmentList);
    }

    @Test(expected = AssertionError.class)
    public void testfindAllEstablishmentsByCompanyInvalid() throws Exception {
        Company invalidCompany = new Company();
        List<Establishment> establishmentList = this.establishmentService.findAllEstablishmentsByCompany(
                invalidCompany);
        assertNull(establishmentList);
    }

    @Test
    public void testfindHeadOfficeEstablishmentByCompany() throws Exception {
        Establishment establishment = this.establishmentService.findHeadOfficeEstablishmentByCompany(
                company);
        assertNotNull(establishment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfindHeadOfficeEstablishmentByCompanyNull() throws Exception {
        Establishment establishment = this.establishmentService.findHeadOfficeEstablishmentByCompany(null);
        assertNotNull(establishment);
    }

    @Test(expected = AssertionError.class)
    public void testfindHeadOfficeEstablishmentByCompanyInvalid() throws Exception {
        Company invalidCompany = new Company();
        Establishment establishment = this.establishmentService.findHeadOfficeEstablishmentByCompany(invalidCompany);
        assertNotNull(establishment);
    }

    @Test
    public void testfindByNic() throws Exception {
        Establishment establishment = this.establishmentService.findByNic(COMPANY_SIREN, COMPANY_NIC);
        assertNotNull(establishment);
    }

    @Test
    public void testfindLastForeignCreated() throws Exception {
        Establishment newEstablishment = new Establishment();
        newEstablishment.setName(COMPANY_NAME_2);
        newEstablishment.setAddress(address);
        newEstablishment.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
        newEstablishment.setPhone("+33(0)000000000");
        newEstablishment.setFax("+33(0)000000000");
        newEstablishment.setForeignIdentifier(Boolean.TRUE);
        newEstablishment.setNationalID(COMPANY_NAME_2);
        newEstablishment.setSiret("378901946" + COMPANY_NIC);
        newEstablishment.setApeCode("723Z");
        newEstablishment.setIsHeadOffice(Boolean.FALSE);
        newEstablishment.setCompany(company);
        newEstablishment.setDiffusableInformations(Boolean.FALSE);

        establishmentService.create(newEstablishment);
        Establishment establishment = this.establishmentService.findLastForeignCreated(COMPANY_NAME_2);
        assertNotNull(establishment);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testPersistWithoutCompany() throws Exception {
        Establishment newEstablishment = new Establishment();
        newEstablishment.setName(COMPANY_NAME_2);
        newEstablishment.setAddress(address);
        newEstablishment.setEmail("dircom" + Calendar.getInstance().getTimeInMillis()
                + "@test.com");
        newEstablishment.setPhone("+33(0)000000000");
        newEstablishment.setFax("+33(0)000000000");
        newEstablishment.setForeignIdentifier(Boolean.FALSE);
        newEstablishment.setNic(COMPANY_NIC);
        newEstablishment.setSiret("378901946" + COMPANY_NIC);
        newEstablishment.setApeCode("723Z");
        newEstablishment.setIsHeadOffice(Boolean.FALSE);

        establishmentService.create(newEstablishment);

        Establishment foundResource = establishmentService.findByName(COMPANY_NAME_2);
        assertNotNull("establishment should not be null", foundResource);
        Company root = foundResource.getCompany();
        assertNotNull("Company should not be null", root);

    }

}
