package org.myec3.socle.synchro.core.domain.dao.jpa;

import org.junit.Assert;
import org.junit.Test;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.AbstractDbUnitTest;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationLogDao;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.stream.Collectors;

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


    @Test
    public void testFindAllSynchronizationLogByCompany() {
        //GIVEN
        Long companyId = new Long(2);
        Long companyNull = null;
        Long companyNotExist = new Long(200);

        //DO
        Assert.assertTrue(synchronizationLogDao.findAllSynchronizationLogByCompany(companyNull).isEmpty());
        Assert.assertTrue(synchronizationLogDao.findAllSynchronizationLogByCompany(companyNotExist).isEmpty());
        List<SynchronizationLogDTO> list =
                synchronizationLogDao.findAllSynchronizationLogByCompany(companyId);
        // ASSERT
        Assert.assertNotNull(list);

        List<SynchronizationLogDTO> listCompanyType = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.COMPANY)).collect(Collectors.toList());
        List<SynchronizationLogDTO> listEstablishment = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.ESTABLISHMENT)).collect(Collectors.toList());

        List<SynchronizationLogDTO> listEmployee = list.stream()
                .filter(sychronizationLogDTO -> sychronizationLogDTO.getSynchronizationLog().getResourceType().equals(ResourceType.EMPLOYEE_PROFILE)).collect(Collectors.toList());


        // 2 type COMany
        Assert.assertEquals(2, listCompanyType.size());
        Assert.assertTrue(listCompanyType.stream().allMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("") &&
                        synchronizationLogDTO.getStructureEmail().equals("structure-2@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("Company test u")));
        // 2 type establishement
        Assert.assertEquals(2, listEstablishment.size());
        Assert.assertTrue(listEstablishment.stream().allMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("") &&
                        synchronizationLogDTO.getStructureEmail().equals("myec3test@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("MYEC3 test")));

        // 2 type Employee
        Assert.assertEquals(2, listEmployee.size());
        Assert.assertTrue(listEmployee.stream().anyMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("USER-2000@mail.fr") &&
                        synchronizationLogDTO.getStructureEmail().equals("lapin@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("Profil 2000 Est 1")));
        Assert.assertTrue(listEmployee.stream().anyMatch(synchronizationLogDTO ->
                synchronizationLogDTO.getUsername().equals("USER-2002@mail.fr") &&
                        synchronizationLogDTO.getStructureEmail().equals("sib@mail.fr") &&
                        synchronizationLogDTO.getStructureLabel().equals("Profil 2002 Est 2")));

    }
}
