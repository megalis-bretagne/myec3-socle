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

import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * Enum that contains a list of available countries.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@XmlEnum
public enum Country {

	FR("France"), AD("Andorre"), AE("Émirats Arabes Unis"), AF("Afghanistan"), AG(
			"Antigua-Et-Barbuda"), AI("Anguilla"), AL("Albanie"), AM("Arménie"), AN(
			"Antilles Néerlandaises"), AO("Angola"), AQ("Antarctique"), AR(
			"Argentine"), AS("Samoa Américaines"), AT("Autriche"), AU(
			"Australie"), AW("Aruba"), AX("Âland, Îles"), AZ("Azerbaïdjan"), BA(
			"Bosnie-Herzégovine"), BB("Barbade"), BD("Bangladesh"), BE(
			"Belgique"), BF("Burkina Faso"), BG("Bulgarie"), BH("Bahreïn"), BI(
			"Burundi"), BJ("Bénin"), BM("Bermudes"), BN("Brunéi Darussalam"), BO(
			"Bolivie"), BR("Brésil"), BS("Bahamas"), BT("Bhoutan"), BV(
			"Bouvet, Île"), BW("Botswana"), BY("Bélarus"), BZ("Belize"), CA(
			"Canada"), CC("Cocos (Keeling), Îles"), CD(
			"Congo, La République Démocratique Du"), CF(
			"Centrafricaine, République"), CG("Congo"), CH("Suisse"), CI(
			"Côte D'Ivoire"), CK("Cook, Îles"), CL("Chili"), CM("Cameroun"), CN(
			"Chine"), CO("Colombie"), CR("Costa Rica"), CU("Cuba"), CV(
			"Cap-Vert"), CX("Christmas, Île"), CY("Chypre"), CZ(
			"Tchèque, République"), DE("Allemagne"), DJ("Djibouti"), DK(
			"Danemark"), DM("Dominique"), DO("Dominicaine, République"), DZ(
			"Algérie"), EC("Équateur"), EE("Estonie"), EG("Égypte"), EH(
			"Sahara Occidental"), ER("Érythrée"), ES("Espagne"), ET("Éthiopie"), FI(
			"Finlande"), FJ("Fidji"), FK("Falkland, Îles (Malvinas)"), FM(
			"Micronésie, États Fédérés De"), FO("Féroé, Îles"), GA("Gabon"), GB(
			"Royaume-Uni"), GD("Grenade"), GE("Géorgie"), GG("Guernesey"), GH(
			"Ghana"), GI("Gibraltar"), GL("Groenland"), GM("Gambie"), GN(
			"Guinée"), GQ("Guinée Équatoriale"), GR("Grèce"), GS(
			"Géorgie Du Sud Et Les Îles Sandwich Du Sud"), GT("Guatemala"), GU(
			"Guam"), GW("Guinée-Bissau"), GY("Guyana"), HK("Hong-Kong"), HM(
			"Heard, Île Et Mcdonald, Îles"), HN("Honduras"), HR("Croatie"), HT(
			"Haïti"), HU("Hongrie"), ID("Indonésie"), IE("Irlande"), IL(
			"Israël"), IM("Île De Man"), IN("Inde"), IO(
			"Océan Indien, Territoire Britannique De L'"), IQ("Iraq"), IR(
			"Iran, République Islamique D'"), IS("Islande"), IT("Italie"), JE(
			"Jersey"), JM("Jamaïque"), JO("Jordanie"), JP("Japon"), KE("Kenya"), KG(
			"Kirghizistan"), KH("Cambodge"), KI("Kiribati"), KM("Comores"), KN(
			"Saint-Kitts-Et-Nevis"), KP(
			"Corée, République Populaire Démocratique De"), KR(
			"Corée, République De"), KW("Koweït"), KY("Caïmanes, Îles"), KZ(
			"Kazakhstan"), LA("Lao, République Démocratique Populaire"), LB(
			"Liban"), LC("Sainte-Lucie"), LI("Liechtenstein"), LK("Sri Lanka"), LR(
			"Libéria"), LS("Lesotho"), LT("Lituanie"), LU("Luxembourg"), LV(
			"Lettonie"), LY("Libyenne, Jamahiriya Arabe"), MA("Maroc"), MC(
			"Monaco"), MD("Moldova, République De"), ME("Monténégro"), MG(
			"Madagascar"), MH("Marshall, Îles"), MK(
			"Macédoine, L'Ex-République Yougoslave De"), ML("Mali"), MM(
			"Myanmar"), MN("Mongolie"), MO("Macao"), MP(
			"Mariannes Du Nord, Îles"), MR("Mauritanie"), MS("Montserrat"), MT(
			"Malte"), MU("Maurice"), MV("Maldives"), MW("Malawi"), MX("Mexique"), MY(
			"Malaisie"), MZ("Mozambique"), NA("Namibie"), NE("Niger"), NF(
			"Norfolk, Île"), NG("Nigéria"), NI("Nicaragua"), NL("Pays-Bas"), NO(
			"Norvège"), NP("Népal"), NR("Nauru"), NU("Niué"), NZ(
			"Nouvelle-Zélande"), OM("Oman"), PA("Panama"), PE("Pérou"), PG(
			"Papouasie-Nouvelle-Guinée"), PH("Philippines"), PK("Pakistan"), PL(
			"Pologne"), PN("Pitcairn"), PR("Porto Rico"), PS(
			"Palestinien Occupé, Territoire"), PT("Portugal"), PW("Palaos"), PY(
			"Paraguay"), QA("Qatar"), RO("Roumanie"), RS("Serbie"), RU(
			"Russie, Fédération De"), RW("Rwanda"), SA("Arabie Saoudite"), SB(
			"Salomon, Îles"), SC("Seychelles"), SD("Soudan"), SE("Suède"), SG(
			"Singapour"), SH("Sainte-Hélène"), SI("Slovénie"), SJ(
			"Svalbard Et Île Jan Mayen"), SK("Slovaquie"), SL("Sierra Leone"), SM(
			"Saint-Marin"), SN("Sénégal"), SO("Somalie"), SR("Suriname"), ST(
			"Sao Tomé-Et-Principe"), SV("El Salvador"), SY(
			"Syrienne, République Arabe"), SZ("Swaziland"), TC(
			"Turks Et Caïques, Îles"), TD("Tchad"), TG("Togo"), TH("Thaïlande"), TJ(
			"Tadjikistan"), TK("Tokelau"), TL("Timor-Leste"), TM("Turkménistan"), TN(
			"Tunisie"), TO("Tonga"), TR("Turquie"), TT("Trinité-Et-Tobago"), TV(
			"Tuvalu"), TW("Taïwan, Province De Chine"), TZ(
			"Tanzanie, République-Unie De"), UA("Ukraine"), UG("Ouganda"), UM(
			"Îles Mineures Éloignées Des États-Unis"), US("États-Unis"), UY(
			"Uruguay"), UZ("Ouzbékistan"), VA(
			"Saint-Siège (État De La Cité Du Vatican)"), VC(
			"Saint-Vincent-Et-Les Grenadines"), VE("Venezuela"), VG(
			"Îles Vierges Britanniques"), VI("Îles Vierges Des États-Unis"), VN(
			"Viet Nam"), VU("Vanuatu"), WS("Samoa"), YE("Yémen"), ZA(
			"Afrique Du Sud"), ZM("Zambie"), ZW("Zimbabwe");
	private final String label;

	private Country(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static EnumMap<Country, String> getEnumMap() {
		EnumMap<Country, String> statusMap = new EnumMap<Country, String>(
				Country.class);
		for (Country type : Country.values()) {
			statusMap.put(type, type.getLabel());
		}
		return statusMap;
	}

	public static Country getTypeValue(String countryValue) {
		for (Country entry : Country.values()) {
			if (entry.toString().equals(countryValue)) {
				return entry;
			}
		}
		return null;
	}
}
