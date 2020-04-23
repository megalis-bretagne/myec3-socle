package org.myec3.socle.webapp.pages;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.myec3.socle.webapp.constants.GuWebAppConstants;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class SingleLogout {

    @Inject
    private Request request;

    private List<String> listeUrlLogout;

    private String portailLogoutUrl;

    private String keycloakLogoutUrl;

    @Property
    private String urlLogout;

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate() {
        if (BooleanUtils.toBoolean(request.getParameter("logoutSSO"))){
            //cas ou le parametre logoutSSO est a true, dans ce cas, on appelle l'url de logout du SSO qui ensuite va faire un redirect
            //sur ce meme controleur mais avec cette fois-ci logoutSSO a false
            String redirectUri;
            try {
                redirectUri = URLEncoder.encode(GuWebAppConstants.MYEC3_BASE_URL + "/singlelogout?logoutSSO=false", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Erreur d'encodage de l'url en UTF-8", e);
            }
            try {
                return new URL(GuWebAppConstants.KEYCLOAK_BASE_URL + "/auth/realms/megalis/protocol/openid-connect/logout?redirect_uri="+redirectUri);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Erreur lors de la construction de l'URL de redirection", e);
            }
        }
        this.listeUrlLogout = GuWebAppConstants.LISTE_URL_LOGOUT;
        this.portailLogoutUrl = GuWebAppConstants.PORTAIL_BASE_URL + "/front/logout.jsp";
        return Boolean.TRUE;
    }

    public List<String> getListeUrlLogout() {
        return listeUrlLogout;
    }

    public String getPortailLogoutUrl() {
        return portailLogoutUrl;
    }
}
