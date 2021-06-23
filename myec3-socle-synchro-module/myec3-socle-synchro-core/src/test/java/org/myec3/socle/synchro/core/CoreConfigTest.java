package org.myec3.socle.synchro.core;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class CoreConfigTest extends AbstractDbUnitTest{


    @Value("#{'${myec3.synchro.applications.monotenant.ids}'.split(',')}")
    private List<Integer> idsApplicationMonotenant;

    @Test
    public void testPropertiesParapheurMonotenant() {
        Assert.assertNotNull(idsApplicationMonotenant);
        Assert.assertEquals( 2,idsApplicationMonotenant.size(),2);
        Assert.assertEquals(new Integer(7) , idsApplicationMonotenant.get(0));
        Assert.assertEquals(new Integer(10) , idsApplicationMonotenant.get(1));
    }
}
