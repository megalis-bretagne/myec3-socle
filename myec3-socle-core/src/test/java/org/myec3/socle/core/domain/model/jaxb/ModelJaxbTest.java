package org.myec3.socle.core.domain.model.jaxb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.tools.SynchronizationMarshaller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ModelJaxbTest {

    private User user;
    private AgentProfile agent;
    private EmployeeProfile employee;
    private Organism organism;
    private Company company;
    private OrganismDepartment organismDepartment0;
    private OrganismDepartment organismDepartment1;
    private OrganismDepartment organismDepartment2;
    private OrganismDepartment organismDepartment3;
    private CompanyDepartment companyDepartment1;
    private CompanyDepartment companyDepartment2;
    private Application application1;
    private Application application2;
    private Role role1;
    private Role role2;
    private Customer customer;

    @Before
    public void setUp() {
        application1 = new Application();
        application1.setId(12L);
        application1.setName("Salle des marchés");
        application1.setExternalId(1245L);
        application1.setUrl("http://urlsdm");

        application2 = new Application();
        application2.setId(13L);
        application2.setName("Gestion documentaire");
        application2.setExternalId(1246L);
        application2.setUrl("http://urlged");

        role1 = new Role();
        role1.setId(10L);
        role1.setExternalId(1242L);
        role1.setName("Administrateur");
        role1.setApplication(application1);
        role2 = new Role();
        role2.setId(11L);
        role2.setExternalId(1243L);
        role2.setName("Contributeur");
        role2.setApplication(application2);

        Address address1 = new Address();
        address1.setPostalAddress("107 -108 boulevard Vivier Merle");
        address1.setPostalCode("69003");
        address1.setCanton("Rhône");
        address1.setCity("Lyon");
        address1.setCountry(Country.FR);

        Address address2 = new Address();
        address2.setPostalAddress("company  - Tour Manhattan - 5, 6 place de l'Iris - La Défense 2");
        address2.setPostalCode("92926");
        address2.setCanton("Ile-de-France");
        address2.setCity("Paris La Défense");
        address2.setCountry(Country.FR);

        Address address3 = new Address();
        address3.setPostalAddress("17 boulevard de la Trémouille - BP 1602");
        address3.setPostalCode("21035");
        address3.setCanton("test");
        address3.setCity("Dijon");
        address3.setCountry(Country.FR);

        user = new User();
        user.setId(1L);
        user.setName("Bernard Dupond");
        user.setFirstname("Bernard");
        user.setLastname("Dupond");
        user.setCivility(Civility.MR);
        user.setEnabled(Boolean.TRUE);
        user.setUsername("username");

        company = new Company();
        company.setId(2L);
        company.setExternalId(1235L);
        company.setName("company ");
        company.setAddress(address2);
        company.setDescription("Description d'company .");
        company.setEmail("dircom-company@company.com");
        company.setEnabled(Boolean.TRUE);
        company.setPhone("+33(0)149009862");
        company.setFax("+33(0)149009800");
        company.setForeignIdentifier(null);
        company.setIconUrl("http://iconurl");
        company.setLogoUrl("http://logourl");
        company.setLegalCategory(CompanyINSEECat.SA);
        company.setWebsite("http://www.company.com");
        company.setSiren("378901946");
        company.setNic("00483");
        company.setApeCode("723Z");
        Person person = new Person(Civility.MR, "Didier", "Dhennin");
        company.addResponsible(person);
        company.setRegistrationCountry(Country.FR);

        companyDepartment1 = new CompanyDepartment();
        companyDepartment1.setId(3L);
        companyDepartment1.setName("Business Unit");
        companyDepartment1.setAddress(address1);
        companyDepartment1.setEmail("bu@company.com");
        companyDepartment1.setDescription("Description de bu.");
        companyDepartment1.setFax("+33(0)000000000");
        companyDepartment1.setPhone("+33(0)000000000");
        companyDepartment1.setSiren(null);
        companyDepartment1.setWebsite(null);

        companyDepartment2 = new CompanyDepartment();
        companyDepartment2.setId(4L);
        companyDepartment2.setName("Département B de bu");
        companyDepartment2.setAddress(address1);
        companyDepartment2.setEmail("bu-b@company.com");
        companyDepartment2.setDescription("Description du département de bu.");
        companyDepartment2.setFax("+33(0)000000000");
        companyDepartment2.setPhone("+33(0)000000000");
        companyDepartment2.setSiren(null);
        companyDepartment2.setWebsite(null);

        employee = new EmployeeProfile();
        employee.setId(5L);
        employee.setExternalId(1234L);
        employee.setName("Employé Bernard Dupond");
        employee.setAddress(address1);
        employee.setEmail("bernard.dupond@company.com");
        employee.setEnabled(Boolean.TRUE);
        employee.setPhone("+33(0)000000000");
        employee.setFax("+33(0)000000000");
        employee.setFunction("Responsable d'application");
        employee.setPrefComMedia(PrefComMedia.EMAIL);
        employee.setUser(user);

        organism = new Organism();
        organism.setId(6L);
        organism.setExternalId(1236L);
        organism.setName("Conseil régional de test");
        organism.setAddress(address3);
        organism.setArticle(Article.LE);
        organism.setDescription("Description du Conseil régional de test.");
        organism.setEmail("contact@test.fr");
        organism.setEnabled(Boolean.TRUE);
        organism.setMember(Boolean.TRUE);
        organism.setPhone("+33(0)000000000");
        organism.setFax("+33(0)000000000");
        organism.setIconUrl("http://iconurl");
        organism.setLogoUrl("http://logourl");
        organism.setLegalCategory(OrganismINSEECat._4_1_40);
        organism.setWebsite("http://www.cr-test.fr");
        organism.setSiren(null);

        organismDepartment0 = new OrganismDepartment();
        organismDepartment0.setId(1L);
        organismDepartment0.setExternalId(1200L);
        organismDepartment0.setName("Organisme de la région - Niveau 0");
        organismDepartment0.setAddress(address3);
        organismDepartment0.setEmail("organisme@test.fr");
        organismDepartment0.setDescription("Description de l'organisme.");
        organismDepartment0.setFax("+33(0)000000000");
        organismDepartment0.setPhone("+33(0)000000000");
        organismDepartment0.setSiren(null);
        organismDepartment0.setWebsite(null);
        organismDepartment0.setParentDepartment(null);

        organismDepartment1 = new OrganismDepartment();
        organismDepartment1.setId(2L);
        organismDepartment1.setExternalId(1220L);
        organismDepartment1.setName("Organisme de la région - Niveau 1");
        organismDepartment1.setAddress(address3);
        organismDepartment1.setEmail("organisme@test.fr");
        organismDepartment1.setDescription("Description de l'organisme.");
        organismDepartment1.setFax("+33(0)000000000");
        organismDepartment1.setPhone("+33(0)000000000");
        organismDepartment1.setSiren(null);
        organismDepartment1.setWebsite(null);
        organismDepartment1.setParentDepartment(organismDepartment0);

        organismDepartment2 = new OrganismDepartment();
        organismDepartment2.setId(3L);
        organismDepartment2.setExternalId(1230L);
        organismDepartment2.setName("Organisme de la région - Niveau 2");
        organismDepartment2.setAddress(address3);
        organismDepartment2.setEmail("organisme@test.fr");
        organismDepartment2.setDescription("Description de l'organisme.");
        organismDepartment2.setFax("+33(0)000000000");
        organismDepartment2.setPhone("+33(0)000000000");
        organismDepartment2.setSiren(null);
        organismDepartment2.setWebsite(null);
        organismDepartment2.setParentDepartment(organismDepartment1);

        organismDepartment3 = new OrganismDepartment();
        organismDepartment3.setId(4L);
        organismDepartment3.setExternalId(1240L);
        organismDepartment3.setName("Organisme de la région - Niveau 3");
        organismDepartment3.setAddress(address3);
        organismDepartment3.setEmail("organisme@test.fr");
        organismDepartment3.setDescription("Description de l'organisme.");
        organismDepartment3.setFax("+33(0)000000000");
        organismDepartment3.setPhone("+33(0)000000000");
        organismDepartment3.setSiren(null);
        organismDepartment3.setWebsite(null);
        organismDepartment3.setParentDepartment(organismDepartment2);

        agent = new AgentProfile();
        agent.setId(8L);
        agent.setExternalId(1240L);
        agent.setName("Agent Bernard Dupond");
        agent.setAddress(address3);
        agent.setEmail("bernard.dupond@test.fr");
        agent.setEnabled(Boolean.TRUE);
        agent.setPhone("+33(0)000000000");
        agent.setFax("+33(0)000000000");
        agent.setElected(Boolean.FALSE);
        agent.setFunction("Agent responsable");
        agent.setPrefComMedia(PrefComMedia.EMAIL);
        agent.setUser(user);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Customer");
        customer.setLabel("Customer label");
        customer.setAdminProfiles(new ArrayList<>());
    }

    @Test
    public void testAgentMarshalling() {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(agent);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testEmployeeMarshalling()  {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(employee);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testOrganismMarshalling() {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(organism);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testCompanyMarshalling()  {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(company);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testOrganismDepartmentMarshalling()  {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(organismDepartment3);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testCompanyDepartmentMarshalling() {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(companyDepartment1);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testCustomerMarshalling() {
        ByteArrayOutputStream baOutputStream = SynchronizationMarshaller.marshalResource(customer);
        Assert.assertNotNull(baOutputStream);
        Assert.assertFalse(baOutputStream.toString().isEmpty());
    }

    @Test
    public void testMarshallAgentProfile() {
        AgentProfile agentProfile = new AgentProfile();
        agentProfile.setId(1L);
        agentProfile.setRoles(new ArrayList<Role>());
        agentProfile.setUser(new User());

        Address address = new Address();
        address.setPostalAddress("t");
        address.setPostalCode("dd");
        address.setCity("city");
        agentProfile.setAddress(address);

        Organism organism = new Organism();
        organism.setApplications(new ArrayList<Application>());
        organism.setDepartments(new ArrayList<OrganismDepartment>());
        organism.setChildStructures(new ArrayList<Structure>());
        organism.setParentStructures(new ArrayList<Structure>());
        organism.setAddress(address);

        agentProfile.setElected(Boolean.FALSE);

        ByteArrayOutputStream c = SynchronizationMarshaller.marshalResource(organism);
        Assert.assertNotNull(c);
        ByteArrayOutputStream c2 = SynchronizationMarshaller.marshalResource(agentProfile);
        Assert.assertNotNull(c2);
    }

    @Test
    public void testMarshallAgentEnabledFalse() {

        AgentProfile agentProfile = new AgentProfile();
        agentProfile.setUser(new User());
        agentProfile.getUser().setEnabled(false);

        agentProfile.setEnabled(false);
        agentProfile.setElected(false);
        agentProfile.setExecutive(false);
        agentProfile.setSubstitute(false);


        // MARSHAL OBJECT
        ByteArrayOutputStream c = SynchronizationMarshaller.marshalResource(agentProfile);
        String xmlMarshal = c.toString();
        Assert.assertTrue(xmlMarshal.contains("<user><enabled>false</enabled></user>"));
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<agentProfile><enabled>false</enabled><roles/><user><enabled>false</enabled></user>" +
                        "<elected>false</elected><executive>false</executive><representative>false</representative><substitute>false</substitute></agentProfile>",
                xmlMarshal);

        // UNMARSHAL THE MarShal Object
        AgentProfile agentUnmarshall = (AgentProfile) SynchronizationMarshaller.unmarshalResource(xmlMarshal);
        Assert.assertEquals(false, agentUnmarshall.getUser().isEnabled());
        Assert.assertEquals(false, agentUnmarshall.getElected());
        Assert.assertEquals(false, agentUnmarshall.getExecutive());
        Assert.assertEquals(false, agentUnmarshall.getSubstitute());
    }
}
