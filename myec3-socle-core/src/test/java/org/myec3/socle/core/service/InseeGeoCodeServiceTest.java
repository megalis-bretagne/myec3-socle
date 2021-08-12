/**
 * 
 */
package org.myec3.socle.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class InseeGeoCodeServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private InseeGeoCodeService inseeGeoCodeService;

	private InseeGeoCode inseeGeoCode;

	private static final String POSTAL_CODE_1 = "72000";
	private static final String TOWN_1 = "LE MANS";
	private static final String INSEE_CODE = "72181";
	private static final String NEW_INSEE_CODE = "72195";
	private static final String INSEE_CODE_COM = "181";
	private static final String INSEE_CODE_DEP = "72";
	private static final Long INSEE_CODE_REG = Long.valueOf("52");

	@Before
	public void setUp() {

		inseeGeoCode = new InseeGeoCode();
		inseeGeoCode.setName(INSEE_CODE);
		inseeGeoCode.setLabel(TOWN_1);
		inseeGeoCode.setInseeCode(INSEE_CODE);
		inseeGeoCode.setExternalId(new Long(123));
		inseeGeoCode.setCom(INSEE_CODE_COM);
		inseeGeoCode.setDep(INSEE_CODE_DEP);
		inseeGeoCode.setReg(INSEE_CODE_REG);
		inseeGeoCode.setPostalCode(POSTAL_CODE_1);

		inseeGeoCodeService.create(inseeGeoCode);
	}

	@After
	public void tearDown() {
		if (null != this.inseeGeoCode) {
			inseeGeoCodeService.delete(inseeGeoCode);
		}
	}

	@Test
	public void testPersist() throws Exception {
		InseeGeoCode newInseeGeoCode = new InseeGeoCode();

		newInseeGeoCode.setName(NEW_INSEE_CODE);
		newInseeGeoCode.setLabel("z9z");
		newInseeGeoCode.setInseeCode(NEW_INSEE_CODE);
		newInseeGeoCode.setExternalId(new Long(123));
		newInseeGeoCode.setCom(INSEE_CODE_COM);
		newInseeGeoCode.setDep(INSEE_CODE_DEP);
		newInseeGeoCode.setReg(INSEE_CODE_REG);
		newInseeGeoCode.setPostalCode("24111");

		inseeGeoCodeService.create(newInseeGeoCode);

		Resource foundResource = inseeGeoCodeService.findByName(NEW_INSEE_CODE);
		assertNotNull(foundResource);
		assertEquals(newInseeGeoCode.getName(), foundResource.getName());

		inseeGeoCodeService.delete(newInseeGeoCode);
		foundResource = inseeGeoCodeService.findByName(newInseeGeoCode
				.getName());
		assertNull(foundResource);
	}

	@Test
	public void testMerge() throws Exception {
		InseeGeoCode foundResource = inseeGeoCodeService.findByName(INSEE_CODE);
		foundResource.setName("Modified Test INSEE code "
				+ Calendar.getInstance().getTimeInMillis());
		inseeGeoCodeService.update(foundResource);

		InseeGeoCode mergedResource = inseeGeoCodeService
				.findByName(foundResource.getName());
		assertNotNull(mergedResource);
		assertEquals(foundResource.getName(), mergedResource.getName());
	}

	@Test
	public void testRemove() throws Exception {
		InseeGeoCode foundInseeGeoCode = inseeGeoCodeService
				.findByName(this.inseeGeoCode.getName());
		inseeGeoCodeService.delete(foundInseeGeoCode);

		this.inseeGeoCode = inseeGeoCodeService.findByName(foundInseeGeoCode
				.getName());
		assertNull(this.inseeGeoCode);
	}

	@Test
	public void testRemoveById() throws Exception {
		InseeGeoCode foundInseeGeoCode = inseeGeoCodeService
				.findByName(this.inseeGeoCode.getName());
		inseeGeoCodeService.delete(foundInseeGeoCode);

		this.inseeGeoCode = inseeGeoCodeService.findByName(foundInseeGeoCode
				.getName());
		assertNull(this.inseeGeoCode);
	}

	@Test
	public void testFindAll() throws Exception {
		List<InseeGeoCode> inseeGeoCode = inseeGeoCodeService.findAll();
		assertEquals(true, inseeGeoCode.size() >= 1);
	}

	@Test
	public void testFindByName() throws Exception {
		InseeGeoCode foundInseeGeoCode = inseeGeoCodeService
				.findByName(this.inseeGeoCode.getName());
		assertNotNull(foundInseeGeoCode);
		assertEquals(this.inseeGeoCode.getName(), foundInseeGeoCode.getName());
	}

	@Test
	public void testFindByInseeCode() throws Exception {
		InseeGeoCode foundInseeGeoCode = inseeGeoCodeService
				.findByInseeCode(this.inseeGeoCode.getInseeCode());
		assertNotNull(foundInseeGeoCode);
		assertEquals(this.inseeGeoCode.getLabel().toUpperCase(),
				foundInseeGeoCode.getLabel().toUpperCase());
	}

}
