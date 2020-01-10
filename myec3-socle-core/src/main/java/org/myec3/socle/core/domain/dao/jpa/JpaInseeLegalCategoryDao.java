package org.myec3.socle.core.domain.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeLegalCategoryDao;
import org.myec3.socle.core.domain.model.InseeLegalCategory;
import org.springframework.stereotype.Repository;

@Repository("inseeLegalCategoryDao")
public class JpaInseeLegalCategoryDao extends JpaNoResourceGenericDao<InseeLegalCategory>
		implements InseeLegalCategoryDao {

	@Override
	public String findParentById(Integer idInsee) {
		try {

			getLog().info("Find parent InseeLegalCategory of Insee Code " + idInsee);

			Query queryParent = getEm().createQuery("SELECT parent FROM " + this.getDomainClass().getSimpleName()
					+ " i " + "WHERE i.idInsee = :idInsee");
			queryParent.setParameter("idInsee", idInsee);
			String parentId = queryParent.getSingleResult().toString();
			int parentIdTemp = Integer.parseInt(parentId);

			Query queryLabel = getEm().createQuery("SELECT label FROM " + this.getDomainClass().getSimpleName() + " i "
					+ "WHERE i.idInsee = :parentId ");
			queryLabel.setParameter("parentId", parentIdTemp);

			String finalLabel = queryLabel.getSingleResult().toString();
			return finalLabel;

		} catch (NoResultException re) {
			// No result found
			getLog().warn("findParentById returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findParentById failed.", re);
			return null;
		}
	}

	@Override
	public String findParentByLabel(String inseeLabel) {
		try {

			getLog().info("Find parent InseeLegalCategory of Insee label " + inseeLabel);

			Query queryParent = getEm().createQuery("SELECT parent FROM " + this.getDomainClass().getSimpleName()
					+ " i " + "WHERE i.label = :inseeLabel");
			queryParent.setParameter("inseeLabel", inseeLabel);
			String parentId = queryParent.getSingleResult().toString();
			int parentIdTemp = Integer.parseInt(parentId);

			Query queryLabel = getEm().createQuery("SELECT label FROM " + this.getDomainClass().getSimpleName() + " i "
					+ "WHERE i.idInsee = :parentId ");
			queryLabel.setParameter("parentId", parentIdTemp);

			String finalLabel = queryLabel.getSingleResult().toString();
			return finalLabel;

		} catch (NoResultException re) {
			// No result found
			getLog().warn("findParentByLabel returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findParentByLabel failed.", re);
			return null;
		}
	}

	@Override
	public Class<InseeLegalCategory> getType() {
		return InseeLegalCategory.class;
	}
}