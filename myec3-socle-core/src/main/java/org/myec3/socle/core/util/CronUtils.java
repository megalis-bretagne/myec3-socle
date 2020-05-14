package org.myec3.socle.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CronUtils {

	private static final Log logger = LogFactory.getLog(CronUtils.class);

	private static ResourceBundle bundle = ResourceBundle
			.getBundle("socleCore");
	
	private static ResourceBundle bundleWs = ResourceBundle
		.getBundle("mpsUpdate");

	public static boolean isCronServer() {
		String currentHostname = null;
		try {
			currentHostname = InetAddress.getLocalHost().getHostName();
			logger.info("CurrentHostname : " + currentHostname);
		} catch (UnknownHostException e) {
			logger.error("Can't retrieve hostname : ", e);
		}

		if (!currentHostname.equalsIgnoreCase(bundle
				.getString("server.instance.hostname"))) {
			return false;
		}
		return true;
	}
}
