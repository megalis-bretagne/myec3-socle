/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.audit;

import java.util.Calendar;

import org.hibernate.envers.RevisionListener;
import org.myec3.socle.core.domain.model.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This class is used to implement the method newRevision in order to know who
 * has modified an instance of an objet by using the username contained in
 * security context.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class AuditListener implements RevisionListener {

	/**
	 * This method allow to know who has modified an instance of an object by
	 * the username contained in security context
	 */
	@Override
	public void newRevision(Object revEntity) {
		RevisionInfo revInfo = (RevisionInfo) revEntity;

		revInfo.setRevisionDate(Calendar.getInstance().getTime());

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		if (null == authentication) {
			return;
		}

		Object principal = authentication.getPrincipal();
		if (null == principal) {
			return;
		}

		if (principal instanceof Profile) {
			revInfo.setModifiedBy(((Profile) principal).getUser().getUsername());
		}
	}

}
