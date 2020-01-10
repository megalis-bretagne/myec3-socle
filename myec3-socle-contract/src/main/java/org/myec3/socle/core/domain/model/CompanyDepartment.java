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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

/**
 * This class represents a {@link Department} of a {@link Company}.
 * 
 * This class is synchronized.<br />
 * 
 * This class extend generic {@link Department} class.<br />
 * 
 * This class is audited by global audit mechanism<br/>
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class CompanyDepartment extends Department {

	private static final long serialVersionUID = 7703755701544089071L;

	private Company company;
	private CompanyDepartment parentDepartment;
	private Integer size;
	private String registerTown;
	private String nic;
	private List<EmployeeProfile> employees = new ArrayList<EmployeeProfile>();

	/**
	 * Default constructor. Do nothing.
	 */
	public CompanyDepartment() {
		super();
	}

	/**
	 * Contructor. Initialize the companyDepartement's name and label
	 * 
	 * @param name  : the name of the departement
	 * @param label : the label of the departement
	 */
	public CompanyDepartment(String name, String label) {
		super(name, label);
	}

	/**
	 * Contructor. Initialize the companyDepartement's name, label and company
	 * 
	 * @param name    : the name of the departement
	 * @param label   : the label of the departement
	 * @param company : the company of the departement
	 */
	public CompanyDepartment(String name, String label, Company company) {
		super(name, label);
		this.company = company;
	}

	/**
	 * Get the size of the departement
	 * 
	 * @return the departement size
	 */
	@Column(name = "departmentSize", nullable = true)
	@XmlElement(required = false)
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * Get the register town of the CompanyDepartement.
	 * 
	 * @return the registered town
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getRegisterTown() {
		return registerTown;
	}

	public void setRegisterTown(String registerTown) {
		this.registerTown = registerTown;
	}

	/**
	 * Get the the {@link Company} of the CompanyDepartement.
	 * 
	 * @return the company belongs to the department.
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
	 * Code NIC of the companyDepartement.
	 * 
	 * @return five-character code.
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
	 * A parent departement of the CompanyDepartement.
	 * 
	 * @return the parent departement of the current company department.
	 */
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlElement(required = false)
	public CompanyDepartment getParentDepartment() {
		return parentDepartment;
	}

	public void setParentDepartment(CompanyDepartment parentDepartment) {
		this.parentDepartment = parentDepartment;
	}

	/**
	 * Get the list of the employees of the CompanyDepartement.
	 * 
	 * @return list of employees of the current company department.
	 */
	@OneToMany(mappedBy = "companyDepartment")
	@XmlTransient
	public List<EmployeeProfile> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeProfile> employees) {
		this.employees = employees;
	}

	/**
	 * Method that adds a new employee to the CompanyDepartement.
	 * 
	 * @param employee to add
	 * @return updated CampanyDepartement.
	 */
	public CompanyDepartment addEmployee(EmployeeProfile employee) {
		this.employees.add(employee);
		return this;
	}

	/**
	 * Method that adds a liste of employees to the CompanyDepartement.
	 * 
	 * @param list of employee to add
	 * @return updated CampanyDepartement.
	 */
	public CompanyDepartment addEmployees(List<EmployeeProfile> employees) {
		this.employees.addAll(employees);
		return this;
	}

	/**
	 * Method that removes an employee from the employees of the CompanyDepartement.
	 * 
	 * @param list of employee to add
	 * @return updated CampanyDepartement.
	 */
	public CompanyDepartment removeEmployee(EmployeeProfile employee) {
		this.employees.remove(employee);
		return this;
	}

	/**
	 * Method that clears all the employees of the CompanyDepartement.
	 * 
	 * @return updated CampanyDepartement.
	 */
	public CompanyDepartment clearEmployees() {
		this.employees.clear();
		return this;
	}
}
