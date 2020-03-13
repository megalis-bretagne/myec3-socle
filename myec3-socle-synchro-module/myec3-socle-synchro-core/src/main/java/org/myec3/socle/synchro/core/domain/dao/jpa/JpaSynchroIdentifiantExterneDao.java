package org.myec3.socle.synchro.core.domain.dao.jpa;


import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.dao.SynchroIdentifiantExterneDao;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;

@Repository("synchroIdentifiantExterneDao")
public class JpaSynchroIdentifiantExterneDao extends JpaGenericSynchronizationDao<SynchroIdentifiantExterne>
        implements SynchroIdentifiantExterneDao {
    @Override
    public Class<SynchroIdentifiantExterne> getType() {
        return SynchroIdentifiantExterne.class;
    }

    @Override
    public boolean truncate() {
        this.getLog().debug("truncate tableSynchroIdentifiantExterne");
        try {
            Query query = this.getEm().createNativeQuery(
                    "truncate table SynchroIdentifiantExterne");
            query.executeUpdate();
            this.getLog().debug("truncate table SynchroIdentifiantExterne successfull.");
            return true;
        }catch (RuntimeException re) {
            this.getLog().error("tableSynchroIdentifiantExterne failed.", re);
            throw re;
        }
    }

    @Override
    public SynchroIdentifiantExterne findByIdSocle(long idSocle, ResourceType resourceType) {
        this.getLog().debug("Finding initial SynchroIdentifiantExterneService with idSocle : " + idSocle);
        try {
            Query query = this.getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
                    + " s WHERE idSocle =:idSocle and typeRessource =:typeRessource" );
            query.setParameter("idSocle", idSocle);
            query.setParameter("typeRessource", resourceType);
            SynchroIdentifiantExterne result = (SynchroIdentifiantExterne) query.getSingleResult();
            this.getLog().debug("findByIdSocle successfull.");
            return result;
        } catch (NoResultException e) {
            this.getLog().info("findByIdSocle returned no result");
            return null;
        } catch (RuntimeException re) {
            this.getLog().error("findByIdSocle failed.", re);
            throw re;
        }
    }
}
