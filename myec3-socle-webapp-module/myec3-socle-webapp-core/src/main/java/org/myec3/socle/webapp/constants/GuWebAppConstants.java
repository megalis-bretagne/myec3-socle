/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Constants class of the application
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
public final class GuWebAppConstants {

    // Date format
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm";

    // file upload
    public static final String FILER_BUNDLE_NAME = "fileUpload";
    public static final String IMAGE = "/images/";

    public static final ResourceBundle FILE_UPLOAD_BUNDLE = ResourceBundle
	    .getBundle(FILER_BUNDLE_NAME);
    public static final String FILER_PATH = FILE_UPLOAD_BUNDLE
	    .getString("filer.path");
    public static final String FILER_URL = FILE_UPLOAD_BUNDLE
	    .getString("filer.url");
    public static final Long LOGO_MAX_SIZE = Long.valueOf(102400);
    public static final int LOGO_MAX_HEIGHT = 250;
    public static final int LOGO_MAX_WIDTH = 250;
    public static final int LOGO_HEIGHT = 200;
    public static final int LOGO_WIDTH = 200;

    // File import
    public static final String FILER_IMPORT_PATH = FILE_UPLOAD_BUNDLE
	    .getString("filer.import.path");
    public static final Long IMPORT_MAX_FILE_SIZE = Long.valueOf(1048576);
    public static final Long IMPORT_MAX_LINES = Long.valueOf(5000);

    // Webapp constants
    private static final String GU_BUNDLE_NAME = "webapp";
    private static final ResourceBundle GU_BUNDLE = ResourceBundle.getBundle(GU_BUNDLE_NAME);

    // environment constant
    private static final String ENV_BUNDLE_NAME = "environment";
    private static final ResourceBundle ENV_BUNDLE = ResourceBundle.getBundle(ENV_BUNDLE_NAME);

    // Max number of levels (sub departments) for an department
    public static int getSocleGuMaxSubDepartments() {
	return Integer.valueOf(GU_BUNDLE.getString("webapp.max.subdepartments"));
    }

    // opensso
    public static final String OPENSSO_BUNDLE_NAME = "opensso";
    public static final ResourceBundle OPENSSO_BUNDLE = ResourceBundle.getBundle(OPENSSO_BUNDLE_NAME);

    public static final String OPENSSO_LOGIN = OPENSSO_BUNDLE.getString("config.opensso.loginURL");
    public static final String OPENSSO_LOGOUT = OPENSSO_BUNDLE.getString("config.opensso.logoutURL");

    // Mon ebourgogne
    public static final String EB_BUNDLE_NAME = "my-ebourgogne";
    public static final ResourceBundle EB_BUNDLE = ResourceBundle.getBundle(EB_BUNDLE_NAME);
    public static final String EB_URL = EB_BUNDLE.getString("eb-url");
    public static final String JEB_URL = EB_BUNDLE.getString("jeb-url");
    public static final String LOCHALLES_LOGIN_URL = EB_BUNDLE.getString("loc-halles-login-url");

    // COFACE
    public static final String STRUCT_REF_BUNDLE_NAME = "structures-ref";
    public static final ResourceBundle STRUCT_REF_BUNDLE = ResourceBundle.getBundle(STRUCT_REF_BUNDLE_NAME);

    public static final String URL_COMPANY = STRUCT_REF_BUNDLE.getString("url-company");
    public static final String URL_HEAD_OFFICE = STRUCT_REF_BUNDLE.getString("url-headoffice");

    // Portals URL
    public static final String LEGAL_NOTICE_URL = GU_BUNDLE.getString("legal.notice.url");

    // file constants
    public static final String FILER_LOGO_PATH = GU_BUNDLE.getString("filer.logo.path");
    public static final String FILER_LOGO_TMP_FOLDER = "tmp";
    public static final String FILER_LOGO_URL = GU_BUNDLE.getString("filer.logo.url");
    public static final String FILER_LOGO_CUSTOMERS_FOLDER = "customers/";
    public static final String CUSTOMER_FOLDER_NAME = "customer";

    // Expiration time password (days)
    public static int expirationTimePassword = Integer.valueOf(GU_BUNDLE.getString("expiration.time.password"));
    public static int expirationTimeRegeneratePassword = Integer.valueOf(GU_BUNDLE
		    .getString("expiration.time.regenerated.password"));
    public static int expirationTimeAgentRegeneratePassword = Integer.valueOf(GU_BUNDLE
		    .getString("expiration.time.agent.regenerated.password"));

    // Expiration time url modif password (days)
    public static int expirationTimeUrlModifPassword = Integer.valueOf(GU_BUNDLE.getString("expiration.time.url.modif.password"));

    public static final String MYEC3_BASE_URL = ENV_BUNDLE.getString("myec3.baseUrl");

    public static final String PORTAIL_BASE_URL = ENV_BUNDLE.getString("portail.baseUrl");

    public static final String SDM_TOKEN_URL = ENV_BUNDLE.getString("sdm.tokenUrl");

    public static final List<String> LISTE_URL_LOGOUT = getListeUrlLogout();

    private static List<String> getListeUrlLogout() {
        List<String> listeUrlLogout = new ArrayList<>();
        String valPpListeUrlLogout = ENV_BUNDLE.getString("listeUrlLogout");
        if (StringUtils.isNotBlank(valPpListeUrlLogout)){
            listeUrlLogout = Arrays.asList(valPpListeUrlLogout.split("|"));
        }
        return listeUrlLogout;
    }
}
