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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.envers.Audited;

/**
 * 
 * This class represents a SVI profile of a user. it is used to identify quickly
 * the user when he calls a hotline. <br />
 * This class is audited by global audit mechanism.<br/>
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
public class SviProfile implements Serializable, Cloneable, PE {

	private static final long serialVersionUID = -5529209148186545457L;

	private Long id;

	/**
	 * Default constructor. Do nothing.
	 */
	public SviProfile() {
	}

	/**
	 * Contructor. Initialize the hotline identifier of the user.
	 * 
	 * @param id : the hotline identifier
	 */
	public SviProfile(Long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SviProfile clone() throws CloneNotSupportedException {
		return (SviProfile) super.clone();
	}

	/**
	 * @return The hotline identifier of the SVI profile of the user. When he calls
	 *         the hotline, the user gives this identifier in order to find his
	 *         account.
	 */
	@Id
	@GeneratedValue
	@XmlElement(required = true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SviProfile other = (SviProfile) obj;
		if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SviProfile [").append("id=").append(this.getId()).append("]");
		return sb.toString();
	}
}
