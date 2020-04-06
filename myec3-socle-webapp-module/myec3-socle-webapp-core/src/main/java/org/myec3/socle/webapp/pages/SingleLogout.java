package org.myec3.socle.webapp.pages;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.myec3.socle.webapp.constants.GuWebAppConstants;

import javax.servlet.ServletException;
import java.util.List;

public class SingleLogout {

    @Inject
    private Request request;

    private List<String> listeUrlLogout;

    private boolean logoutSSO;

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate() {
        this.listeUrlLogout = GuWebAppConstants.LISTE_URL_LOGOUT;
        this.logoutSSO = BooleanUtils.toBoolean(request.getParameter("logoutSSO"));
        return Boolean.TRUE;
    }

    public List<String> getListeUrlLogout() {
        return listeUrlLogout;
    }

    public boolean isLogoutSSO() {
        return logoutSSO;
    }

}
