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
package org.myec3.socle.ws.server.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface which defines methode availables for annuaire
 *
 * @author Charles Bourrée
 */
@RequestMapping("/annuaire/")
public interface AnnuaireResource {


	/**
	 * Return paginate AnnuaireDto
	 *
	 * @param page     page wanted, begin at 1
	 * @param filter   word to search in name, label or competences
	 * @param sortBy   column to order
	 * @param sortDir DESC, ASC
	 * @param size     number of element per page
	 * @return
	 */
	//@CrossOrigin Only for local tests
	@RequestMapping(value = "/agents", method = RequestMethod.GET)
	ResponseEntity getAgents(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String filter,
							 @RequestParam(defaultValue = "label") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir,
							 @RequestParam(defaultValue = "3") int size, @RequestParam(required = false) String competences,
							 @RequestParam(required = false) Boolean enableFilter);

}
