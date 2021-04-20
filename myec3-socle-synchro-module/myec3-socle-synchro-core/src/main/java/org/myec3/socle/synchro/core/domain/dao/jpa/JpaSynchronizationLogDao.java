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
package org.myec3.socle.synchro.core.domain.dao.jpa;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationLogDao;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationLog} objects. It only provides new specific methods and
 * inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationLogDao")
public class JpaSynchronizationLogDao extends JpaGenericSynchronizationDao<SynchronizationLog>
		implements SynchronizationLogDao {

	private static final String RESOURCE_ID_FIELD = "resourceId";
	private static final String RESOURCE_TYPE_FIELD = "resourceType";

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByResourceId(Long resourceId) {
		this.getLog().debug("Finding all SynchronizationLog by resource id: {}", resourceId);
		try {
			Query query = this.getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s where s.resourceId = :resourceId");
			query.setParameter(RESOURCE_ID_FIELD, resourceId);
			List<SynchronizationLog> synchronizationLogList = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByResourceId successfull.");
			return synchronizationLogList;
		} catch (NoResultException e) {
			this.getLog().info("findAllSynchronizationLogByResourceId returned no result");
			return new ArrayList<>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByResourceId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogBySynchronizationLogId(Long synchronizationLogId) {
		this.getLog().debug("Finding all synchronization log with synchronizationlog id : {} ", synchronizationLogId);
		try {
			Query query = this.getEm()
					.createQuery("select s.synchronizationLog from " + this.getDomainClass().getSimpleName()
							+ " s WHERE initialSynchronizationId =:synchronizationLogId");
			query.setParameter("synchronizationLogId", synchronizationLogId);
			List<SynchronizationLog> result = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogBySynchronizationLogId successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().warn("findAllSynchronizationLogBySynchronizationLogId returned no result");
			return new ArrayList<>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogBySynchronizationLogId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByInitialSynchronizationId(Long initialSynchronizationId) {
		this.getLog()
				.debug("Finding all synchronization log with initial synchronization id : {}", initialSynchronizationId);
		try {
			Query query = this.getEm()
					.createQuery("select s.synchronizationLog from " + this.getDomainClass().getSimpleName()
							+ " s WHERE initialSynchronizationId =:initialSynchronizationId");
			query.setParameter("initialSynchronizationId", initialSynchronizationId);
			List<SynchronizationLog> result = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByInitialSynchronizationId successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().warn("findAllSynchronizationLogByInitialSynchronizationId returned no result");
			return new ArrayList<>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByInitialSynchronizationId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByCriteria(Date startDate, Date endDate,
			Application application, String resourceType, String httpStatus, String synchronizationType,
			String methodType, String statut, Boolean isFinal) {

		this.getLog().debug("Finding all SynchronizationLog by applicationName, httpStatus, methodType, statut");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuilder queryString = new StringBuilder(
					"select s from " + this.getDomainClass().getSimpleName() + " s");
			if ((null != startDate) || (null != endDate) || (null != application) || (null != resourceType)
					|| (null != httpStatus) || (null != synchronizationType) || (null != methodType) || (null != statut)
					|| (null != isFinal)) {
				queryString.append(" where ");

				if ((null != startDate) && (null == endDate)) {
					queryString.append("s.synchronizationDate > :startDate");
					hasCriteria = Boolean.TRUE;
				}
				if ((null != endDate) && (null == startDate)) {
					queryString.append("s.synchronizationDate < :endDate");
					hasCriteria = Boolean.TRUE;
				}
				if ((null != startDate) && (null != endDate)) {
					queryString.append("s.synchronizationDate between :startDate and :endDate");
					hasCriteria = Boolean.TRUE;
				}
				if (null != application) {
					queryString.append("s.applicationName like :applicationName");
					hasCriteria = Boolean.TRUE;
				}
				if (null != resourceType) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.resourceType =:resourceType");
				}
				if (null != httpStatus) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.httpStatus =:httpStatus");
				}
				if (null != synchronizationType) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.synchronizationType =:synchronizationType");
				}
				if (null != methodType) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.methodType =:methodType");
				}
				if (null != statut) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.statut like :statut");
				}
				if (null != isFinal) {
					if (Boolean.TRUE.equals(hasCriteria))
						queryString.append(AND_OPERATOR);
					queryString.append("s.isFinal =:isFinal");
				}
			}

			Query query = this.getEm().createQuery(queryString.toString());
			if ((null != startDate) && (null == endDate)) {
				query.setParameter("startDate", startDate, TemporalType.DATE);
			}
			if ((null == startDate) && (null != endDate)) {
				query.setParameter("endDate", endDate, TemporalType.DATE);
			}
			if ((null != startDate) && (null != endDate)) {
				if (startDate.equals(endDate)) {
					Calendar dateCal = Calendar.getInstance();
					dateCal.setTime(endDate);
					dateCal.set(Calendar.HOUR_OF_DAY, 23);
					dateCal.set(Calendar.MINUTE, 59);
					dateCal.set(Calendar.SECOND, 59);
					dateCal.set(Calendar.MILLISECOND, 000);

					endDate.setTime(dateCal.getTimeInMillis());
				}
				query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
				query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
			}
			if (null != application)
				query.setParameter("applicationName", "%" + application.getName() + "%");
			if (null != resourceType)
				query.setParameter(RESOURCE_TYPE_FIELD, ResourceType.valueOf(resourceType));
			if (null != httpStatus)
				query.setParameter("httpStatus", HttpStatus.valueOf(httpStatus));
			if (null != synchronizationType)
				query.setParameter("synchronizationType", SynchronizationType.valueOf(synchronizationType));
			if (null != methodType)
				query.setParameter("methodType", MethodType.valueOf(methodType));
			if (null != statut)
				query.setParameter("statut", statut);
			if (null != isFinal)
				query.setParameter("isFinal", isFinal);

			List<SynchronizationLog> synchronizationLogList = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByCriteria successfull.");
			return synchronizationLogList;
		} catch (NoResultException e) {
			this.getLog().info("findAllSynchronizationLogByCriteria returned no result");
			return new ArrayList<>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByCriteria failed.", re);
			throw re;
		}
	}

	@Override
	public List<SynchronizationLogDTO> findAllSynchronizationLogByOrganism(Long id) {
		List<SynchronizationLogDTO> synchronizationLogDTOList = new ArrayList<>();
		if (id != null) {
			try {
				Query queryOrganism = buildQuerySelectSynchronizationLogDTOByressourceIdAndType(id, ResourceType.ORGANISM);
				Query queryDepartement = buildQuerySelectSynchronizationLogDTOForOrgaDepartmentByOrganismId(id);
				Query queryProfile = buildQuerySelectSynchronizationLogDTOForAgentProfileByOrganismId(id);

				synchronizationLogDTOList.addAll(queryOrganism.getResultList());
				synchronizationLogDTOList.addAll(queryDepartement.getResultList());
				synchronizationLogDTOList.addAll(queryProfile.getResultList());
				return synchronizationLogDTOList;
			} catch (NoResultException e) {
				this.getLog().info("findAllSynchronizationLogByOrganismId returned no result");
				return new ArrayList<>();
			} catch (RuntimeException re) {
				this.getLog().error("findAllSynchronizationLogByOrganismId failed.", re);
				throw re;
			}
		}
		return synchronizationLogDTOList;
	}

	@Override
	public List<SynchronizationLogDTO> findAllSynchronizationLogByCompany(Long id) {
		List<SynchronizationLogDTO> synchronizationLogDTOList = new ArrayList<>();
		if (id != null) {
			try {
				Query queryCompany = buildQuerySelectSynchronizationLogDTOByressourceIdAndType(id, ResourceType.COMPANY);
				Query queryEstablishment = buildQuerySelectSynchronizationLogDTOForEstablishmentByCompanyId(id);
				Query queryEmployee = buildQuerySelectSynchronizationLogDTOForEmployeeByCompanyId(id);

				synchronizationLogDTOList.addAll(queryCompany.getResultList());
				synchronizationLogDTOList.addAll(queryEstablishment.getResultList());
				synchronizationLogDTOList.addAll(queryEmployee.getResultList());
				return synchronizationLogDTOList;
			} catch (NoResultException e) {
				this.getLog().info("findAllSynchronizationLogByOrganismId returned no result");
				return new ArrayList<>();
			} catch (RuntimeException re) {
				this.getLog().error("findAllSynchronizationLogByOrganismId failed.", re);
				throw re;
			}
		}
		return synchronizationLogDTOList;
	}

	@Override
	public Class<SynchronizationLog> getType() {
		return SynchronizationLog.class;
	}

	/**
	 * Build Query to retrieve all synchronizationLog by resource Id and type
	 * @param resourceId	identifier resource
	 * @param type			resource Type
	 * @return Query to select {@link SynchronizationLogDTO}
	 */
	private Query buildQuerySelectSynchronizationLogDTOByressourceIdAndType(Long resourceId, ResourceType type) {
		String queryStringOrganism = SELECT_OPERATOR +
				"new org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO(s, '', struct.email, struct.label) " +
				"FROM " + this.getDomainClass().getSimpleName() +" s " +
				"LEFT JOIN Structure struct ON struct.id = s.resourceId " +
				"WHERE  s.resourceType = :resourceType "+AND_OPERATOR+" s.resourceId = :resourceId";

		return this.getEm().createQuery(queryStringOrganism, SynchronizationLogDTO.class)
			.setParameter(RESOURCE_TYPE_FIELD, type)
			.setParameter(RESOURCE_ID_FIELD, resourceId);
	}

	/**
	 * Build Query to retrieve all synchronizationLog for ORGANISM_DEPARTMENT by Organism Id
	 * @param organismId organism identifier
	 * @return Query to select {@link SynchronizationLogDTO}
	 */
	private Query buildQuerySelectSynchronizationLogDTOForOrgaDepartmentByOrganismId(Long organismId) {
		String queryStringDepartment = SELECT_OPERATOR +
				"new org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO(s, dep.name, dep.email, dep.label) " +
				"FROM " + this.getDomainClass().getSimpleName() +" s " +
				"LEFT JOIN Department dep ON dep.id = s.resourceId " +
				"WHERE  s.resourceType = :resourceType "+AND_OPERATOR+" s.resourceId IN (" +
				"SELECT o.id FROM OrganismDepartment o WHERE o.organism.id = :resourceId)";


		return this.getEm().createQuery(queryStringDepartment, SynchronizationLogDTO.class)
			.setParameter(RESOURCE_TYPE_FIELD, ResourceType.ORGANISM_DEPARTMENT)
			.setParameter(RESOURCE_ID_FIELD, organismId);
	}

	/**
	 * Build Query to retrieve all synchronizationLog for EMPLOYEE by company Id
	 * @param companyId company identifier
	 * @return Query to select {@link SynchronizationLogDTO}
	 */
	private Query buildQuerySelectSynchronizationLogDTOForEmployeeByCompanyId(Long companyId) {
		String queryStringProfile = SELECT_OPERATOR +
				"new org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO(s, user.username, pro.email, pro.label) " +
				"FROM " + this.getDomainClass().getSimpleName() +" s " +
				"LEFT JOIN Profile pro ON pro.id = s.resourceId " +
				"LEFT JOIN User user ON user.id = pro.user.id "+
				"WHERE  s.resourceType = :resourceType "+AND_OPERATOR+" s.resourceId IN ( " +
				"SELECT employee.id FROM EmployeeProfile employee WHERE employee.establishment.id IN " +
				"(SELECT est.id FROM Establishment est WHERE est.company.id = :resourceId)" +
				")";

		return this.getEm().createQuery(queryStringProfile, SynchronizationLogDTO.class)
				.setParameter(RESOURCE_TYPE_FIELD, ResourceType.EMPLOYEE_PROFILE)
				.setParameter(RESOURCE_ID_FIELD, companyId);
	}

	/**
	 * Build Query to retrieve all synchronizationLog for ESTABLISHEMENT by Company Id
	 * @param companyId company identifier
	 * @return
	 */
	private Query buildQuerySelectSynchronizationLogDTOForEstablishmentByCompanyId(Long companyId) {
		String queryString = SELECT_OPERATOR +
				"new org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO(s, '', est.email, est.name) " +
				"FROM " + this.getDomainClass().getSimpleName() +" s " +
				"LEFT JOIN Establishment est ON est.id = s.resourceId " +
				"WHERE  s.resourceType = :resourceType "+AND_OPERATOR+" est.company.id = :companyId";

		return this.getEm().createQuery(queryString, SynchronizationLogDTO.class)
				.setParameter(RESOURCE_TYPE_FIELD, ResourceType.ESTABLISHMENT)
				.setParameter("companyId", companyId);
	}

	/**
	 * Build Query to retrieve all synchronizationLog for AGENT_PROFILE by Organism Id
	 * @return Query to select {@link SynchronizationLogDTO}
	 */
	private Query buildQuerySelectSynchronizationLogDTOForAgentProfileByOrganismId(Long organismId) {
		String queryStringProfile = SELECT_OPERATOR +
				"new org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO(s, user.username, pro.email, pro.label) " +
				"FROM " + this.getDomainClass().getSimpleName() +" s " +
				"LEFT JOIN Profile pro ON pro.id = s.resourceId " +
				"LEFT JOIN User user ON user.id = pro.user.id "+
				"WHERE  s.resourceType = :resourceType "+AND_OPERATOR+" s.resourceId IN ( " +
					"SELECT agent.id FROM AgentProfile agent WHERE agent.organismDepartment.id IN " +
					"(SELECT o.id FROM OrganismDepartment o WHERE o.organism.id = :resourceId)" +
				")";

		return this.getEm().createQuery(queryStringProfile, SynchronizationLogDTO.class)
			.setParameter(RESOURCE_TYPE_FIELD, ResourceType.AGENT_PROFILE)
			.setParameter(RESOURCE_ID_FIELD, organismId);
	}
}
