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
package org.myec3.socle.ws.server.resource.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompetenceService;
import org.myec3.socle.ws.server.dto.AnnuaireDto;
import org.myec3.socle.ws.server.resource.AnnuaireResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Methods uses for Monmaximilien annuaire
 *
 * @author Charles Bourrée <charles@bourree.worldline.com>
 */
@Scope("prototype")
@RestController
public class AnnuaireResourceImpl implements AnnuaireResource {
	
	private static final Logger logger = LogManager.getLogger(AnnuaireResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getAgents(int page, String filter, String sortBy, String sortDir, int size,
									String competences, Boolean enableFilter) {
		try {
			List<AgentProfile> agentProfileList = agentProfileService.getAnnuaire(page, filter, sortBy, sortDir, size,
					competences, enableFilter);
			Long nbResults = agentProfileService.getAnnuaireCount(filter, competences, enableFilter);
			AnnuaireDto annuaireDto = new AnnuaireDto(agentProfileList, nbResults);
			return ResponseEntity.ok(annuaireDto);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}

	}

}
