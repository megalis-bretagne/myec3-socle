package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeBoroughDao;
import org.myec3.socle.core.domain.model.InseeBorough;
import org.springframework.stereotype.Repository;

@Repository("InseeBoroughDao")
public class JpaInseeBoroughDao extends JpaNoResourceGenericDao<InseeBorough> implements InseeBoroughDao {

	public InseeBorough findBourough(Long regionId, String countyId, Long arId) {
		this.getLog().debug("Finding the Borough that is associate with following region and county : " + regionId + " "
				+ countyId + " " + arId);
		try {
			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.region = :regionId AND r.dep = :countyId AND r.ar = :arId");
			q.setParameter("regionId", regionId);
			q.setParameter("countyId", countyId);
			q.setParameter("arId", arId);
			InseeBorough borough = (InseeBorough) q.getSingleResult();
			getLog().debug("findBourough successfull.");
			return borough;
		} catch (NoResultException nre) {
			getLog().warn("findBourough returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findBourough failed.", re);
			return null;
		}
	}

	@Override
	public Class<InseeBorough> getType() {
		return InseeBorough.class;
	}

}