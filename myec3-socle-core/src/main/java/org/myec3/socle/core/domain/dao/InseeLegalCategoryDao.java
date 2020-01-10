package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.InseeLegalCategory;

public interface InseeLegalCategoryDao extends NoResourceGenericDao<InseeLegalCategory> {


	/**
	 * Find InseeLegalCategory by its children id
	 *
	 * @param idInsee
	 *            : exact child id to find parent.
	 * @return the label of the parent.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	String findParentById(Integer idInsee);

	/**
	 * Find InseeLegalCategory by its children label
	 *
	 * @param inseeLabel
	 *            : exact child label to find parent.
	 * @return the label of the parent.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	String findParentByLabel(String inseeLabel);

}