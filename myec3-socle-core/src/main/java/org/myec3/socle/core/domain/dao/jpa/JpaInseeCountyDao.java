package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeCountyDao;
import org.myec3.socle.core.domain.model.InseeCounty;
import org.springframework.stereotype.Repository;

@Repository("InseeCountyDao")
public class JpaInseeCountyDao extends JpaNoResourceGenericDao<InseeCounty> implements InseeCountyDao {

	public InseeCounty findById(String countyId) {
		this.getLog().debug("Finding the County that is associate with following id : " + countyId);
		try {
			Query q = this.getEm().createQuery(
					"SELECT r FROM " + this.getDomainClass().getSimpleName() + " r WHERE r.dep = :countyId");
			q.setParameter("countyId", countyId);
			InseeCounty county = (InseeCounty) q.getSingleResult();
			getLog().debug("InseeCounty successfull.");
			return county;
		} catch (NoResultException nre) {
			getLog().warn("InseeCounty returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("InseeCounty failed.", re);
			return null;
		}
	}

	@Override
	public Class<InseeCounty> getType() {
		return InseeCounty.class;
	}
}