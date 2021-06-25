package org.myec3.socle.tools;

import org.junit.Assert;
import org.junit.Test;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.tools.SynchronizationMarshaller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SynchronizationMarshallerTest {

    @Test
    public void testMarshall() {
        AgentProfile agentProfile = new AgentProfile();
        agentProfile.setId(1L);
        agentProfile.setRoles(new ArrayList<Role>());
        agentProfile.setUser(new User());


        Address address = new Address();
        address.setPostalAddress("t");
        address.setPostalCode("dd");
        address.setCity("city");
        agentProfile.setAddress(address);

        Organism organism = new Organism();
        organism.setApplications(new ArrayList<Application>());
        organism.setDepartments(new ArrayList<OrganismDepartment>());
        organism.setChildStructures(new ArrayList<Structure>());
        organism.setParentStructures(new ArrayList<Structure>());
        organism.setAddress(address);


        agentProfile.setElected(Boolean.FALSE);

        ByteArrayOutputStream c = SynchronizationMarshaller.marshalResource(organism);
        Assert.assertNotNull(c);
        ByteArrayOutputStream c2 = SynchronizationMarshaller.marshalResource(agentProfile);
        Assert.assertNotNull(c2);
    }

    @Test
    public void testMarshallAgentEnabledFalse() {

        AgentProfile agentProfile = new AgentProfile();
        agentProfile.setUser(new User());
        agentProfile.getUser().setEnabled(false);

        agentProfile.setEnabled(false);
        agentProfile.setElected(false);
        agentProfile.setExecutive(false);
        agentProfile.setSubstitute(false);


        // MARSHAL OBJECT
        ByteArrayOutputStream c = SynchronizationMarshaller.marshalResource(agentProfile);
        String xmlMarshal = c.toString();
        Assert.assertTrue(xmlMarshal.contains("<user><enabled>false</enabled></user>"));
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<agentProfile><enabled>false</enabled><roles/><user><enabled>false</enabled></user>" +
                        "<elected>false</elected><executive>false</executive><representative>false</representative><substitute>false</substitute></agentProfile>",
                xmlMarshal);

        // UNMARSHAL THE MarShal Object
        AgentProfile agentUnmarshall = (AgentProfile) SynchronizationMarshaller.unmarshalResource(xmlMarshal);
        Assert.assertEquals(false, agentUnmarshall.getUser().isEnabled());
        Assert.assertEquals(false, agentUnmarshall.getElected());
        Assert.assertEquals(false, agentUnmarshall.getExecutive());
        Assert.assertEquals(false, agentUnmarshall.getSubstitute());
    }

}
