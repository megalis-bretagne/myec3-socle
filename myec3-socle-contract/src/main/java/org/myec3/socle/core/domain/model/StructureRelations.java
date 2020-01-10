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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;

/**
 * This class allows to marshall parentStructures and childStructures of a
 * {@link Structure} in order to customize the XML send at the remote
 * {@link Application}.
 * 
 * This class is used by two adapters : ChildStructuresAdapter.class and
 * ParentStructuresAdapter.class
 * 
 * @see Structure.class
 * @see ChildStructuresAdapter.class
 * @see ParentStructuresAdapter.class
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public class StructureRelations {

	public static final String CHILD_RELATION_TYPE = "child";
	public static final String PARENT_RELATION_TYPE = "parent";

	private Long id;
	private String url;
	private StructureTypeValue structureType;
	// maps each member of this list to an XML element named parentStructure
	private List<StructureRelations> parentRelations = null;
	// maps each member of this list to an XML element named childStructure
	private List<StructureRelations> childRelations = null;

	/**
	 * Default constructor of StructureRelations Object. Do nothing.
	 */
	public StructureRelations() {
	}

	/**
	 * Constructor. Initialize id, url and the structureType.
	 * 
	 * @param id
	 *            : id of the structure relations
	 * @param url
	 *            : url of the structure relations
	 * @param structureType
	 *            : the structure type
	 */
	public StructureRelations(Long id, String url,
			StructureTypeValue structureType) {
		this.id = id;
		this.url = url;
		this.structureType = structureType;
	}

	/**
	 * Constructor. Initialize listRelations and structureTypeRelation.
	 * 
	 * @param listRelations
	 *            : list of structure relations
	 * @param structureTypeRelation
	 *            : structure type relation
	 */
	public StructureRelations(List<StructureRelations> listRelations,
			String structureTypeRelation) {
		if (structureTypeRelation.equals(PARENT_RELATION_TYPE)) {
			this.parentRelations = new ArrayList<StructureRelations>();
			this.parentRelations = listRelations;
		}
		if (structureTypeRelation.equals(CHILD_RELATION_TYPE)) {
			this.childRelations = new ArrayList<StructureRelations>();
			this.childRelations = listRelations;
		}
	}

	/**
	 * @return the UID of the {@link Structure} contained into the relation.
	 */
	@XmlElement(required = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the url corresponding at the REST method to retrieve the
	 *         {@link Company} or the {@link Organism}
	 */
	@XmlElement(required = false)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the {@link StructureType} associated.
	 */
	@XmlElement(required = false)
	public StructureTypeValue getStructureType() {
		return structureType;
	}

	public void setStructureType(StructureTypeValue structureType) {
		this.structureType = structureType;
	}

	/**
	 * @return the list of parent {@link Structure} associated at the current
	 *         {@link Structure}
	 */
	@XmlElement(name = "parentStructure", required = false)
	public List<StructureRelations> getParentRelations() {
		return parentRelations;
	}

	public void setParentRelations(List<StructureRelations> parentRelations) {
		this.parentRelations = parentRelations;
	}

	/**
	 * @return the list of child {@link Structure} associated at the current
	 *         {@link Structure}
	 */
	@XmlElement(name = "childStructure", required = false)
	public List<StructureRelations> getChildRelations() {
		return childRelations;
	}

	public void setChildRelations(List<StructureRelations> childRelations) {
		this.childRelations = childRelations;
	}
}
