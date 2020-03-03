package org.myec3.socle.synchro.core.domain.dao.jpa;


import org.myec3.socle.synchro.core.domain.dao.SynchroIdentifiantExterneDao;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.springframework.stereotype.Repository;

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
}
