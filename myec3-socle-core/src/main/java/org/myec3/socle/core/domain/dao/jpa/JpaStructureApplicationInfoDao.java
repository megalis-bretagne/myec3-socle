package org.myec3.socle.core.domain.dao.jpa;

import org.myec3.socle.core.domain.dao.StructureApplicationInfoDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplicationInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation provides methods to perform specific queries on
 * {@link StructureApplicationInfo} objects.
 */
@Repository("structureApplicationInfoDao")
public class JpaStructureApplicationInfoDao extends JpaNoResourceGenericDao<StructureApplicationInfo> implements StructureApplicationInfoDao {

    private static final String BEGIN_JPQL = "select s from ";

    @Override
    public List<StructureApplicationInfo> findAllByApplication(Application application) {
        getLog().debug("Finding all StructureApplicationInfo by Application");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.application = :application");

            query.setParameter("application", application);

            List<StructureApplicationInfo> structureApplicationInfos = query.getResultList();
            getLog().debug("findAllByApplication successfull.");
            return structureApplicationInfos;
        } catch (NoResultException re) {
            // No result found, we return null instead of errors
            getLog().warn("findAllByApplication returned no result.");
            return new ArrayList<>();
        } catch (RuntimeException re) {
            getLog().error("findAllByApplication failed.", re);
            return new ArrayList<>();
        }
    }

    @Override
    public List<StructureApplicationInfo> findAllByStructure(Structure structure) {
        getLog().debug("Finding all StructureApplicationInfo by Structure");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.structure = :structure");

            query.setParameter("structure", structure);

            List<StructureApplicationInfo> structureApplicationInfos = query.getResultList();
            getLog().debug("findAllByStructure successfull.");
            return structureApplicationInfos;
        } catch (NoResultException re) {
            // No result found, we return null instead of errors
            getLog().warn("findAllByStructure returned no result.");
            return new ArrayList<>();
        } catch (RuntimeException re) {
            getLog().error("findAllByStructure failed.", re);
            return new ArrayList<>();
        }
    }

    @Override
    public StructureApplicationInfo findByStructureAndApplication(Structure structure, Application application) {
        getLog().debug("Finding StructureApplication");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.structure = :structure and s.application = :application");

            query.setParameter("structure", structure);
            query.setParameter("application", application);
            StructureApplicationInfo structureApplicationInfo = (StructureApplicationInfo) query.getSingleResult();
            getLog().debug("findByStructureAndApplication successfull.");
            return structureApplicationInfo;
        } catch (NoResultException re) {
            // No result found, we return null instead of errors
            getLog().warn("findByStructureAndApplication returned no result.");
            return null;
        } catch (RuntimeException re) {
            getLog().error("findByStructureAndApplication failed.", re);
            return null;
        }
    }

    @Override
    public Class<StructureApplicationInfo> getType() {
        return StructureApplicationInfo.class;
    }
}
