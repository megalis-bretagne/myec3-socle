package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.InseeLegalCategoryDao;
import org.myec3.socle.core.domain.model.InseeLegalCategory;
import org.myec3.socle.core.service.InseeLegalCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("inseeLegalCategoryService")
public class InseeLegalCategoryServiceImpl extends AbstractGenericServiceImpl<InseeLegalCategory, InseeLegalCategoryDao>
		implements InseeLegalCategoryService {

	@Override
	public String findParentById(Integer idInsee) {
		Assert.notNull(idInsee, "idInsee is mandatory. null value is forbidden.");

		return this.dao.findParentById(idInsee);
	}

	@Override
	public String findParentByLabel(String inseeLabel) {
		Assert.notNull(inseeLabel, "inseeLabel is mandatory. null value is forbidden.");

		return this.dao.findParentByLabel(inseeLabel);
	}

}