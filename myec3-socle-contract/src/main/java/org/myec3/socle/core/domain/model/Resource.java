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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * This class is the global abstract and root class defining any other object.
 * It is designed to be extended by concrete classes and provide common fields
 * and methods to allow generic behaviours and functionalities<br/>
 * This class is audited by global audit mechanism
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Audited
@MappedSuperclass
public class Resource implements Serializable, Cloneable, PE {

	private static final long serialVersionUID = -5529209148186545457L;

	private Long id;
	private Long externalId;
	private String name;
	private String label;


	// /**
	// * Allows to force an resource update on many to many relations
	// */
	// private Long updated = Long.valueOf(0);

	/**
	 * Default constructor. Do nothing.
	 */
	public Resource() {
	}

	/**
	 * Contructor. Initialize the resource name
	 * 
	 * @param name : name of the resource
	 */
	public Resource(String name) {
		this.name = name;
	}

	/**
	 * Constructor. Initialize resource name and label
	 * 
	 * @param name  : name of the resource
	 * @param label : label of the resource
	 */
	public Resource(String name, String label) {
		this.name = name;
		this.label = label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource clone() throws CloneNotSupportedException {
		return (Resource) super.clone();
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Technical id of the resource
	 * 
	 * @return the id for this resource
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlElement(required = true)
	public Long getId() {
		return id;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	/**
	 * Id for this resource, if the resource is originally coming from an external
	 * system or application in order to be able to perform synchronizations
	 * 
	 * @return the external id for this resource
	 */
	@XmlElement(required = true)
	public Long getExternalId() {
		if ((externalId == null) || (externalId.equals(Long.valueOf(0)))) {
			return this.getId();
		}
		return externalId;
	}

	/**
	 * Technical and internal name of the resource. Cannot be null.
	 * 
	 * @return the name of the resource
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("raison_sociale")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Label of the resource to be displayed.
	 * 
	 * @return the label for this resource.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getLabel() {
		return null != label ? label : name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// /**
	// * Determines if the resource has been updated
	// *
	// * @return true if the resource has been updated, false otherwise
	// */
	// @Column(nullable = true)
	// @XmlTransient
	// public Long getUpdated() {
	// return updated;
	// }
	//
	// public void setUpdated(Long updated) {
	// this.updated = updated;
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Two resources are equals if their ids are equals
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

		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Resource [").append("id=").append(this.getId()).append(",").append("name=").append(this.getName())
				.append(",").append("label=").append(this.getLabel()).append("]");
		return sb.toString();
	}
}
