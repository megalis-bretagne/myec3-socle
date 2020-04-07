package org.myec3.socle.webapp.pages;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.myec3.socle.webapp.constants.GuWebAppConstants;

import java.util.List;

public class SingleLogout {

    @Inject
    private Request request;

    private List<String> listeUrlLogout;

    private boolean logoutSSO;

    private String portailBaseUrl;

    private String keycloakLogoutUrl;

    @Property
    private String urlLogout;

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate() {
        this.listeUrlLogout = GuWebAppConstants.LISTE_URL_LOGOUT;
        this.logoutSSO = BooleanUtils.toBoolean(request.getParameter("logoutSSO"));
        this.portailBaseUrl = GuWebAppConstants.PORTAIL_BASE_URL;
        this.keycloakLogoutUrl = GuWebAppConstants.KEYCLOAK_BASE_URL + "/auth/realms/megalis/protocol/openid-connect/logout";
        return Boolean.TRUE;
    }

    public List<String> getListeUrlLogout() {
        return listeUrlLogout;
    }

    public boolean isLogoutSSO() {
        return logoutSSO;
    }

    public String getPortailBaseUrl() {
        return portailBaseUrl;
    }

    public String getKeycloakLogoutUrl() {
        return keycloakLogoutUrl;
    }

}
