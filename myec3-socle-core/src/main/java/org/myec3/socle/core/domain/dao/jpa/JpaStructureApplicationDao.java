package org.myec3.socle.core.domain.dao.jpa;

import org.myec3.socle.core.domain.dao.StructureApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplication;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation provides methods to perform specific queries on
 * {@link StructureApplication} objects.
 */
@Repository("structureApplicationDao")
public class JpaStructureApplicationDao extends JpaNoResourceGenericDao<StructureApplication> implements StructureApplicationDao {

    private static final String BEGIN_JPQL = "select s from ";

    @Override
    public List<StructureApplication> findAllByApplication(Application application) {
        getLog().debug("Finding all StructureApplication by Application");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.structureApplicationId.applicationsId = :id");

            query.setParameter("id", application.getId());

            List<StructureApplication> structureApplications = query.getResultList();
            getLog().debug("findAllByApplication successfull.");
            return structureApplications;
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
    public List<StructureApplication> findAllByStructure(Structure structure) {
        getLog().debug("Finding all StructureApplication by Structure");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.structureApplicationId.structuresId = :id");

            query.setParameter("id", structure.getId());

            List<StructureApplication> structureApplications = query.getResultList();
            getLog().debug("findAllByStructure successfull.");
            return structureApplications;
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
    public StructureApplication findByStructureAndApplication(Structure structure, Application application) {
        getLog().debug("Finding StructureApplication");
        try {
            Query query = getEm().createQuery(BEGIN_JPQL + this.getDomainClass().getSimpleName()
                    + " s where s.structureApplicationId.structuresId = :idStructure and s.structureApplicationId.applicationsId = :idApplication");

            query.setParameter("idStructure", structure.getId());
            query.setParameter("idApplication", application.getId());
            StructureApplication structureApplication = (StructureApplication) query.getSingleResult();
            getLog().debug("findByStructureAndApplication successfull.");
            return structureApplication;
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
    public Class<StructureApplication> getType() {
        return StructureApplication.class;
    }
}
