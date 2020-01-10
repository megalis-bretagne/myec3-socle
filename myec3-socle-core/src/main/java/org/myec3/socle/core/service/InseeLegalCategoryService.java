package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.InseeLegalCategory;

public interface InseeLegalCategoryService extends IGenericService<InseeLegalCategory> {

	String findParentById(Integer idInsee);

	String findParentByLabel(String inseeLabel);

}