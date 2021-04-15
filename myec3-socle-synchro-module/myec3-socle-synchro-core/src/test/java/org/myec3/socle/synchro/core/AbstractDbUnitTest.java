package org.myec3.socle.synchro.core;


import org.junit.runner.RunWith;
import org.myec3.socle.synchro.core.config.SynchroCoreConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SynchroCoreConfig.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractDbUnitTest {
}