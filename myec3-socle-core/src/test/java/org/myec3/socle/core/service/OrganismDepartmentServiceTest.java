package org.myec3.socle.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Article;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.OrganismINSEECat;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for
 * {@link org.myec3.socle.core.service.impl.OrganismDepartmentServiceImpl}
 * 
*
 */
@Transactional
public class OrganismDepartmentServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	private OrganismService organismService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AcronymsListService acronymsListService;

	@Autowired
	private StructureTypeService structureTypeService;

	private OrganismDepartment organismDepartment;
	private Organism organism;
	private Customer customer;

	public static final String ACRONYM = "d5f";
	public static final String ACRONYM_1 = "w7w";
	public static final String ACRONYM_2 = "q9a";
	private static final String ORGANISM_NAME_1 = "Test Organism 1 "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_NAME_2 = "Test Organism 2 "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_DEPARTMENT_NAME_1 = "Organisme de la région "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String ORGANISM_DEPARTMENT_NAME_2 = "Organisme 2 de la région "
			+ Calendar.getInstance().getTimeInMillis();
	private static final String CUSTOMER_NAME = "Test Customer OrganismDepartmentServiceTest";

	@Before
	public void setUp() {

		AcronymsList acronym0 = new AcronymsList();
		acronym0.setValue(ACRONYM);
		acronym0.setAvailable(true);
		acronymsListService.create(acronym0);

		AcronymsList acronym1 = new AcronymsList();
		acronym1.setValue(ACRONYM_1);
		acronym1.setAvailable(true);
		acronymsListService.create(acronym1);

		AcronymsList acronym2 = new AcronymsList();
		acronym2.setValue(ACRONYM_2);
		acronym2.setAvailable(true);
		acronymsListService.create(acronym2);

		// Create StructureType
		StructureType organismType = new StructureType();
		organismType.setValue(StructureTypeValue.ORGANISM);
		this.structureTypeService.create(organismType);

		StructureType companyType = new StructureType();
		companyType.setValue(StructureTypeValue.COMPANY);
		this.structureTypeService.create(companyType);

		organismDepartment = new OrganismDepartment();

		organismDepartment.setName(ORGANISM_DEPARTMENT_NAME_2);
		organismDepartment.setLabel("OrganismDptLabel");

		Address address = new Address();
		address.setCity("");
		address.setPostalAddress("");
		address.setPostalCode("");
		address.setCountry(Country.FR);

		organismDepartment.setAcronym("q7a");
		organismDepartment.setDescription("");
		organismDepartment.setEmail("test-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organismDepartment.setPhone("");
		organismDepartment.setAddress(address);
		organismDepartment.setParentDepartment(null);

		// Create customer
		customer = new Customer();
		customer.setName(CUSTOMER_NAME);
		customer.setLabel("label");
		customerService.create(customer);

		// test customer creation
		customer = customerService.findByName(CUSTOMER_NAME);
		assertNotNull("Customer should not be null", customer);
		assertEquals("Customer Name should be equals to " + CUSTOMER_NAME,
				CUSTOMER_NAME, customer.getName());

		organism = new Organism();
		organism.setName(ORGANISM_NAME_1);
		organism.setLabel("OrganismLabel");
		organism.setAcronym("q9a");
		organism.setDescription("OrganismDescription");
		organism.setEmail("test-create-organism"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		organism.setPhone("0000000000");
		organism.setArticle(Article.LE);
		organism.setMember(true);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode("69000");
		add.setCity("Lyon");
		add.setCountry(Country.FR);

		organism.setAddress(add);
		organism.setLegalCategory(OrganismINSEECat._4_1);
		organism.setCustomer(customer);

		organismService.create(organism);

		organismDepartment.setOrganism(organismService
				.findByName(ORGANISM_NAME_1));
		organismDepartmentService.create(organismDepartment);
	}

	@After
	public void tearDown() {
		if (null != this.organismDepartment) {
			organismDepartmentService.delete(organismDepartment);
		}
		if (null != this.organism) {
			organismDepartmentService.delete(organismDepartmentService
					.findRootOrganismDepartment(organism));
			organismService.delete(organism);
		}
	}

	@Test
	public void testPersist() throws Exception {
		OrganismDepartment newOrganismDepartment = new OrganismDepartment();

		newOrganismDepartment.setName(ORGANISM_DEPARTMENT_NAME_1);
		newOrganismDepartment.setLabel("OrganismLabel");

		Address address = new Address();
		address.setCity("");
		address.setPostalAddress("");
		address.setPostalCode("");
		address.setCountry(Country.FR);

		newOrganismDepartment.setAcronym(ACRONYM_1);
		newOrganismDepartment.setDescription("");
		newOrganismDepartment.setEmail("test-organism-dpt"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganismDepartment.setPhone("");
		newOrganismDepartment.setAddress(address);
		newOrganismDepartment.setParentDepartment(null);

		Organism newOrganism = new Organism();

		newOrganism.setName(ORGANISM_NAME_2);
		newOrganism.setLabel("OrganismLabel");
		newOrganism.setAcronym(ACRONYM);
		newOrganism.setDescription("OrganismDescription");
		newOrganism.setExternalId(123L);
		newOrganism.setEmail("test-create-organism2"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		newOrganism.setPhone("0000000000");
		newOrganism.setArticle(Article.LE);
		newOrganism.setMember(true);
		newOrganism.setCustomer(customer);

		Address add = new Address();
		add.setPostalAddress("Address");
		add.setPostalCode("69000");
		add.setCity("Lyon");
		add.setCountry(Country.FR);

		newOrganism.setAddress(add);
		newOrganism.setLegalCategory(OrganismINSEECat._4_1);

		organismService.create(newOrganism);

		newOrganismDepartment.setOrganism(organismService
				.findByName(ORGANISM_NAME_2));
		organismDepartmentService.create(newOrganismDepartment);

		Resource foundResource = organismDepartmentService
				.findByName(ORGANISM_DEPARTMENT_NAME_1);
		assertNotNull(foundResource);
		assertEquals(ORGANISM_DEPARTMENT_NAME_1, foundResource.getName());

		organismDepartmentService.delete(newOrganismDepartment);
		foundResource = organismDepartmentService
				.findByName(ORGANISM_DEPARTMENT_NAME_1);
		assertNull(foundResource);

		organismDepartmentService.delete(organismDepartmentService
				.findRootOrganismDepartment(newOrganism));
		organismService.delete(newOrganism);
		foundResource = organismService.findByName(ORGANISM_NAME_2);
		assertNull(foundResource);
	}

	@Test
	public void testMerge() throws Exception {
		OrganismDepartment foundResource = organismDepartmentService
				.findByName(this.organismDepartment.getName());
		foundResource.setName("Modified Test Organism");
		organismDepartmentService.update(foundResource);

		OrganismDepartment mergedResource = organismDepartmentService
				.findByName(foundResource.getName());
		assertNotNull(mergedResource);
		assertEquals(foundResource.getName(), mergedResource.getName());
	}

	@Test
	public void testRemove() throws Exception {
		OrganismDepartment foundOrganism = organismDepartmentService
				.findByName(this.organismDepartment.getName());
		organismDepartmentService.delete(foundOrganism);

		this.organismDepartment = organismDepartmentService
				.findByName(foundOrganism.getName());
		assertNull(this.organismDepartment);
	}

	@Test
	public void testRemoveById() throws Exception {
		OrganismDepartment foundOrganism = organismDepartmentService
				.findByName(this.organismDepartment.getName());
		organismDepartmentService.delete(foundOrganism);

		this.organismDepartment = organismDepartmentService
				.findByName(foundOrganism.getName());
		assertNull(this.organismDepartment);
	}

	@Test
	public void testFindByName() throws Exception {
		OrganismDepartment foundOrganism = organismDepartmentService
				.findByName(this.organismDepartment.getName());
		assertNotNull(foundOrganism);
		assertEquals(this.organismDepartment.getName(), foundOrganism.getName());
	}

	@Test
	public void testFindAllByOrganismId() throws Exception {
		Organism organism = organismService.findByName(ORGANISM_NAME_1);
		List<OrganismDepartment> foundOrganismDpt = organismDepartmentService
				.findAllDepartmentByOrganism(organism);
		assertEquals(true, foundOrganismDpt.size() >= 1);
	}
}
