package org.myec3.socle.ws.client.impl;

import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEtablissement;
import org.myec3.socle.ws.client.impl.mps.response.ResponseMandataires;
import org.myec3.socle.ws.client.impl.mps.response.ResponseUniteLegale;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * classe de test manuel pour tester les services
 */
public class mpsWsClientTest {


    //@Test
    public void getInfoEtablissementsTest() throws IOException {
        MpsWsClient mpsWsClient = new MpsWsClient();
        ResponseEtablissement responseEtablissement = mpsWsClient.getInfoEtablissements("41816609600051");
        Assert.notNull(responseEtablissement.getData(), "resource is mandatory. null value is forbidden");
        Assert.notNull(responseEtablissement.getMeta(), "resource is mandatory. null value is forbidden");
    }


    //@Test
    public void getInfoEntreprisesTest() throws IOException {
        MpsWsClient mpsWsClient = new MpsWsClient();
        ResponseUniteLegale responseUniteLegale = mpsWsClient.getInfoEntreprises("418166096");
        Assert.notNull(responseUniteLegale.getData(), "resource is mandatory. null value is forbidden");
        Assert.notNull(responseUniteLegale.getMeta(), "resource is mandatory. null value is forbidden");
    }


    //@Test
    public void getInfoMandatairesTest() throws IOException {
        MpsWsClient mpsWsClient = new MpsWsClient();
        ResponseMandataires responseMandataires = mpsWsClient.getInfoMandataires("418166096");
        Assert.notNull(responseMandataires.getData(), "resource is mandatory. null value is forbidden");
        Assert.notNull(responseMandataires.getMeta(), "resource is mandatory. null value is forbidden");
    }

}
