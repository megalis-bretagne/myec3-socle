/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.dao.AgentProfileDao;
import org.myec3.socle.core.domain.dao.CompetenceDao;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Service("competenceService")
public class CompetenceServiceImpl extends AbstractGenericServiceImpl<Competence, CompetenceDao> implements CompetenceService  {

	private static final Log logger = LogFactory.getLog(CompetenceServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void update(AgentProfile ap) {
		//Update comptences
		this.dao.findAll().forEach(cp -> {
			if (ap.getCompetences().contains(cp)) {
				cp.getAgentProfiles().add(ap);
			} else {
				cp.getAgentProfiles().remove(ap);
			}
			this.dao.update(cp);
		});
	}
}
