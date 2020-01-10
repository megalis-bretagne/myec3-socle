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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

/**
 * This class represents a user profile that is in reality an employee of a
 * {@link Company}. This class extend generic profile class.<br/>
 * 
 * This class is synchronized. <br />
 * 
 * This class is audited by global audit mechanism.<br/>
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class EmployeeProfile extends Profile {

	private static final long serialVersionUID = -1745419540038964872L;

	private CompanyDepartment companyDepartment;
	private Establishment establishment;
	private String nic;
	private Boolean hasEstablishmentChanged;

	/**
	 * Default constructor. Do nothing.
	 */
	public EmployeeProfile() {
		super();
	}

	/**
	 * Contructor. Initialize the employee name
	 * 
	 * @param name : name of the employee
	 */
	public EmployeeProfile(String name) {
		super(name);
	}

	/**
	 * Constructor. Initialize the employee name, label, email and function
	 * 
	 * @param name     : name of the employee
	 * @param label    : label of the employee
	 * @param email    : email of the employee
	 * @param function : function of the employee
	 */
	public EmployeeProfile(String name, String label, String email, String function) {
		super(name, label, email, function);
	}

	/**
	 * Constructor. Initialize the employee name, label, email, function, user,
	 * elected, organismDepartment and establishment
	 * 
	 * @param name              : name of the employee
	 * @param label             : label of the employee
	 * @param email             : email of the employee
	 * @param function          : function of the employee
	 * @param user              : real user associated to this employee
	 * @param companyDepartment : department of the employee
	 * @param establishment     : establishment of the employee
	 */
	public EmployeeProfile(String name, String label, String email, String function, User user,
			CompanyDepartment companyDepartment, Establishment establishment) {
		super(name, label, email, function, user);
		this.companyDepartment = companyDepartment;
		this.establishment = establishment;
	}

	/**
	 * Department of the employee that the profile is attached to
	 * 
	 * @return the department of the profile
	 */
	@ManyToOne
	@XmlElement(required = true)
	@JoinColumn(nullable = false)
	@NotNull
	@Valid
	public CompanyDepartment getCompanyDepartment() {
		return companyDepartment;
	}

	public void setCompanyDepartment(CompanyDepartment companyDepartment) {
		this.companyDepartment = companyDepartment;
	}

	/**
	 * {@link Establishment} of the employee that the profile is attached to
	 * 
	 * @return the {@link Establishment} of the profile
	 */
	@ManyToOne
	@XmlElement(required = false)
	@JoinColumn(nullable = true)
	@Valid
	public Establishment getEstablishment() {
		return establishment;
	}

	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}

	/**
	 * NIC Number of the employee profile.
	 * 
	 * @return NIC number (a five-numbers code)
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	/**
	 * @return true - profile is currently an employee profile
	 */
	@Override
	@Transient
	public boolean isEmployee() {
		return Boolean.TRUE;
	}

	@Transient
	public Boolean getHasEstablishmentChanged() {
		return this.hasEstablishmentChanged;
	}

	public void setHasEstablishmentChanged(Boolean hasEstablishmentChanged) {
		this.hasEstablishmentChanged = hasEstablishmentChanged;
	}

	/**
	 * @return false - profile is currently an employee profile
	 */
	@Override
	@Transient
	public boolean isAgent() {
		return Boolean.FALSE;
	}

	/**
	 * @return false - profile is currently an agent profile
	 */
	@Override
	@Transient
	public boolean isAdmin() {
		return Boolean.FALSE;
	}

	/**
	 * Determines if the profile is enabled. i.e. if the profile holders can access
	 * to applications. Cannot be null.
	 * 
	 * @return true if the profile is enabled, false otherwise
	 */
	@Override
	@Transient
	public boolean isEnabled() {
		return super.getEnabled();
	}

	@Override
	public String toString() {
		return "EmployeeProfile [companyDepartment=" + companyDepartment + ", establishment=" + establishment + ", nic="
				+ nic + "]";
	}

}
