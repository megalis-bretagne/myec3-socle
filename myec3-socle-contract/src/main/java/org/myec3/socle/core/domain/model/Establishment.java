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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents an Establishment of {@link Company}.<br>
 * 
 * This class is synchronized.<br>
 * 
 * This class is audited by global audit mechanism<br>
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 * 
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class Establishment extends Resource {

	private static final long serialVersionUID = -6213849679066931043L;

	private Boolean isHeadOffice;
	private Company company;

	private List<EmployeeProfile> employees = new ArrayList<EmployeeProfile>();

	private Boolean foreignIdentifier;
	private String nationalID;

	private String nic;
	private String siret;

	private String apeCode;
	private String apeNafLabel;
	private AdministrativeState administrativeState;

	private Address address;
	private String email;
	private String phone;
	private String fax;

	private Date lastUpdate;

	private Boolean diffusableInformations;

	@Transient
	private PaysImplantation pays;

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
	 * Default constructor
	 */
	public Establishment() {
		super();
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Initialize Establishment's name and label
	 * 
	 * @param name
	 * @param label
	 */
	public Establishment(String name, String label) {
		super(name, label);
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Initialize Establishment's name, label and Company
	 * 
	 * @param name
	 * @param label
	 * @param company
	 */
	public Establishment(String name, String label, Company company) {
		super(name, label);
		this.company = company;
	}

	/**
	 * Determine if the establishment is its company's head office
	 * 
	 * @return true if the establishment is the head office
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("siege_social")
	public Boolean getIsHeadOffice() {
		// return (isHeadOffice &= (isHeadOffice != null)); // isHeadOffice == null <=>
		// isHeadOffice == false
		return this.isHeadOffice;
	}

	public void setIsHeadOffice(Boolean isHeadOffice) {
		this.isHeadOffice = isHeadOffice;
	}

	/**
	 * Get the the {@link Company} of the {@link Establishment}.
	 * 
	 * @return the {@link Company} that owns the {@link Establishment}
	 */
	@ManyToOne
	@NotNull
	@Valid
	@JoinColumn(nullable = false)
	@XmlElement(required = true)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * Get the list of the employees of the {@link Establishment}.
	 * 
	 * @return list of employees of the current {@link Establishment}.
	 */
	@OneToMany(mappedBy = "establishment")
	@XmlTransient
	public List<EmployeeProfile> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeProfile> employees) {
		this.employees = employees;
	}

	/**
	 * Determine if the establishment is an foreign establishment.
	 * 
	 * @return true if it's a foreign establishment
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Boolean getForeignIdentifier() {
		return foreignIdentifier;
	}

	public void setForeignIdentifier(Boolean foreignIdentifier) {
		this.foreignIdentifier = foreignIdentifier;
	}

	/**
	 * For foreign companies, in this case {@link #getNic()} is null
	 * 
	 * @return the national id of the company
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getNationalID() {
		return nationalID;
	}

	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
	}

	/**
	 * NIC code is a five character code. <br>
	 * 
	 * It is used to identify an {@link Establishment} inside a {@link Company}.<br>
	 * 
	 * {@link Establishment}'s SIRET = {@link Company}'s SIREN + NIC
	 *
	 * @return NIC five character code
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getNic() {
		return nic;
	}

	/**
	 * Set {@link Establishment}'s NIC and update its SIRET accordingly.
	 * 
	 * @param nic
	 */
	public void setNic(String nic) {
		this.nic = nic;
		// updateSiretFromNic(this.nic);
	}

	/**
	 * SIRET code identify an Establishment. <br>
	 * 
	 * {@link Establishment}'s SIRET = {@link Company}'s SIREN + NIC
	 *
	 * @return SIRET code
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getSiret() {
		return siret;
	}

	/**
	 * Set {@link Establishment}'s SIRET and update its NIC accordingly.
	 * 
	 * @param nic
	 */
	public void setSiret(String siret) {
		this.siret = siret;
		// updateNicFromSiret(this.siret);
	}

	/**
	 * APE code (issued by INSEE) of the {@link Establishment}
	 * 
	 * @return the ape code
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("naf")
	public String getApeCode() {
		return apeCode;
	}

	public void setApeCode(String apeCode) {
		this.apeCode = apeCode;
	}

	/**
	 * NAF label (label of APE code) of the {@link Establishment}
	 * 
	 * @return NAF label
	 */
	@JsonProperty("libelle_naf")
	public String getApeNafLabel() {
		return apeNafLabel;
	}

	public void setApeNafLabel(String apeNafLabel) {
		this.apeNafLabel = apeNafLabel;
	}

	/**
	 * Administrative status of the {@link Establishment}. Should be a valid
	 * {@link AdministrativeState} object.
	 * 
	 * @return the {@link AdministrativeState} of the {@link Establishment}
	 */
	@Valid
	@Embedded
	@Column(nullable = true)
	@XmlElement(required = false)
	public AdministrativeState getAdministrativeState() {
		return administrativeState;
	}

	public void setAdministrativeState(AdministrativeState administrativeState) {
		this.administrativeState = administrativeState;
	}

	/**
	 * Address of this {@link Establishment}. Should be a valid address object.
	 * Cannot be null.
	 * 
	 * @return the address for this {@link Establishment}.
	 */
	@NotNull
	@Valid
	@Embedded
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("adresse")
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * Email of the {@link Establishment}. Should be compliant with the format
	 * defined by {@link @email} annotation and cannot be null
	 * 
	 * @return the {@link Establishment} email
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
	 * Phone number for the {@link Establishment}. No particular format.
	 * 
	 * @return the {@link Establishment} phone number
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
	 * Fax number for the {@link Establishment}. No particular format.
	 * 
	 * @return the {@link Establishment} fax number
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
	 * Method that adds a new employee to the {@link Establishment}.
	 * 
	 * @param employee employee to add
	 * @return updated {@link Establishment}.
	 */
	public Establishment addEmployee(EmployeeProfile employee) {
		this.employees.add(employee);
		return this;
	}

	/**
	 * Method that adds a list of employees to the {@link Establishment}.
	 * 
	 * @param employees list of employee to add
	 * @return updated {@link Establishment}.
	 */
	public Establishment addEmployees(List<EmployeeProfile> employees) {
		this.employees.addAll(employees);
		return this;
	}

	/**
	 * Method that removes an employee from the employees of the
	 * {@link Establishment}.
	 * 
	 * @param employee employee to remove
	 * @return updated {@link Establishment}.
	 */
	public Establishment removeEmployee(EmployeeProfile employee) {
		this.employees.remove(employee);
		return this;
	}

	/**
	 * Method that clears all the employees of the {@link Establishment}.
	 * 
	 * @return updated {@link Establishment}.
	 */
	public Establishment clearEmployees() {
		this.employees.clear();
		return this;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	@JsonProperty("date_mise_a_jour")
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date date) {
		this.lastUpdate = date;
	}

	@JsonSetter
	public void setLastUpdate(Long timestamp) {
		Timestamp finalTimeStamp = new Timestamp(timestamp * 1000);
		this.lastUpdate = new Date(finalTimeStamp.getTime() + (finalTimeStamp.getNanos() / 1000000));
	}

	@Column(nullable = false)
	@XmlElement(required = false)
	@JsonProperty("diffusable_commercialement")
	public Boolean getDiffusableInformations() {
		return this.diffusableInformations;
	}

	public void setDiffusableInformations(Boolean diffusableInformations) {
		this.diffusableInformations = diffusableInformations;
	}

	private void updateSiretFromNic(String nic) {
		siret = company.getSiren() + nic;
	}

	private void updateNicFromSiret(String siret) {
		nic = siret.substring(siret.length() - 5);
	}

	@XmlElement(required = false)
	@JsonProperty("pays_implantation")
	@Transient
	public PaysImplantation getPays() {
		return pays;
	}

	public void setPays(PaysImplantation pays) {
		this.pays = pays;
	}
}
