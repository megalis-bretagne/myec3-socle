package org.myec3.socle.core.domain.model.constants;

import java.util.ResourceBundle;

/**
 *
 * Created by a557263 on 15/12/2014.
 */
public final class MyEc3AlfrescoConstants {

    private static final String ALF_BUNDLE_NAME = "alfresco";
    private static final ResourceBundle ALF_BUNDLE = ResourceBundle
            .getBundle(ALF_BUNDLE_NAME);

    public static String getEndTenantsName() {
        return ALF_BUNDLE.getString("alf.tenantsname.end");
    }
}
