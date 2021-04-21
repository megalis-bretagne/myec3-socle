package org.myec3.socle;


import org.junit.runner.RunWith;
import org.myec3.socle.core.domain.dao.CoreConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableTransactionManagement
@ContextConfiguration(classes = CoreConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractDbSocleUnitTest {

}