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
package org.myec3.socle.webapp.holders;

import java.io.Serializable;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Person;

/**
 * Holder class used to manage company's {@link Company} responsibles
 * {@link Person}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 * 
 */
public class PersonHolder implements Serializable {

	private static final long serialVersionUID = 7046391557441622823L;

	private Person _person;
	private Long _key;
	private Boolean _new;
	private Boolean _deleted;

	public PersonHolder(Person person, Boolean newPerson, Boolean deleted,
			Long key) {
		_person = person;
		_new = newPerson;
		_deleted = deleted;
		_key = key;
	}

	public Person getPerson() {
		return _person;
	}

	public Long getKey() {
		return _key;
	}

	public boolean isNew() {
		return _new;
	}

	public boolean setDeleted(boolean deleted) {
		return _deleted = deleted;
	}

	public boolean isDeleted() {
		return _deleted;
	}
}
