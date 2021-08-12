package org.myec3.socle.synchro.core.service;

import org.junit.Test;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.synchro.core.AbstractDbUnitTest;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@SqlGroup({
		@Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(value = {"classpath:/db/test/synchronizationQueue/insert_sync.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@Transactional
public class SynchronizationSubscriptionServiceTest extends AbstractDbUnitTest {

	@Autowired
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Autowired
	private ApplicationService applicationService;

	@Test
	public void testCreateWithNoCustomer() throws Exception {
		Application application2 = new Application();
		application2.setName("appli2 test");
		application2.setLabel("application 2 label");
		application2.setUrl("http://url.fr");
		applicationService.create(application2);

		SynchronizationSubscription newSubcription = new SynchronizationSubscription();
		newSubcription.setUri("http://url/sync/agent/");
		newSubcription.setResourceLabel(ResourceType.AGENT_PROFILE);
		newSubcription.setSynchronizationFilter(new SynchronizationFilter());
		newSubcription.getSynchronizationFilter().setId(1L);
		newSubcription.setApplication(application2);

		synchronizationSubscriptionService.create(newSubcription);

		SynchronizationSubscription foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(newSubcription.getId());
		assertNotNull(foundSynchronizationSubscription);
		assertEquals(foundSynchronizationSubscription.getResourceLabel(),
				ResourceType.AGENT_PROFILE);
	}

	@Test
	public void testCreate() throws Exception {
		Application application2 = new Application();
		application2.setName("appli2 test");
		application2.setLabel("application 2 label");
		application2.setUrl("http://url.fr");
		applicationService.create(application2);

		SynchronizationSubscription newSubcription = new SynchronizationSubscription();
		newSubcription.setUri("http://url/sync/agent/");
		newSubcription.setResourceLabel(ResourceType.AGENT_PROFILE);
		newSubcription.setSynchronizationFilter(new SynchronizationFilter());
		newSubcription.getSynchronizationFilter().setId(1L);
		newSubcription.setApplication(application2);

		synchronizationSubscriptionService.create(newSubcription);

		SynchronizationSubscription foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(newSubcription.getId());
		assertNotNull(foundSynchronizationSubscription);
		assertEquals(foundSynchronizationSubscription.getResourceLabel(),
				ResourceType.AGENT_PROFILE);
	}

	@Test
	public void testUpdate() throws Exception {

		// Retrieve synchronization subsciption created during @before method
		SynchronizationSubscription foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(10L);

		assertNotNull("SynchronizationSubscription should not be null",
				foundSynchronizationSubscription);

		// Change synchronization subscription resource label
		foundSynchronizationSubscription.setResourceLabel(ResourceType.COMPANY);

		// Update synchronization subsciption
		synchronizationSubscriptionService
				.update(foundSynchronizationSubscription);

		// Check if new value is updated
		foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(10L);

		assertNotNull("SynchronizationSubscription should not be null",
				foundSynchronizationSubscription);

		assertEquals(
				"Synchronization subscription resource label should be equals at COMPANY",
				ResourceType.COMPANY,
				foundSynchronizationSubscription.getResourceLabel());
	}


	@Test
	public void testDelete() throws Exception {
		// Retrieve synchronization subsciption created during @before method
		SynchronizationSubscription foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(10L);

		assertNotNull("SynchronizationSubscription should not be null",
				foundSynchronizationSubscription);

		// Delete synchronization subsciption
		synchronizationSubscriptionService
				.delete(foundSynchronizationSubscription);

		// Check if synchronization subscription has been deleted
		foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(10L);

		assertNull("SynchronizationSubscription should be null",
				foundSynchronizationSubscription);
	}

	@Test
	public void testDeleteById() throws Exception {
		// Retrieve synchronization subsciption created during @before method
		SynchronizationSubscription foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(11L);

		assertNotNull("SynchronizationSubscription should not be null",
				foundSynchronizationSubscription);

		// Delete synchronization subsciption
		synchronizationSubscriptionService
				.delete(foundSynchronizationSubscription);

		// Check if synchronization subscription has been deleted
		foundSynchronizationSubscription = synchronizationSubscriptionService
				.findOne(11L);

		assertNull("SynchronizationSubscription should be null",
				foundSynchronizationSubscription);
	}

	@Test
	public void testFindAllByApplicationId() throws Exception {
		List<SynchronizationSubscription> synchronizationSubscriptionList = synchronizationSubscriptionService
				.findAllByApplicationId(2L);
		assertEquals("List size should be equals at 1", 1,
				synchronizationSubscriptionList.size());
	}


	@Test
	public void testFindAllByResourceLabel() throws Exception {
		SynchronizationSubscription newSubcription2 = new SynchronizationSubscription();
		newSubcription2.setUri("http://uri/sync/organism/");
		newSubcription2.setResourceLabel(ResourceType.ORGANISM);
		newSubcription2.setApplication(new Application());
		newSubcription2.getApplication().setId(2L);
		newSubcription2.setSynchronizationFilter(new SynchronizationFilter());
		newSubcription2.getSynchronizationFilter().setId(1L);

		synchronizationSubscriptionService.create(newSubcription2);

		List<SynchronizationSubscription> synchronizationSubscriptionList = synchronizationSubscriptionService
				.findAllByResourceLabel(ResourceType.ORGANISM);
		assertFalse("Synchronization subscription list should not be empty",
				synchronizationSubscriptionList.isEmpty());

		synchronizationSubscriptionList = synchronizationSubscriptionService
				.findAllByResourceLabel(ResourceType.AGENT_PROFILE);
		assertFalse("Synchronization subscription list should not be empty",
				synchronizationSubscriptionList.isEmpty());

		Iterator<SynchronizationSubscription> itr = synchronizationSubscriptionList
				.iterator();
		while (itr.hasNext()) {
			SynchronizationSubscription subscription = itr.next();
			System.out.println("Application Id : "
					+ subscription.getApplication().getId());
			System.out.println("URI	        : " + subscription.getUri());
			System.out.println("Resource Label : "
					+ subscription.getResourceLabel());
			System.out.println("");
		}
	}


	@Test
	public void testFindByResourceTypeAndApplicationId() throws Exception {
		SynchronizationSubscription newSubcription2 = new SynchronizationSubscription();
		newSubcription2.setUri("http://uri/sync/organism/");
		newSubcription2.setResourceLabel(ResourceType.ORGANISM);
		newSubcription2.setApplication(new Application());
		newSubcription2.getApplication().setId(2L);
		newSubcription2.setSynchronizationFilter(new SynchronizationFilter());
		newSubcription2.getSynchronizationFilter().setId(1L);

		synchronizationSubscriptionService.create(newSubcription2);

		List<SynchronizationSubscription> foundSynchronizationSubscription = synchronizationSubscriptionService
				.findByResourceTypeAndApplicationId(ResourceType.ORGANISM,
						2L);

		assertNotNull("Synchronization subscription list should not be null",
				foundSynchronizationSubscription);
		assertEquals(2,
				foundSynchronizationSubscription.size());
		assertTrue(foundSynchronizationSubscription.stream().anyMatch(synchronizationSubscription ->
				synchronizationSubscription.getUri().equals(newSubcription2.getUri())));
	}


	@Test
	public void testFindAllApplicationsSubscribe() throws Exception {
		List<Application> listOfSubscribedApplications = synchronizationSubscriptionService
				.findAllApplicationsSubscribe();
		assertFalse("Synchronization subscription list should not be empty",
				listOfSubscribedApplications.isEmpty());
	}


	@Test
	public void testFindAllSynchronizationSubscriptionByCriteria()
			throws Exception {
		SynchronizationSubscription newSubcription2 = new SynchronizationSubscription();
		newSubcription2.setUri("http://uri/sync/organism/");
		newSubcription2.setResourceLabel(ResourceType.ESTABLISHMENT);
		newSubcription2.setApplication(new Application());
		newSubcription2.getApplication().setId(2L);
		newSubcription2.setSynchronizationFilter(new SynchronizationFilter());
		newSubcription2.getSynchronizationFilter().setId(1L);

		synchronizationSubscriptionService.create(newSubcription2);

		List<SynchronizationSubscription> synchronizationSubscriptionList = synchronizationSubscriptionService
				.findAllByCriteria(ResourceType.ORGANISM,
						newSubcription2.getApplication());

		assertEquals("Synchronization subscription list should be equals at 1",
				1, synchronizationSubscriptionList.size());

		// Create application
		Application newApplication = new Application();
		newApplication.setName("new application");
		newApplication.setLabel("label");
		newApplication.setUrl("http://newwebsite.fr");
		applicationService.create(newApplication);

		synchronizationSubscriptionList = synchronizationSubscriptionService
				.findAllByCriteria(ResourceType.ORGANISM, newApplication);
		assertTrue("Synchronization subscription list should be empty",
				synchronizationSubscriptionList.isEmpty());
	}
}
