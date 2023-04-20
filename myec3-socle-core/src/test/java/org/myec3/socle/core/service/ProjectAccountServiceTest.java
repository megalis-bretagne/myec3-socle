package org.myec3.socle.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Test class for
 * {@link org.myec3.socle.core.service.impl.ProjectAccountServiceImpl}
 * 
*
 */
@Transactional
public class ProjectAccountServiceTest extends AbstractDbSocleUnitTest {

	@Autowired
	private ProjectAccountService projectAccountService;

	@Autowired
	private UserService userService;

	private static ProjectAccount projectAccount;

	private static final String PROJECT_ACCOUNT_NAME = "Test projectAccount"
			+ Calendar.getInstance().getTimeInMillis();

	@Before
	public void testInit() throws Exception {
		// ProjectAccount
		projectAccount = new ProjectAccount();
		projectAccount.setExternalId(96L);
		projectAccount.setName(PROJECT_ACCOUNT_NAME);
		projectAccount.setLabel(PROJECT_ACCOUNT_NAME);
		projectAccount.setEmail("test-email"
				+ Calendar.getInstance().getTimeInMillis() + "@test.fr");
		projectAccount.setEnabled(Boolean.TRUE);
		projectAccount.setLogin("test"
				+ Calendar.getInstance().getTimeInMillis());
	}

	@Test
	public void createProjectAccount() {
		projectAccountService.create(projectAccount);

		// get projectAccount created
		ProjectAccount foundProjectAccount = projectAccountService
				.findByName(projectAccount.getName());
		assertNotNull(foundProjectAccount);

		// Get user created
		User user = userService.findByUsername(foundProjectAccount.getLogin());
		assertNotNull(user);
		assertEquals(projectAccount.getLogin(), user.getUsername());
		assertTrue(user.isEnabled());
	}

	@Test
	public void updateProjectAccount() {
		projectAccountService.create(projectAccount);

		ProjectAccount foundProjectAccount = projectAccountService
				.findByName(projectAccount.getName());
		assertNotNull(foundProjectAccount);

		foundProjectAccount.setLogin("newLogin"
				+ Calendar.getInstance().getTimeInMillis());

		foundProjectAccount.getUser().setFirstname("new First Name");

		ProjectAccount updatedProject = projectAccountService.update(foundProjectAccount);
		Assert.assertEquals(foundProjectAccount.getLogin(), updatedProject.getLogin());

	}
}
