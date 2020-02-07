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

import javax.xml.bind.annotation.XmlEnum;

/**
 * Enum that contains a list of organism's member status.
 *
 */
@XmlEnum
public enum OrganismMemberStatus {

    ADHERENT("Adhérent"), PROSPECT("Prospect"), PARTENAIRE("Partenaire"), RESILIE(
			"Résilié"), REFUS_D_ADHERER("Refus d\'adhérer"), EN_COURS_D_ADHESION(
			"En cours d\'adhésion"), DISSOLUTION("Dissolution"), TEST("Test");

	private final String label;

	private OrganismMemberStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
