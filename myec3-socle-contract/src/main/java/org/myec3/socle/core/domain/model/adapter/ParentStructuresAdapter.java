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
package org.myec3.socle.core.domain.model.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureRelations;
import org.myec3.socle.core.domain.model.constants.MyEc3AdapterConstants;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;

/**
 * This class allows to override the marshal and unmarshal methods for the
 * attribute "parentStructures" of {@link Structure} class in order to choose
 * the attributes to send into the XML
 * 
 * To use this adapter you must add the annotation :
 * 
 * @XmlJavaTypeAdapter(ParentStructuresAdapter.class) over the attribute to send
 *                                                    into the XML
 * @see Structure.class
 * @see StructureRelations.class
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ParentStructuresAdapter extends
		XmlAdapter<StructureRelations, List<Structure>> {

	public static final String PARENT_RELATION_TYPE = "parent";

	/**
	 * Convert JAVA to XML
	 */
	@Override
	public StructureRelations marshal(List<Structure> listStructures)
			throws Exception {
		List<StructureRelations> parentRelationList = new ArrayList<StructureRelations>();

		if (listStructures != null) {
			for (Structure structure : listStructures) {
				if (structure instanceof Organism) {
					parentRelationList.add(new StructureRelations(structure
							.getId(), MyEc3AdapterConstants
							.getSocleServerOrganismUrl() + structure.getId(),
							StructureTypeValue.ORGANISM));
				}
				if (structure instanceof Company) {
					parentRelationList.add(new StructureRelations(structure
							.getId(), MyEc3AdapterConstants
							.getSocleServerCompanyUrl() + structure.getId(),
							StructureTypeValue.COMPANY));
				}
			}
		}

		if (parentRelationList.size() > 0) {
			return new StructureRelations(parentRelationList,
					PARENT_RELATION_TYPE);
		}

		return null;
	}

	/**
	 * Convert XML to JAVA.
	 * 
	 * @return an empty list of {@link Structure}. Not implemented.
	 */
	@Override
	public List<Structure> unmarshal(StructureRelations structureRelations)
			throws Exception {
		return new ArrayList<Structure>();
	}

}