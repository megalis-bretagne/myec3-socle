package org.myec3.socle.synchro.core.domain.dao.jpa;

import org.junit.Assert;
import org.junit.Test;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.AbstractDbUnitTest;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationLogDao;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.stream.Collectors;

@SqlGroup({
        @Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/organism/organism_1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/organism/organism_31952.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/synchroLog/initSynchro.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
public class JpaSynchronizationLogDaoTest extends AbstractDbUnitTest {

    @Autowired
    public SynchronizationLogDao synchronizationLogDao;

    @Test
    public void testFindAllSynchronizationLogByOrganism() {
        //GIVEN
        Long organismId = new Long(31952);
        Long organismNull = null;
        Long organismNotExist = new Long(2);
        Long organismWithNoSynchro = new Long(1);

        //DO
        Assert.assertTrue(synchronizationLogDao.findAllSynchronizationLogByOrganism(organismNull).isEmpty());
        Assert.assertTrue(synchronizationLogDao.findAllSynchronizationLogByOrganism(organismNotExist).isEmpty());
        Assert.assertTrue(synchronizationLogDao.findAllSynchronizationLogByOrganism(organismWithNoSynchro).isEmpty());

        List<SynchronizationLogDTO> list =
                synchronizationLogDao.findAllSynchronizationLogByOrganism(organismId);
        // ASSERT
        Assert.assertNotNull(list);

        List<SynchronizationLogDTO> listOrganismType = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.ORGANISM)).collect(Collectors.toList());
        List<SynchronizationLogDTO> listAgentProfile = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.AGENT_PROFILE)).collect(Collectors.toList());
        List<SynchronizationLogDTO> listDepartement = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.ORGANISM_DEPARTMENT)).collect(Collectors.toList());

        // 2 type ORGANISM
        Assert.assertEquals(2, listOrganismType.size());
        Assert.assertTrue(listOrganismType.stream().allMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("") &&
                        synchronizationLogDTO.getStructureEmail().equals("myec3.sib+31952-CessonCevi@gmail.com") &&
                        synchronizationLogDTO.getStructureLabel().equals("zTest_Megalisd")

        ));

        // 4 type ORGANISM_DEPARTMENT
        Assert.assertEquals(4, listDepartement.size());
        Assert.assertTrue(listDepartement.stream().anyMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("sous sol MEGALIS") &&
                        synchronizationLogDTO.getStructureEmail().equals("megalis-sous-sol@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("Niveau Sous Sol")
        ));
        // 2 type AGENT
        Assert.assertEquals(2, listAgentProfile.size());
        Assert.assertTrue(listAgentProfile.stream().anyMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("lapin-login@mail.fr") &&
                        synchronizationLogDTO.getStructureEmail().equals("lapin@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("Profil 1 OrgDepart 32816")
        ));
    }

}
