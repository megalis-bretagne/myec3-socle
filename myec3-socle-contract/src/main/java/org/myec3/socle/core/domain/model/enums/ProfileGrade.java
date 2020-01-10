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
 * An enumeration of available profile graduation.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@XmlEnum
public enum ProfileGrade {

	LA_CHEF_DE_SERVICE("la Chef de service"), CONSEILLERE_COMMUNAUTAIRE(
			"la Conseillère communautaire"), CONSEILLERE_GENERALE(
			"la Conseillère générale"), CONSEILLERE_MUNICIPALE(
			"la Conseillère municipale"), CONSEILLERE_REGIONALE(
			"la Conseillère régionale"), DELEGUEE_REGIONALE(
			"la Déléguée régionale"), DEPUTEE("la Députée"), DEPUTEE_MAIRE(
			"la Députée-Maire"), DEUXIEME_ADJOINTE("la Deuxième adjointe"), DIRECTRICE(
			"la Directrice"), DIRECTRICE_ADJOINTE("la Directrice adjointe"), DIRECTRICE_DELEGUEE(
			"la Directrice déléguée"), DIRECTRICE_GENERALE(
			"la Directrice générale"), DIRECTRICE_GENERALE_ADJOINTE(
			"la Directrice générale adjointe"), DIRECTRICE_GENERALE_DES_SERVICES(
			"la Directrice générale des services"), LA_MAIRE("la Maire"), PREFETE(
			"la Préfète"), PREMIERE_ADJOINTE("la Première adjointe"), PRESIDENTE(
			"la Présidente"), PRINCIPALE("la Principale"), PROVISEURE(
			"la Proviseure"), SECRETAIRE_GENERALE("la Secrétaire générale"), SENATRICE(
			"la Sénatrice"), SENATRICE_MAIRE("la Sénatrice-Maire"), SOUS_PREFETE(
			"la Sous-Préfète"), TROISIEME_ADJOINTE("la Troisième adjointe"), VICE_PRESIDENTE(
			"la Vice-Présidente"), ADJOINT("l\'Adjoint"), ADJOINTE(
			"l\'Adjointe"), ADMINISTRATEUR("l\'Administrateur"), ADMINISTRATRICE(
			"l\'Administratrice"), LE_CHEF_DE_SERVICE("le Chef de service"), CONSEILLER_COMMUNAUTAIRE(
			"le Conseiller communautaire"), CONSEILLER_GENERAL(
			"le Conseiller général"), CONSEILLER_MUNICIPAL(
			"le Conseiller municipal"), CONSEILLER_REGIONAL(
			"le Conseiller régional"), DELEGUE_REGIONAL("le Délégué régional"), DEPUTE(
			"le Député"), DEPUTE_MAIRE("le Député-Maire"), DEUXIEME_ADJOINT(
			"le Deuxième adjoint"), DIRECTEUR("le Directeur"), DIRECTEUR_ADJOINT(
			"le Directeur adjoint"), DIRECTEUR_DELEGUE("le Directeur délégué"), DIRECTEUR_GENERAL(
			"le Directeur général"), DIRECTEUR_GENERAL_ADJOINT(
			"le Directeur général adjoint"), DIRECTEUR_GENERAL_DES_SERVICES(
			"le Directeur général des services"), MAIRE("le Maire"), PREFET(
			"le Préfet"), PREMIER_ADJOINT("le Premier adjoint"), PRESIDENT(
			"le Président"), PRINCIPAL("le Principal"), PROVISEUR(
			"le Proviseur"), SECRETAIRE_GENERAL("le Secrétaire général"), SENATEUR(
			"le Sénateur"), SENATEUR_MAIRE("le Sénateur-Maire"), SOUS_PREFET(
			"le Sous-Préfet"), TROISIEME_ADJOINT("le Troisième adjoint"), VICE_PRESIDENT(
			"le Vice-Président");

	private final String label;

	private ProfileGrade(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
