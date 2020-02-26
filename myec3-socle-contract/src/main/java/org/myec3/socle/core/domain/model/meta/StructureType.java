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
package org.myec3.socle.core.domain.model.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;

/**
 * 
 * Description of the object StructureType. It extends from {@link Resource}
 * object.
 * 
 * This class represents the type of a {@link Structure} (i.e : an
 * {@link Organism} or a {@link Company}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
public class StructureType implements Serializable, PE {

	private static final long serialVersionUID = -1002610177496081249L;

	private Long id;
	private StructureTypeValue value;
	private List<Structure> structures = new ArrayList<Structure>();
	private List<StructureTypeApplication> applications = new ArrayList<StructureTypeApplication>();

	/**
	 * Default constructor : call the {@link Resource} default constructor : do
	 * nothing
	 */
	public StructureType() {
	}

	/**
	 * Technical id of the StructureType
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
	 * @return the list of {@link Application} associated at this type of
	 *         {@link Structure}
	 */
	@OneToMany(mappedBy = "structureType")
	@JsonIgnore
	public List<StructureTypeApplication> getApplications() {
		return this.applications;
	}

	public void setApplications(List<StructureTypeApplication> applications) {
		this.applications = applications;
	}

	/**
	 * @return the list of {@link Structure} associated
	 */
	@OneToMany(mappedBy = "structureType")
	@JsonIgnore
	public List<Structure> getStructures() {
		return structures;
	}

	public void setStructures(List<Structure> structures) {
		this.structures = structures;
	}

	/**
	 * @return the value corresponding at this type of {@link Structure}
	 * 
	 * @see StructureTypeValue
	 */
	@Enumerated(EnumType.STRING)
	@Column(unique = true)
	public StructureTypeValue getValue() {
		return value;
	}

	public void setValue(StructureTypeValue value) {
		this.value = value;
	}

}