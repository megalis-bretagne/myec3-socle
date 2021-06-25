package org.myec3.socle.ws.client.impl;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;

/**
 * classe de test manuel pour tester les services
 */
public class ResourceWsClientTest {

    private java.util.logging.Logger loggerJava = java.util.logging.Logger.getLogger(getClass().getName());


    public void testClient() {
        Client clientWs = JerseyClientBuilder.newClient();
        Feature feature = new LoggingFeature(loggerJava, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_TEXT, null);
        clientWs.register(feature);

        WebTarget webResource = clientWs.target("http://localhost:9090/test");

        Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);

        AgentProfile agent = new AgentProfile("test","label", "toto@mail.fr",
                "function", new User(), Boolean.TRUE, Boolean.TRUE,
                Boolean.TRUE, Boolean.FALSE,
                new OrganismDepartment());

        Response response = builder.post(Entity.xml(agent));
    }
}
