/**
 * 
 */
package org.myec3.socle.core.service;

import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.SviProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.*;


@Transactional
@SqlGroup({
		@Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
public class SviProfileTest extends AbstractDbSocleUnitTest {

	@Autowired
	private SviProfileService sviProfileService;

	private SviProfile sviProfile;

	@Before
	public void setUp() throws Exception {
		sviProfile = this.sviProfileService.findOne(1L);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.SviProfileServiceImpl#create(org.myec3.socle.core.domain.model.SviProfile)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullCreate() {
		this.sviProfileService.create(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.SviProfileServiceImpl#create(org.myec3.socle.core.domain.model.SviProfile)}
	 * .
	 */
	@Test
	public void testCreate() {
		SviProfile newProfile = new SviProfile();
		this.sviProfileService.create(newProfile);

		SviProfile foundProfile = this.sviProfileService.findOne(newProfile.getId());
		assertNotNull(foundProfile);
		assertEquals(foundProfile.getId(), foundProfile.getId());

		this.sviProfileService.delete(newProfile);
		foundProfile = this.sviProfileService.findOne(newProfile.getId());
		assertNull(foundProfile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullUpdate() {
		this.sviProfileService.update(null);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.SviProfileServiceImpl#update(org.myec3.socle.core.domain.model.SviProfile)}
	 * .
	 */
	@Test
	public void testUpdate() {
		this.sviProfileService.update(sviProfile);

		SviProfile mergedProfile = sviProfileService.findOne(sviProfile.getId());
		assertNotNull(mergedProfile);
		assertEquals(mergedProfile.getId(), sviProfile.getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteNullSviProfile() {
		SviProfile newProfile = null;
		sviProfileService.delete(newProfile);
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.core.service.impl.SviProfileServiceImpl#delete(org.myec3.socle.core.domain.model.SviProfile)}
	 * .
	 */
	@Test
	public void testDeleteSviProfile() {
		sviProfileService.delete(sviProfile);
		this.sviProfile = sviProfileService.findOne(sviProfile.getId());

		assertNull(this.sviProfile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteNullLong() { ;
		sviProfileService.delete(null);
	}

	@Test
	public void testDeleteLong() {
		sviProfileService.delete(sviProfile);
		this.sviProfile = sviProfileService.findOne(sviProfile.getId());

		assertNull(this.sviProfile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByNullId() {
		this.sviProfileService.findOne(null);
	}

	@Test
	public void testEmptyFindById() {
		this.sviProfile = this.sviProfileService.findOne(0L);

		assertNull("sviProfile should be null", this.sviProfile);
	}

	@Test
	public void testFindById() {
		SviProfile foundSviProfile = this.sviProfileService.findOne(this.sviProfile.getId());

		assertNotNull("sviProfile should not be null", foundSviProfile);
		assertEquals("foundSviProfile should be equals to sviProfile", foundSviProfile, this.sviProfile);
	}


	@Test
	public void testFindAll() {
		List<SviProfile> sviProfiles = this.sviProfileService.findAll();

		assertNotNull("sviProfiles should not be null", sviProfiles);
		assertFalse("sviProfiles should be equals to sviProfile", sviProfiles.isEmpty());
		assertTrue("sviProfiles size should be greater than 1", sviProfiles.size() >= 1);
	}

}
