package org.myec3.socle.ws.client.constants;

import java.util.ResourceBundle;

public final class WsConstants {

    // environment constant
    private static final String ENV_BUNDLE_NAME = "environment";
    private static final ResourceBundle ENV_BUNDLE = ResourceBundle.getBundle(ENV_BUNDLE_NAME);

    public static final String SDM_TOKEN_URL = ENV_BUNDLE.getString("sdm.tokenUrl");
}
