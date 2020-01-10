package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeRegionDao;
import org.myec3.socle.core.domain.model.InseeRegion;
import org.springframework.stereotype.Repository;

@Repository("InseeRegionDao")
public class JpaInseeRegionDao extends JpaNoResourceGenericDao<InseeRegion> implements InseeRegionDao {

	@Override
	public Class<InseeRegion> getType() {
		return InseeRegion.class;
	}

	@Override
	public InseeRegion findById(Long regionId) {
		this.getLog().debug("Finding the Region that is associate with following id : " + regionId);
		try {
			Query q = this.getEm().createQuery(
					"SELECT r FROM " + this.getDomainClass().getSimpleName() + " r WHERE r.region = :regionId");
			q.setParameter("regionId", regionId);
			InseeRegion region = (InseeRegion) q.getSingleResult();
			getLog().debug("InseeRegion successfull.");
			return region;
		} catch (NoResultException nre) {
			getLog().warn("InseeRegion returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("InseeRegion failed.", re);
			return null;
		}

	}
}