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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import org.myec3.socle.core.domain.model.enums.Country;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents an address that can be hold by different model objects
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 *
 */
@Embeddable
public class Address implements Serializable {

	private static final long serialVersionUID = -6828812686561183801L;

	// Fields needed for MPS parsing
	private String streetNumber;
	private String streetType;
	private String streetName;
	private String insee;

	private String postalAddress;
	private String postalCode;
	private String city;
	private String canton;
	private Country country;

	private String localityAddress;
	private String additionalInfoAddress;

	/**
	 * String containing the street number given by MPS. Can be null Full address is
	 * compossed of streetNumber, streetName and streetExtraInfos.
	 *
	 * @return the street number
	 */
	@Transient
	@JsonProperty("numero_voie")
	public String getStreetNumber() {
		return this.streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	/**
	 * String containing the street name given by MPS. Can be null Full address is
	 * compossed of streetNumber, streetName and streetExtraInfos.
	 *
	 * @return the street number
	 */
	@Transient
	@JsonProperty("type_voie")
	public String getStreetType() {
		return this.streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}

	/**
	 * String containing the street name given by MPS. Can be null Full address is
	 * compossed of streetNumber, streetName and streetExtraInfos.
	 *
	 * @return the street number
	 */
	@Transient
	@JsonProperty("nom_voie")
	public String getStreetName() {
		return this.streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	@XmlElement(required = false)
	@JsonProperty("code_insee_localite")
	public String getInsee() {
		return insee;
	}

	public void setInsee(String insee) {
		this.insee = insee;
	}

	/**
	 * String containing the entire postal address. Cannot be null
	 *
	 * @return the postal address
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	/**
	 * String containing the postal code. Format not defined. Cannot be null
	 *
	 * @return the postal code
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("code_postal")
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * String containing the city name. Format not defined. Cannot be null
	 *
	 * @return the city name
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("localite")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * String containing the name of the canton. Format not defined.
	 *
	 * @return the name of the canton
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getCanton() {
		return canton;
	}

	public void setCanton(String canton) {
		this.canton = canton;
	}

	/**
	 * String containing the name of the country. Format not defined.
	 *
	 * @return the name of the country
	 */
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	@XmlElement(required = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * String containing the name of the "Boite Postal" or any additionnal
	 * information. Format not defined.
	 *
	 * @return the name of the "Boite Postal" or any additionnal information.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getAdditionalInfoAddress() {
		return additionalInfoAddress;
	}

	public void setAdditionalInfoAddress(String additionalInfoAddress) {
		this.additionalInfoAddress = additionalInfoAddress;
	}

	/**
	 * String containing the name of the "Lieu dit". Format not defined.
	 *
	 * @return the name of the "Lieu dit"
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getLocalityAddress() {
		return localityAddress;
	}

	public void setLocalityAddress(String localityAddress) {
		this.localityAddress = localityAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((postalAddress == null) ? 0 : postalAddress.hashCode());
		result = prime * result
				+ ((postalCode == null) ? 0 : postalCode.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Two addresses are equals if only all its field are equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Address other = (Address) obj;
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		if (postalAddress == null) {
			if (other.postalAddress != null) {
				return false;
			}
		} else if (!postalAddress.equals(other.postalAddress)) {
			return false;
		}
		if (postalCode == null) {
			if (other.postalCode != null) {
				return false;
			}
		} else if (!postalCode.equals(other.postalCode)) {
			return false;
		}
		return true;
	}

	/*
	 * Duplicate the object to prevent the same reference
	 * 
	 * @return the new object with same information
	 */
	public Address clone() {
		Address address = new Address();
		address.setPostalCode(postalCode);
		address.setCity(city);
		address.setPostalAddress(postalAddress);
		address.setCountry(country);
		address.setCanton(canton);
		address.setInsee(insee);
		address.setAdditionalInfoAddress(additionalInfoAddress);
		address.setLocalityAddress(localityAddress);
		address.setStreetName(streetName);
		address.setStreetNumber(streetNumber);
		address.setStreetType(streetType);

		return address;
	}
}
