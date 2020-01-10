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
package org.myec3.socle.webapp.propertyConduit;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.tapestry5.PropertyConduit;
import org.myec3.socle.core.domain.model.Structure;

/**
 * Custom implementation of propertyConduit for Relation between structures (in
 * order to make column sortable)
 * 
 * @author denis.cucchietti@atosorigin.com
 * 
 */
public class RelationPropertyConduit implements PropertyConduit {

	private List<Structure> listOfParentStructures;

	/**
	 * @see org.apache.tapestry5.PropertyConduit#get(java.lang.Object)
	 */
	@Override
	public Object get(Object instance) {
		Structure structureRelationRow = (Structure) instance;

		if (this.listOfParentStructures.contains(structureRelationRow)) {
			return "Appartient \u00E0";
		}

		return "Regroupe";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class getPropertyType() {
		return String.class;
	}

	@Override
	public void set(Object arg0, Object arg1) {
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> getListOfParentStructures() {
		return listOfParentStructures;
	}

	public void setListOfParentStructures(List<Structure> listOfParentStructures) {
		this.listOfParentStructures = listOfParentStructures;
	}
}
