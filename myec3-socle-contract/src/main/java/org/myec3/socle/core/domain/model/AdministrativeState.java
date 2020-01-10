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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;


/**
 * This class represents an AdmnistrativeState that can be hold by a {@link Company} or an {@link Establishment}
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 *
 */
@Embeddable
public class AdministrativeState implements Serializable {
	
	private static final long serialVersionUID = 3060365032957830038L;
	
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	
	private AdministrativeStateValue adminStateValue;
	private Date adminStateLastUpdated;
	
	public AdministrativeState() {
		adminStateLastUpdated = new Date();
	}
	
	public AdministrativeState(AdministrativeStateValue value, Date lastUpdated) {
		this.adminStateValue = value;
		this.adminStateLastUpdated = lastUpdated;
	}

	/**
	 * Return the value of the Administrative State of the entity.
	 * 
	 * <br>
	 * 
	 * Can be either {@link AdministrativeState#STATUS_ACTIVE} or {@link AdministrativeState#STATUS_INACTIVE}
	 * 
	 * @return the administrative state value of the entity
	 */
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	@XmlElement(required = false)
	public AdministrativeStateValue getAdminStateValue() {
		return adminStateValue;
	}

	public void setAdminStateValue(AdministrativeStateValue value) {
		this.adminStateValue = value;
	}

	/**
	 * Return the date of the last Administrative State update
	 * @return the date of the last update
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Date getAdminStateLastUpdated() {
		return adminStateLastUpdated;
	}

	public void setAdminStateLastUpdated(Date lastUpdated) {
		this.adminStateLastUpdated = lastUpdated;
	}

	@JsonSetter
	public void setAdminStateLastUpdated(Long timestamp) {
	    	Timestamp finalTimeStamp = new Timestamp(timestamp*1000);
		this.adminStateLastUpdated = new Date(finalTimeStamp.getTime() + (finalTimeStamp.getNanos() / 1000000));
	}
	
	/**
	 * Return the last updated date with the format 'dd/MM/yyyy'
	 * @return the formated date as a String
	 */
	@Transient
	public String getFormatedAdminStateLastUpdated() {
		return getFormatedAdminStateLastUpdated(DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Return a string that represents the formated last updated date.
	 * @param format :
	 * 			a String that represents a valid {@link SimpleDateFormat} pattern
	 * @return the formated date
	 */
	@Transient
	public String getFormatedAdminStateLastUpdated(String format) {
		return new SimpleDateFormat(format).format(adminStateLastUpdated);
	}

}
