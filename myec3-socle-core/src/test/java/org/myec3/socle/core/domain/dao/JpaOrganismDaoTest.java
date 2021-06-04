package org.myec3.socle.core.domain.dao;

import org.junit.Assert;
import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.dao.jpa.JpaOrganismDao;
import org.myec3.socle.core.domain.dto.OrganismLightDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

@SqlGroup({
        @Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // organism
        @Sql(value = {"classpath:/db/test/organism/organism_1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:/db/test/organism/organism_31952.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // company
        @Sql(value = {"classpath:/db/test/company/company_2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
public class JpaOrganismDaoTest extends AbstractDbSocleUnitTest {

    @Autowired
    public JpaOrganismDao jpaOrganismDao;

    @Test
    public void testQuery() {
        List<OrganismLightDTO>  organismList = jpaOrganismDao.findOrganismLight();
        Assert.assertNotNull(organismList);
    }
}
