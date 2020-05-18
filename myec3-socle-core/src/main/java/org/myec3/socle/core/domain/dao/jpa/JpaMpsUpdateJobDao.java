package org.myec3.socle.core.domain.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.MpsUpdateJobDao;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.util.CronUtils;
import org.springframework.stereotype.Repository;

@Repository("mpsUpdateJobDao")
public class JpaMpsUpdateJobDao extends JpaNoResourceGenericDao<MpsUpdateJob> implements MpsUpdateJobDao {

	public List<MpsUpdateJob> findLimit(int limit) {
		this.getLog().debug("Finding the resources to update with the following limit : " + limit);
		try {

			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName() + " r ORDER BY r.priority ASC");
			q.setMaxResults(limit);
			List<MpsUpdateJob> results = q.getResultList();
			getLog().debug("getCompanyToUpdate successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("findLimit returned no results.");
			return new ArrayList<MpsUpdateJob>();
		} catch (RuntimeException re) {
			getLog().error("findLimit failed.", re);
			return new ArrayList<MpsUpdateJob>();
		}

	}

	@Override
	public Class<MpsUpdateJob> getType() {
		return MpsUpdateJob.class;
	}
}
