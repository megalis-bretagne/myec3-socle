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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * This class represents a department of an organism ({@link OrganismDepartment}
 * ) or a company ({@link CompanyDepartment} ).<br />
 * 
 * This class is audited by global audit mechanism<br />
 * This class extends {@link Resource} class<br />
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class Department extends Resource {

	private static final long serialVersionUID = -509235474809439899L;

	private String acronym;
	private String description;
	private String email;
	private String phone;
	private String fax;
	private String website;
	private String siren;
	private Address address;

	@Getter
	@Setter
	@Column(nullable = false)
	@XmlTransient
	private Date createdDate;

	/**
	 * Id du user
	 */
	@Getter
	@Setter
	@Column()
	@XmlTransient
	private Long createdUserId;

	/**
	 * Default constructor. Do nothing.
	 */
	public Department() {
		super();
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Contructor. Initialize the department's name and label
	 * 
	 * @param name  : the name of the department
	 * @param label : the label of the department
	 */
	public Department(String name, String label) {
		super(name, label);
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * @retun the acronym of the department
	 */
	@NotNull
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * @retun the description of the department.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @retun the email of the department
	 */
	@NotNull
	@Email
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @retun the phone number of the department.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @retun the fax of the department.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @retun the website of the department.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @retun the SIREN number of the department (9 numbers).
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	/**
	 * @retun the {@link Address} of the department (postal Address, postal Code,
	 *        city, canton, country).
	 */
	@Embedded
	@NotNull
	@Valid
	@Column(nullable = false)
	@XmlElement(required = true)
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
