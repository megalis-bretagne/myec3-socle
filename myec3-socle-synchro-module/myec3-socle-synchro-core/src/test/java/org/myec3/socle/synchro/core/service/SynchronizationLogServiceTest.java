package org.myec3.socle.synchro.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.AbstractDbUnitTest;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Date;
import java.util.Random;

@SqlGroup({
        @Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // organism
        @Sql(value = {"classpath:/db/test/organism/organism_1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/organism/organism_31952.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // company
        @Sql(value = {"classpath:/db/test/company/company_2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/synchroLog/initSynchro.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SynchronizationLogServiceTest extends AbstractDbUnitTest {

    @Autowired
    SynchronizationLogService synchronizationLogService;

    @Test
    public void testCreateLogWithBigErrorMessage() {
        // generate string 260 charactere
        String generatedString = new Random().ints(97, 123)
                .limit(260)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        // GIVEN
        SynchronizationSubscription synchronizationSubscription = new SynchronizationSubscription();
        synchronizationSubscription.setId(13L);

        SynchronizationLog synchronizationLog = new SynchronizationLog();
        synchronizationLog.setResourceId(2000L);
        synchronizationLog.setResourceType(ResourceType.EMPLOYEE_PROFILE);
        synchronizationLog.setHttpCode(400);
        synchronizationLog.setHttpStatus(HttpStatus.BAD_REQUEST);
        synchronizationLog.setMethodType(MethodType.PUT);
        synchronizationLog.setSynchronizationDate(new Date());
        synchronizationLog.setSynchronizationSubscription(synchronizationSubscription);
        synchronizationLog.setApplicationName("Portail megalis");
        synchronizationLog.setSynchronizationType(SynchronizationType.SYNCHRONIZATION);
        synchronizationLog.setSendingApplication("Portail megalis");
        synchronizationLog.setNbAttempts(0);
        synchronizationLog.setErrorCodeType(ErrorCodeType.INTERNAL_SERVER_ERROR);
        synchronizationLog.setErrorLabel("INTERNAL_SERVER_ERROR");
        synchronizationLog.setErrorMessage(generatedString);
        synchronizationLog.setStatut("ERROR");

        // DO
        try {
            synchronizationLogService.create(synchronizationLog);
        } catch (Exception e) {
            Assert.fail("failed");
        }
    }
}
