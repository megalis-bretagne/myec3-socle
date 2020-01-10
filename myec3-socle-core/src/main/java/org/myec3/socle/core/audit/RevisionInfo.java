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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * This class is used to implement an audit on tables annoted by the annotation
 * 
 * @Audited.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Entity
@RevisionEntity(AuditListener.class)
public class RevisionInfo {

	private Long id;
	private Long timestamp;
	private Date revisionDate;
	private String modifiedBy;

	/**
	 * Id of the RevisionInfo
	 * 
	 * @return
	 */
	@Id
	@GeneratedValue
	@RevisionNumber
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Timestamp of the revision
	 * 
	 * @return
	 */
	@RevisionTimestamp
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Revision date of the Revision
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	/**
	 * Username of the personn that modified the object
	 * 
	 * @return
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
