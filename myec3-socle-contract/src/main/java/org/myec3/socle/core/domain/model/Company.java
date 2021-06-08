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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.enums.CompanyCategory;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.StructureINSEECat;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents a Company. A company can have one or many departements
 * and resposibles. This class extends abstract class ({@link Structure})<br />
 *
 * This class is synchronized.<br />
 *
 * This class is audited by global audit mechanism<br />
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 *
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class Company extends Structure {

	private static final long serialVersionUID = 2797441818971406424L;
	private Boolean foreignIdentifier;
	// FIXME add enum instead of 2 strings, be careful of sync. existing
	// applications
	private String apeCode;
	private String apeNafLabel;
	private AdministrativeState administrativeState;
	private Country registrationCountry;
	private CompanyINSEECat legalCategory;
	private String nic;
	private String siretHeadOffice;
	private String nationalID;
	private Boolean rm;
	private Boolean rcs;
	private String insee;
	private Date lastUpdate;
	private Date creationDate;
	private Date radiationDate;
	private List<CompanyDepartment> departments = new ArrayList<>();
	private List<Establishment> establishments = new ArrayList<>();
	private List<Person> responsibles = new ArrayList<>();
	private String companyAcronym;
	private CompanyCategory companyCategory;
	private String legalCategoryString;
	private Boolean diffusableInformations;

	/**
	 * Default constructor. Do nothing.
	 */
	public Company() {
		super();
	}

	/**
	 * ape code (issued by INSEE)
	 *
	 * @return the ape code (five-character code)
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	@JsonProperty("forme_juridique_code")
	public String getApeCode() {
		return apeCode;
	}

	public void setApeCode(String apeCode) {
		this.apeCode = apeCode;
	}

	/**
	 * naf label (label of ape code) of the company
	 *
	 * @return business label
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public void setApeNafLabel(String apeNafLabel) {
		this.apeNafLabel = apeNafLabel;
	}

	public String getApeNafLabel() {
		return apeNafLabel;
	}

	/**
	 * Administrative status of the {@link Company}. Should be a valid
	 * {@link AdministrativeState} object.
	 *
	 * @return the {@link AdministrativeState} of the {@link Company}
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
	 * Determine if the company is an foreign company.
	 *
	 * @return true if it's a foreign company
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
	 * Registration Country
	 *
	 * @return country code of the company
	 */
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlElement(required = true)
	public Country getRegistrationCountry() {
		return registrationCountry;
	}

	public void setRegistrationCountry(Country registrationCountry) {
		this.registrationCountry = registrationCountry;
	}

	/**
	 * business categories
	 *
	 * @return category of enum {@link CompanyINSEECat}
	 */
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlElement(required = true)
	public CompanyINSEECat getLegalCategory() {
		return legalCategory;
	}

	public void setLegalCategory(CompanyINSEECat legalCategory) {
		this.legalCategory = legalCategory;
	}

	@Transient
	@JsonProperty("forme_juridique")
	public String getLegalCategoryString() {
		return this.legalCategoryString;
	}

	public void setLegalCategoryString(String legalCategory) {
		this.legalCategoryString = legalCategory;
	}

	/**
	 * Return Head Office's NIC code. <br>
	 *
	 * See {@link Establishment#getNic()}
	 *
	 * @return NIC code
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getNic() {
		return nic;
	}

	/**
	 * Set Head Office's NIC code. <br>
	 *
	 * See {@link Establishment#setNic(String)}
	 *
	 * @param nic
	 */
	public void setNic(String nic) {
		this.nic = nic;
	}

	@Transient
	@JsonProperty("siret_siege_social")
	public String getSiretHeadOffice() {
		return siretHeadOffice;
	}

	public void setSiretHeadOffice(String siretHeadOffice) {
		this.siretHeadOffice = siretHeadOffice;
	}

	/**
	 * for foreign companies, in this case {@link #getSiren()} and {@link #getNic()}
	 * are null
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
	 * @return true if the company is registered at the trade directory
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean getRM() {
		if (this.rm == null) {
			this.rm = Boolean.FALSE;
		}
		return this.rm;
	}

	public void setRM(Boolean rm) {
		this.rm = rm;
	}

	/**
	 * @return true if the company is registered at the register of Commerce and
	 *         Companies
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean getRCS() {
		if (this.rcs == null) {
			this.rcs = Boolean.FALSE;
		}
		return this.rcs;
	}

	public void setRCS(Boolean rcs) {
		this.rcs = rcs;
	}

	/**
	 * INSEE Code of the company
	 *
	 * @return the INSEE code of the company (a five-character code).
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getInsee() {
		return this.insee;
	}

	public void setInsee(String insee) {
		this.insee = insee;
	}

	/**
	 * The last update of the company. It corresponds to the last update of the head
	 * office
	 *
	 * @return last update date
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	@JsonSetter
	public void setLastUpdate(Long timestamp) {
		Timestamp finalTimeStamp = new Timestamp(timestamp * 1000);
		this.lastUpdate = new Date(finalTimeStamp.getTime() + (finalTimeStamp.getNanos() / 1000000));
	}

	public void setLastUpdate(Date date) {
		this.lastUpdate = date;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	@JsonProperty("date_creation")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSetter
	public void setCreationDate(Long timestamp) {
		Timestamp finalTimeStamp = new Timestamp(timestamp * 1000);
		this.creationDate = new Date(finalTimeStamp.getTime() + (finalTimeStamp.getNanos() / 1000000));
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public Date getRadiationDate() {
		return radiationDate;
	}

	public void setRadiationDate(Date radiationDate) {
		this.radiationDate = radiationDate;
	}

	@JsonSetter
	public void setRadiationDate(Long timestamp) {
		if (timestamp > 0) {
			Timestamp finalTimeStamp = new Timestamp(timestamp * 1000);
			this.radiationDate = new Date(finalTimeStamp.getTime() + (finalTimeStamp.getNanos() / 1000000));
		}
	}

	/**
	 * All the departements of the company.
	 *
	 * @return list of departements of the company.
	 */
	@OneToMany(mappedBy = "company", cascade = { CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.FALSE)
	@XmlTransient
	@JsonIgnore
	public List<CompanyDepartment> getDepartments() {
		return departments;
	}

	public void setDepartments(List<CompanyDepartment> departments) {
		this.departments = departments;
	}

	/**
	 * All Company's Establishments.
	 *
	 * @return list of company's establishments.
	 */
	@OneToMany(mappedBy = "company", cascade = { CascadeType.ALL })
	@XmlTransient
	@JsonIgnore
	public List<Establishment> getEstablishments() {
		return establishments;
	}

	public void setEstablishments(List<Establishment> establishments) {
		this.establishments = establishments;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getCompanyAcronym() {
		return companyAcronym;
	}

	public void setCompanyAcronym(String acronym) {
		this.companyAcronym = acronym;
	}

	public CompanyCategory getCompanyCategory() {
		return companyCategory;
	}

	public void setCompanyCategory(CompanyCategory companyCategory) {
		this.companyCategory = companyCategory;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public Boolean getDiffusableInformations() {
		return this.diffusableInformations;
	}

	public void setDiffusableInformations(Boolean diffusableInformations) {
		this.diffusableInformations = diffusableInformations;
	}

	/**
	 * Method that adds a new {@link CompanyDepartment} to the company.
	 *
	 * @param department : new {@link CompanyDepartment} to add to the company
	 * @return Updated company.
	 */
	public Company addDepartment(CompanyDepartment department) {
		this.departments.add(department);
		return this;
	}

	/**
	 * Method that adds a list of {@link CompanyDepartment} to the departements of
	 * the company.
	 *
	 * @param departments : list of {@link CompanyDepartment} to add to the company
	 * @return Updated company.
	 */
	public Company addDepartments(List<CompanyDepartment> departments) {
		this.departments.addAll(departments);
		return this;
	}

	/**
	 * Method that removes a {@link CompanyDepartment} from the departements list of
	 * the company.
	 *
	 * @param department : a {@link CompanyDepartment} to remove form the
	 *                   departements list of the company
	 * @return Updated company.
	 */
	public Company removeDepartment(CompanyDepartment department) {
		this.departments.remove(department);
		return this;
	}

	/**
	 * Method that clears all the departements of the company.
	 *
	 * @return Updated company.
	 */
	public Company clearDepartments() {
		this.departments.clear();
		return this;
	}

	/**
	 * Method that adds a new {@link Establishment} to the company.
	 *
	 * @param establishment : new {@link Establishment} to add to the company
	 * @return Updated company.
	 */
	public Company addEstablishment(Establishment establishment) {
		this.establishments.add(establishment);
		return this;
	}

	/**
	 * Method that adds a list of {@link Establishment} to the establishments of the
	 * company.
	 *
	 * @param establishments : list of {@link Establishment} to add to the company
	 * @return Updated company.
	 */
	public Company addEstablishments(List<Establishment> establishments) {
		this.establishments.addAll(establishments);
		return this;
	}

	/**
	 * Method that removes a {@link Establishment} from the establishments list of
	 * the company.
	 *
	 * @param establishment : a {@link Establishment} to remove form the
	 *                      establishments list of the company
	 * @return Updated company.
	 */
	public Company removeEstablishment(Establishment establishment) {
		this.establishments.remove(establishments);
		return this;
	}

	/**
	 * Method that clears all the establishments of the company.
	 *
	 * @return Updated company.
	 */
	public Company clearEstablishments() {
		this.establishments.clear();
		return this;
	}

	/**
	 * all the responsibles of the company.
	 *
	 * @return list of responsibles of the company.
	 */

	@XmlElement(name = "responsible", required = false)
	@XmlElementWrapper(name = "responsibles", required = false)
	@OneToMany(mappedBy = "company", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER , orphanRemoval = true)
	@JsonProperty("mandataires_sociaux")
	public List<Person> getResponsibles() {
		return responsibles;
	}

	public void setResponsibles(List<Person> responsibles) {
		this.responsibles = responsibles;
	}

	/**
	 * Method that adds a new responsible to the company .
	 *
	 * @Param responsible to add.
	 * @return Updated company.
	 */
	public Company addResponsible(Person responsible) {
		this.responsibles.add(responsible);
		return this;
	}

	/**
	 * Method that adds a list of responsibles to the company .
	 *
	 * @Param responsibles list.
	 * @return Updated company.
	 */
	public Company addResponsibles(List<Person> responsibles) {
		this.responsibles.addAll(responsibles);
		return this;
	}

	/**
	 * Method that removes a responsibles from the company .
	 *
	 * @Param responsibles to remove.
	 * @return Updated company.
	 */
	public Company removeResponsible(Person responsible) {
		this.responsibles.remove(responsible);
		return this;
	}

	/**
	 * Method that clears all the responsibles of the company.
	 *
	 * @return Updated company.
	 */
	public Company clearResponsibles() {
		this.responsibles.clear();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public boolean isCompany() {
		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public boolean isOrganism() {
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public StructureINSEECat getStrutureLegalCategory() {
		return this.getLegalCategory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStrutureLegalCategory(StructureINSEECat strutureLegalCategory) {
		Assert.isInstanceOf(CompanyINSEECat.class, strutureLegalCategory);
		this.setLegalCategory((CompanyINSEECat) strutureLegalCategory);
	}

	public boolean hasHeadOffice() {
		for (Establishment establishment : this.establishments) {
			if (establishment.getIsHeadOffice()) {
				return true;
			}
		}
		return false;
	}

}
