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

import javax.persistence.Column;
import javax.persistence.Entity;


/**
 * This class represents an INSEE Code.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * 
 */
@Entity
public class InseeGeoCode extends Resource {

	private static final long serialVersionUID = 1237773758165613984L;

	private String inseeCode;

	private String postalCode;
	
	// Code région
	private Long reg;

	// Code département
	private String dep;

	// Code commune
	private String com;

	// Code arrondissement
	private Long ar;

	// Code canton
	private Long ct;

	/**
	 * Default constructor.
	 */
	public InseeGeoCode() {
		super();
	}

	public InseeGeoCode(String inseeCode){
		this.inseeCode = inseeCode;
	}

	/**
	 * The INSEE Code.
	 * 
	 * @param inseeCode
	 *            : the inseeCode to set
	 */
	public void setInseeCode(String inseeCode) {
		this.inseeCode = inseeCode;
	}

	/**
	 * @return the inseeCode
	 */
	@Column(nullable = false)
	public String getInseeCode() {
		return inseeCode;
	}


	@Column(nullable = false)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

		@Column(nullable = false)
	public Long getReg() {
	    return reg;
	}

	public void setReg(Long reg) {
	    this.reg = reg;
	}

	@Column(nullable = false)
	public String getDep() {
	    return dep;
	}

	public void setDep(String dep) {
	    this.dep = dep;
	}

	@Column(nullable = false)
	public String getCom() {
	    return com;
	}

	public void setCom(String com) {
	    this.com = com;
	}

	@Column(nullable = true)
	public Long getAr() {
	    return ar;
	}

	public void setAr(Long ar) {
	    this.ar = ar;
	}

	@Column(nullable = true)
	public Long getCt() {
	    return ct;
	}

	public void setCt(Long ct) {
	    this.ct = ct;
	}

	public static long getSerialversionuid() {
	    return serialVersionUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getLabel() == null) ? 0 : this.getLabel().hashCode());
		result = prime * result
				+ ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InseeGeoCode other = (InseeGeoCode) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
	    return "InseeGeoCode [inseeCode=" + inseeCode + ", getLabel()="
		    + getLabel() + "]";
	}

}
