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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class InseeGlobal{

	private String inseeCode;
	
	// Code région
	private String inseeRegion;

	// Code département
	private String inseeCounty;

	// Code arrondissement
	private String inseeBorough;

	// Code canton
	private String inseeCanton;

	/**
	 * Default constructor.
	 */
	public InseeGlobal() {
		super();
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
	@XmlElement(required = true)
	public String getInseeCode() {
		return inseeCode;
	}

	@XmlElement(required = false)
	public String getInseeRegion() {
	    return inseeRegion;
	}

	public void setInseeRegion(String inseeRegion) {
	    this.inseeRegion = inseeRegion;
	}

	@XmlElement(required = false)
	public String getInseeCounty() {
	    return inseeCounty;
	}

	public void setInseeCounty(String inseeCounty) {
	    this.inseeCounty = inseeCounty;
	}

	@XmlElement(required = false)
	public String getInseeBorough() {
	    return inseeBorough;
	}

	public void setInseeBorough(String inseeBorough) {
	    this.inseeBorough = inseeBorough;
	}

	@XmlElement(required = false)
	public String getInseeCanton() {
	    return inseeCanton;
	}

	public void setInseeCanton(String inseeCanton) {
	    this.inseeCanton = inseeCanton;
	}

}
