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
package org.myec3.socle.core.domain.model.enums;

import java.util.EnumMap;

/**
 * Enum that contains a list of available civilities.
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
public enum Civility {

	MR("M."), MRS("Mme");

	private final String label;

	/**
	 * Constructor. Initialize the label of the civility.
	 * 
	 * @param label
	 *            : the label of the civility
	 */
	private Civility(String label) {
		this.label = label;
	}

	/**
	 * @return the label of the civility
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.label;
	}

	/**
	 * 
	 * @return
	 */
	public static EnumMap<Civility, String> getEnumMap() {
		EnumMap<Civility, String> statusMap = new EnumMap<Civility, String>(
				Civility.class);
		for (Civility type : Civility.values()) {
			statusMap.put(type, type.getLabel());
		}
		return statusMap;
	}

	/**
	 * 
	 * @param civilityValue
	 * @return
	 */
	public static Civility getTypeValue(String civilityValue) {
		for (Civility entry : Civility.values()) {
			if (entry.toString().equals(civilityValue)) {
				return entry;
			}
		}
		return null;
	}

}
