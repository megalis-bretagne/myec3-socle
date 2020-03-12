package org.myec3.socle.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component("sdmInitResourceClientimpl")
public class SdmInitResourceClientimpl {

    private static final Logger logger = LoggerFactory.getLogger(SdmInitResourceClientimpl.class);


    private Client clientWs = null;

    private Client getClientWs() {
        if (this.clientWs == null) {
            this.clientWs = JerseyClientBuilder.newClient();
        }
        return this.clientWs;
    }

    private void prepareHeaderAtexo(Invocation.Builder builder) {

        WebTarget webResource = getClientWs()
                .target(GuWebAppConstants.SDM_TOKEN_URL);
        Invocation.Builder builderToken = webResource.request().accept(MediaType.APPLICATION_JSON);
/*        builderToken.header("externalid", 1122);
        builderToken.header("usertype", "AGENT");*/

        Response response = builderToken.get();
        String responseToString = response.readEntity(String.class);
        String[] tab = StringUtils.split(responseToString, "<ticket>");
        String[] tab2 = StringUtils.split(tab[1], "</ticket>");

        builder.header("Authorization", "Bearer " + tab2[0]);
/*        builder.header("externalid", 1122);
        builder.header("usertype", "AGENT");*/
    }

    private Invocation.Builder getInvocationBuilder(String url, Map<String, String> queryParameters) {
        WebTarget webResource = getClientWs().target(url);

        if (queryParameters != null) {
            for (Map.Entry<String, String> queryParameter : queryParameters.entrySet())
                webResource = webResource.queryParam(queryParameter.getKey(), queryParameter.getValue());
        }
        Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);

        prepareHeaderAtexo(builder);
        return builder;
    }


    public Object get(String url, int page) {

        Map<String, String> queryParameters = new HashMap<String, String>();

        queryParameters.put("limit", "1000");
        queryParameters.put("page", String.valueOf(page));

        try {
            Invocation.Builder builder = getInvocationBuilder(url, queryParameters);

            Response response = builder.get();

            String json = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(json, Map.class);
            Object o = map.get("mpe");
            LinkedHashMap<String, Object> o1 = (LinkedHashMap<String, Object>) o;
            Object o2 = o1.get("reponse");
            ArrayList<Object> o3 = (ArrayList<Object>) o2;
            Object statutReponse = o3.get(0);

            Object resourceSDM = o3.get(1);
            return resourceSDM;

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }


    }
}