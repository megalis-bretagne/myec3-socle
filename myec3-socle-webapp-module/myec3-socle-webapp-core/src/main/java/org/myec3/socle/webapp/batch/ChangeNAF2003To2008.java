package org.myec3.socle.webapp.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.util.CronUtils;
import org.myec3.socle.synchro.api.SynchronizationConfiguration;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("ChangeNAF2003ToNAF2008")
public class ChangeNAF2003To2008 {

	// private CompanyWSinfo cofaceWS = new CofaceWsClient();
	private static final Log logger = LogFactory.getLog(ChangeNAF2003To2008.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	public void updateNAF2003ToNAF2008() {

		// if this is a cron server
		if (CronUtils.isCronServer()) {
			logger.warn("[UPDATE SOCLE NAF] start ");
			ResourceBundle bundle = ResourceBundle.getBundle("database");
			String login = bundle.getString("dataSource.username");
			String password = bundle.getString("dataSource.password");
			String base = "eb_socle";
			String url = bundle.getString("dataSource.url");
			Boolean error = Boolean.FALSE;
			String siren;
			Connection connection = null;
			try {
				Class.forName(bundle.getString("dataSource.driverClassName"));
				connection = DriverManager.getConnection(url, login, password);
				Statement stmt = connection.createStatement();
				ResultSet resultats = stmt
						.executeQuery("SELECT `siren` FROM " + base + ".Update where `status` is NULL LIMIT 1");
				if (resultats.next()) {
					siren = resultats.getString(1);
					/* TREATMENT */
					Company company = companyService.findBySiren(siren);
					if (company != null) {
						// cofaceWS.updateNAF2003ToNAF2008(company);
						logger.warn("[UPDATE SOCLE NAF] Company " + company.getSiren() + "--" + company.getName());
						if (company.getApeCode().length() == 5) {
							companyService.update(company);

							// Synchronization with the other services
							SynchronizationConfiguration configSync = new SynchronizationConfiguration();
							// Apps ID
							ArrayList<Long> listeApplicationId = new ArrayList<Long>();
							listeApplicationId.add(applicationService.findByName("Application - GRC").getId());
							listeApplicationId.add(applicationService.findByName("sdm").getId());
							listeApplicationId.add(applicationService.findByName("jeb").getId());
							configSync.setListApplicationIdToResynchronize(listeApplicationId);
							configSync.setSynchronizationType(SynchronizationType.SYNCHRONIZATION);
							configSync.setSendingApplication("Socle GU");
							this.synchronizationService.notifyUpdate(company, configSync);
						} else {
							error = Boolean.TRUE;
						}

					} else {
						logger.error("[UPDATE SOCLE NAF] Code APE 2008 pour " + siren + " indisponible");
						error = Boolean.TRUE;
					}

					/* END TREATMENT */
					if (!error) {
						stmt.executeUpdate(
								"UPDATE " + base + ".Update SET `status`= 1, `realized`= 1 WHERE siren=" + siren);
					} else {
						stmt.executeUpdate(
								"UPDATE " + base + ".Update SET `status`= 0, `realized`= 1 WHERE siren=" + siren);
					}
				} else {
					logger.warn("[UPDATE SOCLE NAF] All update finished");
				}

			} catch (ClassNotFoundException cnfe) {
				logger.error("[UPDATE SOCLE NAF] Driver introuvable : " + cnfe.toString());

			} catch (SQLException sqle) {
				logger.error("[UPDATE SOCLE NAF] Erreur SQL : " + sqle.toString());

			} catch (Exception e) {
				logger.error("[UPDATE SOCLE NAF] Exception : " + e.toString());
			} finally {
				if (connection != null) {
					try {
						connection.close();
						logger.warn("[UPDATE SOCLE NAF] finished ");
					} catch (Exception e) {
						logger.error("[UPDATE SOCLE NAF] Close Exception : " + e.toString());
					}

				}
			}
		}

	}
}
