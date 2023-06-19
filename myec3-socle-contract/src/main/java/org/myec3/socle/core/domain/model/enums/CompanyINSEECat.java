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

	//https://public.opendatasoft.com/explore/dataset/categories-juridiques-insee/export/

	ASSOCIATION("Association","9220"),
	AUTRE("Autre",""),
	EARL("EARL",""),
	EURL("EURL","5488"),
	EI("EI","1"),
	GIE("GIE","6220"),
	GROUPEMENT_D_ENTREPRISE("Groupement d\'entreprise","62"),
	SA("SA","56"),
	SARL("SARL","5499"),
	SAS("SAS","5710"),
	SASU("SASU","5720"),
	SCOP("SCOP","5458"),
	SNC("SNC","5203");

	private final String label;

	private final String code;

	/**
	 * Constructor. Initialize the INSEE category.
	 * 
	 * @param label
	 *            : the INSEE category to set
	 */
	private CompanyINSEECat(String label,String code) {
		this.label = label;this.code = code;
	}

	/**
	 * @return a string value containing the INSEE category
	 */
	public String getLabel() {
		return label;
	}


	public String getCode() {
		return code;
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

	public static CompanyINSEECat getByCode(String code){
		for (final CompanyINSEECat element : EnumSet.allOf(CompanyINSEECat.class)) {
			if (element.getCode().equals(code)) {
				return element;
			}
		}
		return CompanyINSEECat.AUTRE;
	}
}
