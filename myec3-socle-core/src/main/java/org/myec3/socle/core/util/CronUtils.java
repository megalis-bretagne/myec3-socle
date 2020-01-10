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
	
	public static Boolean executeCronOnHost(){
	    
	    String currentHostname = null;
	    try {
		currentHostname = InetAddress.getLocalHost().getHostName();
		logger.info("CurrentHostname : " + currentHostname);
	    } catch (UnknownHostException e) {
		logger.error("Can't retrieve hostname : ", e);
	    }
	    logger.info("Comparing hostname : " + currentHostname
		    + " with machineName1 : "
		    + bundleWs.getString("mpsUpdate.machineName1")
		    + " and machineName2 : "
		    + bundleWs.getString("mpsUpdate.machineName2"));
	    if (!currentHostname.equalsIgnoreCase(bundleWs
		    .getString("mpsUpdate.machineName1"))
		    && !currentHostname.equalsIgnoreCase(bundleWs
			    .getString("mpsUpdate.machineName2"))) {
		logger.info("Hostname not matching the properties : " + currentHostname);
		return false;
	    }
	    logger.info("Hostname has the right to execute CRON : " + currentHostname);
	    return true;
	}
	
	public static String getHostName(){
	    String currentHostname = null;
	    try {	
		currentHostname = InetAddress.getLocalHost().getHostName();
	    } catch (UnknownHostException e) {
		logger.error("Can't retrieve hostname : ", e);
	    }
	    return currentHostname;
	}
}
