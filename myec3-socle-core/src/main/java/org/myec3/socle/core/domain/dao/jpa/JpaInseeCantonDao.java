package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeCantonDao;
import org.myec3.socle.core.domain.model.InseeCanton;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link InseeCanton} objects. It only provides new specific methods and
 * inherits methods from {@link JpaGenericProfileDao}.
 *
 * @author Matthieu Gaspard <matthieu.gaspard@atosorigin.com>
 */

@Repository("InseeCantonDao")
public class JpaInseeCantonDao extends JpaNoResourceGenericDao<InseeCanton> implements InseeCantonDao {

	public InseeCanton findCanton(Long regionId, String countyId, Long cantonId) {
		this.getLog().debug("Finding the Borough that is associate with following region and county : " + regionId + " "
				+ countyId + " " + cantonId);
		try {
			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.region = :regionId AND r.dep = :countyId AND r.canton = :cantonId");
			q.setParameter("regionId", regionId);
			q.setParameter("countyId", countyId);
			q.setParameter("cantonId", cantonId);
			InseeCanton canton = (InseeCanton) q.getSingleResult();
			getLog().debug("findCanton successfull.");
			return canton;
		} catch (NoResultException nre) {
			getLog().warn("findCanton returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findCanton failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getCantonMaxId() {
		this.getLog().debug("Finding the InseeCanton max id.");

		try {
			/*
			 * TODO : I can't just get the MAX because getSingleResult returns an object ?
			 */
			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.canton = (SELECT MAX(canton) FROM " + this.getDomainClass().getSimpleName() + ")");
			InseeCanton canton = (InseeCanton) q.getSingleResult();
			getLog().debug("getCantonMaxId successfull.");
			return canton.getCanton();
		} catch (NoResultException nre) {
			getLog().warn("getCantonMaxId returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("getCantonMaxId failed.", re);
			return null;
		}

	}

	@Override
	public Class<InseeCanton> getType() {
		return InseeCanton.class;
	}

}