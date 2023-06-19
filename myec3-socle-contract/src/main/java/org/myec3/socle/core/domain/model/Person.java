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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.enums.Civility;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class describes the model of a Person. A person is an user who has no
 * rights on the platform it's just for information (i.e : responsibles of a
 * {@link Company}.
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Entity
@Audited
@XmlRootElement
public class Person extends Resource {

	private static final long serialVersionUID = 2936976151703876984L;

	private Civility civility;
	private String firstname;
	private String lastname;
	private String type;
	private String email;
	private String phone;
	private Company company;
	private String function;
	private String moralName;

	/**
	 * Default constructor. Do nothing.
	 */
	public Person() {
		super();
	}

	/**
	 * Constructor. Initialize civility, firstname and lastname of a person.
	 * 
	 * @param civility  : civility of the person
	 * @param firstname : firstname of the person
	 * @param lastname  : lastname of the person
	 */
	public Person(Civility civility, String firstname, String lastname) {
		super(firstname + " " + lastname);
		this.civility = civility;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * @return the civility of the person.
	 * @see Civility TODO : not used anymore as MPS doesn't return civility
	 */
	@Enumerated(EnumType.STRING)
	@XmlElement(required = false)
	@Column(nullable = true)
	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	/**
	 * @return the firstname of the person
	 */
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * 
	 * @return the lastname of the person
	 */
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 *
	 * @return the type of the person
	 */
	@Column(nullable = false)
	@XmlElement(required = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 
	 * @return the function of the person
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getFunction() {
		return function;
	}

	public void setFunction(String val) {
		this.function = val;
	}

	/**
	 * 
	 * @return the {@link Company} of the person
	 */
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlTransient
	@JsonIgnore
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public String getMoralName() {
		return this.moralName;
	}

	public void setMoralName(String moralName) {
		this.moralName = moralName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getLabel() == null) ? 0 : this.getLabel().hashCode());
		result = prime * result
				+ ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
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
		Person other = (Person) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}
