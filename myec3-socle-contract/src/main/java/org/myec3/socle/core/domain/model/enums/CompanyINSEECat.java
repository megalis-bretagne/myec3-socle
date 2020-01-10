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

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlEnum;


/**
 * Enum that contains a list of available Code INSEE Categories.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@XmlEnum
public enum CompanyINSEECat implements StructureINSEECat {

	ASSOCIATION("Association"), 
	AUTRE("Autre"), 
	EARL("EARL"), 
	EURL("EURL"), 
	EI("EI"), 
	GIE("GIE"), 
	GROUPEMENT_D_ENTREPRISE("Groupement d\'entreprise"), 
	SA("SA"), SARL("SARL"), 
	SAS("SAS"), 
	SASU("SASU"), 
	SCOP("SCOP"), 
	SNC("SNC");

	private final String label;

	/**
	 * Constructor. Initialize the INSEE category.
	 * 
	 * @param label
	 *            : the INSEE category to set
	 */
	private CompanyINSEECat(String label) {
		this.label = label;
	}

	/**
	 * @return a string value containing the INSEE category
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
	 * @return a value containing the INSEE category
	 * If the INSEE category doesn't exist, return AUTRE
	 */
	public static CompanyINSEECat getByValue(String value){
        for (final CompanyINSEECat element : EnumSet.allOf(CompanyINSEECat.class)) {
            if (element.toString().equals(value)) {
                return element;
            }
        }
        return CompanyINSEECat.AUTRE;
    }
}
