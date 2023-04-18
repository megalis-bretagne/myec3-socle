package org.myec3.socle.synchro.core.service;

import org.junit.Test;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.core.AbstractDbUnitTest;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.*;


@SqlGroup({
		@Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(value = {"classpath:/db/test/synchronizationQueue/insert_sync.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@Transactional
public class SynchronizationQueueTest extends AbstractDbUnitTest {

	@Autowired
	private SynchronizationQueueService synchronizationQueueService;

	/**
	 * Test method for
	 * {@link org.myec3.socle.synchro.core.service.impl.SynchronizationQueueServiceImpl#create}
	 */
	@Test
	public void testCreate() throws Exception {
		SynchronizationQueue newSynchronizationQueue = new SynchronizationQueue();
		newSynchronizationQueue.setResourceId(200L);
		newSynchronizationQueue.setResourceType(ResourceType.AGENT_PROFILE);
		newSynchronizationQueue.setSendingApplication("GU");
		newSynchronizationQueue
				.setSynchronizationJobType(SynchronizationJobType.CREATE);

		this.synchronizationQueueService.create(newSynchronizationQueue);

		List<SynchronizationQueue> foundSynchronizationQueueList = this.synchronizationQueueService
				.findAll();
		assertNotNull(foundSynchronizationQueueList);

		SynchronizationQueue foundSyncQueue = this.synchronizationQueueService
				.findOne(newSynchronizationQueue.getId());
		assertNotNull(foundSyncQueue);
		assertEquals(newSynchronizationQueue.getId(), foundSyncQueue.getId());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.synchro.core.service.impl.SynchronizationQueueServiceImpl#update}
	 */
	@Test
	public void testUpdate() throws Exception {
		SynchronizationQueue syncQueue = synchronizationQueueService.findOne(10L);
		syncQueue.setResourceType(ResourceType.EMPLOYEE_PROFILE);
		this.synchronizationQueueService.update(syncQueue);

		SynchronizationQueue foundSyncQueue = this.synchronizationQueueService
				.findOne(syncQueue.getId());
		assertEquals(ResourceType.EMPLOYEE_PROFILE,
				foundSyncQueue.getResourceType());
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.synchro.core.service.impl.SynchronizationQueueServiceImpl#delete}
	 */
	@Test
	public void testDelete() throws Exception {
		Long queueId = 10L;
		SynchronizationQueue foundSyncQueue = this.synchronizationQueueService
				.findOne(queueId);
		assertNotNull(foundSyncQueue);

		this.synchronizationQueueService.delete(foundSyncQueue);
		assertNull(this.synchronizationQueueService.findOne(queueId));
	}

	/**
	 * Test method for
	 * {@link org.myec3.socle.synchro.core.service.impl.SynchronizationQueueServiceImpl#findAll(int limit)}
	 */
	@Test
	public void testFindAllByLimit() throws Exception {
		int limit = 50;
		List<SynchronizationQueue> foundSyncQueues = this.synchronizationQueueService
				.findAll(limit);
		assertNotNull(foundSyncQueues);
		assertTrue(foundSyncQueues.size() >= 1);
	}
}
