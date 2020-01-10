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
package org.myec3.socle.core.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.myec3.socle.core.domain.model.meta.StructureType;

/**
 * This class represents an acronym from a list that can be associated to a
 * {@link Structure}. An acronym represents the trigram of a structure and must
 * be unique for any concrete {@link StructureType}. An acronym cannot be reused
 * for an other structure and its availability should be checked any time it is
 * associated to a structure.<br/>
 * 
 * Acronym is represented by a simple string (label) but this class allow to
 * store and perform actions on the list of all available or already-used
 * acronyms through the entire structure set.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Entity
public class AcronymsList implements Serializable, PE {

	private static final long serialVersionUID = 2936976151703876984L;

	private Long id;
	private String value;
	private Boolean available;

	/**
	 * Default constructor. Do nothing
	 */
	public AcronymsList() {
	}

	/**
	 * Constructor. Initialize label and availability.
	 * 
	 * @param label     : label of the acronym
	 * @param available : availability of the acronym
	 */
	public AcronymsList(String value, Boolean available) {
		this.value = value;
		this.available = available;
	}

	/**
	 * Technical id of the AcronymsList
	 * 
	 * @return the unique identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Value of the acronym
	 * 
	 * @return the acronym value
	 */
	@Column(nullable = false, unique = true)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Availability of the acronym
	 * 
	 * @return true if the acronym is available, false otherwise
	 */
	@Column(nullable = false)
	public Boolean getAvailable() {
		return this.available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AcronymsList other = (AcronymsList) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}