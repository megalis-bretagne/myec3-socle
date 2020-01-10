package org.myec3.socle.core.domain.dao.jpa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.dao.ConnectionInfosDao;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.springframework.stereotype.Repository;

@Repository("connectionInfosDao")
public class JpaConnectionInfosDao extends JpaNoResourceGenericDao<ConnectionInfos> implements ConnectionInfosDao {

	public List<ConnectionInfos> getRecentConnectionInfos() {
		this.getLog().debug("Finding recent connection infos that need update");
		try {

			Calendar cal = Calendar.getInstance();

			// TODO : add property for Day
			cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getConnectionInfoDay()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.lastConnectionDate > :lastConnectionDateLimit");
			q.setParameter("lastConnectionDateLimit", formatForBdd.getCalendar().getTime());
			List<ConnectionInfos> results = q.getResultList();
			getLog().debug("getRecentConnectionInfos successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("getRecentConnectionInfos returned no results.");
			return new ArrayList<ConnectionInfos>();
		} catch (RuntimeException re) {
			getLog().error("getRecentConnectionInfos failed.", re);
			return new ArrayList<ConnectionInfos>();
		}
	}

	@Override
	public Class<ConnectionInfos> getType() {
		return ConnectionInfos.class;
	}

}
