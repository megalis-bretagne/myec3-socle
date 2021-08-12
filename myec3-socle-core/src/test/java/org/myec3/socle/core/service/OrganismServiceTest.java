package org.myec3.socle.core.service;

import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.Assert.*;

@Transactional
@SqlGroup({
		// WITH CUSTOMER
		@Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
		// organism
		@Sql(value = {"classpath:/db/test/organism/organism_1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
public class OrganismServiceTest extends AbstractDbSocleUnitTest {

	public static final String APP_NAME_1 = "Application 1 " + Calendar.getInstance().getTimeInMillis();
	public static final String APP_NAME_2 = "Application 2 " + Calendar.getInstance().getTimeInMillis();
	public static final String ORGANISM_CITY = "Cesson Cevignee";
	public static final String ORGANISM_POSTAL_CODE = "35510";
	public static final String ORGANISM_NAME_1 = "Test Organism " + Calendar.getInstance().getTimeInMillis();
	public static final String ORGANISM_NAME_2 = "Test Create Organism " + Calendar.getInstance().getTimeInMillis();
	public static final String ORGANISM_DEPARTMENT_NAME = "Test Root Department " + Calendar.getInstance().getTimeInMillis();
	public static final String ORGANISM_ACRONYM = "a0y";
	public static final String ORGANISM_ACRONYM_2 = "1f5";
	public static final String ORGANISM_ACRONYM_3 = "9d9";
	public static final String ORGANISM_LABEL = "TEST U1";
	public static final String ORGANISM_SIREN = "150248938";


	@Autowired
	private OrganismService organismService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private StructureTypeService structureTypeService;

    @Autowired
    private OrganismStatusService organismStatusService;

	@Test
	public void testPersistWithoutAcronymAndDepartment() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism organism = organismService
				.findOne(1L);

		Organism newOrganism = new Organism();

		Application newApplication1 = new Application();
		newApplication1.setName(APP_NAME_1);
		newApplication1.setExternalId(new Long(1245));
		newApplication1.setUrl("http://urlsdm");
		applicationService.create(newApplication1);

		Application newApplication2 = new Application();
		newApplication2.setName(APP_NAME_2);
		newApplication2.setExternalId(new Long(1246));
		newApplication2.setUrl("http://urlged");
		 applicationService.create(newApplication2);

		List<Application> listApplication = new ArrayList<Application>();
		listApplication.add(newApplication1);
		listApplication.add(newApplication2);

		newOrganism.setName(ORGANISM_NAME_2);
		newOrganism.setLabel("OrganismLabel");
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setApplications(listApplication);
		newOrganism.setCustomer(customer);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		Organism foundResource = organismService
				.findByName(ORGANISM_NAME_2);
		assertNotNull("organism should not be null", foundResource);
		assertEquals("invalid organism name", newOrganism.getName(),
				foundResource.getName());
		assertNotNull("acronym should not be null", organism.getAcronym());
		OrganismDepartment root = organismDepartmentService
				.findRootOrganismDepartment(foundResource);
		assertNotNull("root department should not be null", root);

		organismDepartmentService.delete(organismDepartmentService
				.findRootOrganismDepartment(newOrganism));
		organismService.delete(newOrganism);
		foundResource = organismService.findByName(newOrganism.getName());
		assertNull("organism should be null", foundResource);
	}

	@Test
	public void testPersistWithAcronymAndDepartment() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism organism = organismService
				.findOne(1L);

		Organism newOrganism = new Organism();

		Application newApplication1 = new Application();
		newApplication1.setName(APP_NAME_1);
		newApplication1.setExternalId(new Long(1245));
		newApplication1.setUrl("http://urlsdm");

		applicationService.create(newApplication1);

		Application newApplication2 = new Application();
		newApplication2.setName(APP_NAME_2);
		newApplication2.setExternalId(new Long(1246));
		newApplication2.setUrl("http://urlged");

		applicationService.create(newApplication2);

		List<Application> listApplication = new ArrayList<Application>();
		listApplication.add(newApplication1);
		listApplication.add(newApplication2);

		newOrganism.setName(ORGANISM_NAME_2);
		newOrganism.setLabel("OrganismLabel");
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setApplications(listApplication);
		newOrganism.setCustomer(customer);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		OrganismDepartment organismDepartment = new OrganismDepartment();
		organismDepartment
				.setName(ORGANISM_DEPARTMENT_NAME);
		organismDepartment
				.setAcronym(ORGANISM_ACRONYM_3);
		organismDepartment.setDescription("");
		organismDepartment.setEmail("test-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organismDepartment.setPhone("");
		organismDepartment.setAddress(add);
		organismDepartment.setParentDepartment(null);
		organismDepartment.setOrganism(newOrganism);

		newOrganism.addDepartment(organismDepartment);

		organismService.create(newOrganism);

		Organism foundResource = organismService
				.findByName(ORGANISM_NAME_2);
		assertNotNull("organism should not be null", foundResource);
		assertEquals("invalid organism name", newOrganism.getName(),
				foundResource.getName());
		assertNotNull("acronym should not be null", organism.getAcronym());
		OrganismDepartment root = organismDepartmentService
				.findRootOrganismDepartment(foundResource);
		assertNotNull("root department should not be null", root);
		assertEquals("departments should be equals",
				ORGANISM_DEPARTMENT_NAME,
				root.getName());

		organismDepartmentService.delete(organismDepartmentService
				.findRootOrganismDepartment(newOrganism));
		organismService.delete(newOrganism);
		foundResource = organismService.findByName(newOrganism.getName());
		assertNull("organism should be null", foundResource);
	}

	@Test
	public void testMerge() throws Exception {
		Organism foundResource = organismService
				.findByName("TEST U");
		foundResource.setName("Modified Test Organism"
				+ Calendar.getInstance().getTimeInMillis());
		organismService.update(foundResource);

		Organism mergedResource = organismService.findByName(foundResource
				.getName());
		assertNotNull(mergedResource);
		assertEquals(foundResource.getName(), mergedResource.getName());
	}


	@Test
	public void testFindAll() throws Exception {
		List<Organism> organismList = organismService.findAll();
		assertEquals(true, organismList.size() >= 1);
	}

	@Test
	public void testFindByName() throws Exception {
		Organism foundOrganism = organismService
				.findByName("TEST U");
		assertNotNull(foundOrganism);
		assertEquals("TEST U",
				foundOrganism.getName());
	}

	@Test
	public void testFindAllApplicationInOrganism() throws Exception {
		List<Application> applicationList = organismService.findByName(
				"TEST U").getApplications();
		assertNotNull(applicationList);
		assertEquals(1, applicationList.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaNullLabel() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel("OrganismLabel 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(null,
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaPartialLabel() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel("PREFIX" + ORGANISM_LABEL
				+ "SUFFIX");
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(123L);
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test u",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());

	}

	@Test
	public void testFindAllOrganismByCriteriaNullSiren() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren("123456456");
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test u", null,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaSuffixSiren() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN + "123");
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test u",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaPrefixSiren() throws Exception {
		Customer customer = customerService
				.findOne(1l);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren("123" + ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test U",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 1", 1, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaNullPostalCode() throws Exception {
		Customer customer = customerService
				.findOne(1l);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode("13000");
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test u",
				ORGANISM_SIREN, null,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaPrefixPostalCode() {
		Customer customer = customerService
				.findOne(1l);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode("13" + ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test u",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 1", 1, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaSuffixPostalCode() {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE + "13");
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		//List<Organism> organisms = this.organismService.findAll();
		List<Organism> organisms = this.organismService.findAllByCriteria(
				"test U",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaNullCity() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity("Paris");
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"Test U",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE, null);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaPrefixCity() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity("C" + ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"test u",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 1", 1, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaSuffixCity() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		 
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY + "C");
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"test U",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 1", 1, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaAll() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name 2"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		 
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(
				"test u",
				ORGANISM_SIREN,
				ORGANISM_POSTAL_CODE,
				ORGANISM_CITY);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());
		assertEquals("organisms size should be exactly 2", 2, organisms.size());
	}

	@Test
	public void testFindAllOrganismByCriteriaAllNull() throws Exception {
		Customer customer = customerService
				.findOne(1L);
		Organism newOrganism = new Organism();

		newOrganism.setName("Organism Name"
				+ Calendar.getInstance().getTimeInMillis());
		newOrganism.setLabel(ORGANISM_LABEL);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setSiren(ORGANISM_SIREN);
		newOrganism.setCustomer(customer);

		StructureType structureType = new StructureType();
		structureType.setValue(StructureTypeValue.ORGANISM);
		
		newOrganism.setStructureType(structureType);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		List<Organism> organisms = this.organismService.findAllByCriteria(null,
				null, null, null);
		assertNotNull("organisms should not be null", organisms);
		assertFalse("organisms should not be empty", organisms.isEmpty());

		List<Organism> all = this.organismService.findAll();

		assertEquals("organisms should contain all organisms", organisms, all);
	}

	@Test
	public void testEmptyFindBySiren() throws Exception {
		Organism foundOrganism = this.organismService.findBySiren("132456");
		assertNull("organism should be null", foundOrganism);
	}

	@Test
	public void testFindBySiren() throws Exception {
		Organism foundOrganism = this.organismService
				.findBySiren(ORGANISM_SIREN);
		assertNotNull("organism should be null", foundOrganism);
		assertEquals("organism name is invalid",
				"TEST U",
				foundOrganism.getName());
	}

	@Test
	public void testIsSirenNull() throws Exception {
		Boolean result = this.organismService.isSirenValid(null);
		assertFalse("Siren should be invalid", result);
	}

	@Test
	public void testIsSirenValid() throws Exception {
		Boolean result = this.organismService
				.isSirenValid(ORGANISM_SIREN);
		assertTrue("Siren should be valid", result);
	}

	@Test
	public void testIsSirenInvalid() throws Exception {
		Boolean result = this.organismService.isSirenValid("123456");
		assertFalse("Siren should be invalid", result);
	}

	@Test
	public void testEmptyFindByAcronym() throws Exception {
		Organism foundOrganism = this.organismService.findByAcronym("132");
		assertNull("organism should be null", foundOrganism);
	}

	@Test
	public void testFindByAcronym() throws Exception {
		Organism foundOrganism = this.organismService
				.findByAcronym(ORGANISM_ACRONYM);
		assertNotNull("organism should be null", foundOrganism);
		assertEquals("organism name is invalid",
				"TEST U",
				foundOrganism.getName());
	}

	@Test
	public void testPopulateCollections() throws Exception {
		Customer customer = customerService
				.findOne(1L);

		Organism newOrganism = new Organism();

		Application newApplication1 = new Application();
		newApplication1.setName(APP_NAME_1);
		newApplication1.setExternalId(new Long(1245));
		newApplication1.setUrl("http://urlsdm");

		applicationService.create(newApplication1);

		Application newApplication2 = new Application();
		newApplication2.setName(APP_NAME_2);
		newApplication2.setExternalId(new Long(1246));
		newApplication2.setUrl("http://urlged");

		applicationService.create(newApplication2);

		List<Application> listApplication = new ArrayList<Application>();
		listApplication.add(newApplication1);
		listApplication.add(newApplication2);

		newOrganism.setName(ORGANISM_NAME_2);
		newOrganism.setLabel("OrganismLabel");
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(new Long(123));
		newOrganism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setSiren("111111111");
		newOrganism.setAcronym(ORGANISM_ACRONYM_2);
		newOrganism.setApplications(listApplication);
		newOrganism.setCustomer(customer);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode(ORGANISM_POSTAL_CODE);
		add.setCity(ORGANISM_CITY);
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		OrganismDepartment organismDepartment = new OrganismDepartment();
		organismDepartment
				.setName(ORGANISM_DEPARTMENT_NAME);
		organismDepartment.setAcronym("q7a");
		organismDepartment.setDescription("");
		organismDepartment.setEmail("test-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organismDepartment.setPhone("");
		organismDepartment.setAddress(add);
		organismDepartment.setSiren("11111111");
		organismDepartment.setParentDepartment(null);
		organismDepartment.setOrganism(newOrganism);

		newOrganism.addDepartment(organismDepartment);

		organismService.create(newOrganism);

		Organism foundOrganism = this.organismService
				.findByName(ORGANISM_NAME_2);
		assertNotNull(foundOrganism);

		HashSet<OrganismStatus> newListeOrganismStatus = new HashSet<OrganismStatus>();
		OrganismStatus newOrganismStatus = new OrganismStatus(OrganismMemberStatus.ADHERENT, new Date(), foundOrganism);
		newListeOrganismStatus.add(newOrganismStatus);

		foundOrganism.clearOrganismStatus();
		foundOrganism.getOrganismStatus().addAll(newListeOrganismStatus);

		foundOrganism.setApplications(new ArrayList<Application>());
		foundOrganism.setDepartments(new ArrayList<OrganismDepartment>());
		foundOrganism.setDepartments(new ArrayList<OrganismDepartment>());

		this.organismService.populateCollections(foundOrganism);
		assertEquals(0, foundOrganism.getApplications().size());
		assertEquals(1, foundOrganism.getDepartments().size());
		assertEquals(1, foundOrganism.getOrganismStatus().size());

		organismDepartmentService.delete(organismDepartmentService
				.findRootOrganismDepartment(newOrganism));

		organismService.delete(newOrganism);
		Organism foundOrganismResource = organismService.findByName(newOrganism
				.getName());
		assertNull(foundOrganismResource);

		OrganismDepartment foundOrganismDepartmentResource = organismDepartmentService
				.findByName(organismDepartment.getName());
		assertNull(foundOrganismDepartmentResource);
	}
}
