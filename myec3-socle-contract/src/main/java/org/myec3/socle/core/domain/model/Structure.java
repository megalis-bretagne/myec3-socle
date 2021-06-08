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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.adapter.ChildStructuresAdapter;
import org.myec3.socle.core.domain.model.adapter.ParentStructuresAdapter;
import org.myec3.socle.core.domain.model.constants.MyEc3AlfrescoConstants;
import org.myec3.socle.core.domain.model.enums.StructureINSEECat;
import org.myec3.socle.core.domain.model.meta.StructureType;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents a structure, i.e. an organization, public or
 * private.<br/>
 * This class is abstract and designed to be extended by a concrete class
 * representing the real structure for a given type of structure<br/>
 * 
 * This class is audited by global audit mechanism.<br />
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class Structure extends Resource {

	private static final long serialVersionUID = 4886581410509064914L;

	@Getter
	@Setter
	@Column(nullable = true, columnDefinition = "LONGTEXT")
	@XmlElement(required = false)
	private String description;
	private Boolean enabled;
	private String email;
	private String phone;
	private String fax;
	private String website;
	private String logoUrl;
	private String iconUrl;
	private Address address;
	private String acronym;
	private String siren;
	private StructureType structureType;
	private Integer workforce;
	private String tenantIdentifier;

	@Getter
	@Setter
	@Column(nullable = false)
	private Date createdDate;

	/**
	 * Id du user
	 */
	@Getter
	@Setter
	@Column()
	private Long createdUserId;

	private List<Application> applications = new ArrayList<>();
	private List<Structure> parentStructures = new ArrayList<>();
	private List<Structure> childStructures = new ArrayList<>();

	/**
	 * Default constructor. Initialize an empty address
	 */
	public Structure() {
		super();
		this.enabled = Boolean.TRUE;
		this.address = new Address();
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * email of the structure. Should be compliant with the format defined by
	 * {@link Email annotation) and cannot be null
	 * 
	 * @return the Structure email
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
	 * Fax number for the Structure. No particular format.
	 * 
	 * @return the Structure fax number
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
	 * Phone number for the Structure. No particular format.
	 * 
	 * @return the Structure phone number
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
	 * Acronym for the Structure. Length should be exactly 3 and acronym cannot be
	 * null.<br/>
	 * Acronym is associated to a structure during its creation and cannot be
	 * modified.
	 * 
	 * @return the Structure acronym
	 */

	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * Applications that the Structure can access to.
	 * 
	 * @return the list of authorized applications
	 */
	@ManyToMany
	@XmlElementWrapper(name = "applications", required = false)
	@XmlElement(name = "application", required = false)
	public List<Application> getApplications() {
		return applications;
	}

	/**
	 * We must add/remove manually applications in order to trigger
	 * preUpdateEventListener
	 * 
	 * @param newApplications : new applications to set at the structure
	 */
	public void setApplications(List<Application> newApplications) {

		if (this.applications == null || this.applications.isEmpty()) {
			this.applications = newApplications;
		}

		if (!(this.applications == null) && !(this.applications.isEmpty())) {
			List<Application> listNewApplications = newApplications;

			// get all resources from listNewApplications that where not in
			// listOldApplications = applications
			// added
			for (Application application : listNewApplications) {
				if (!this.applications.contains(application)) {
					this.addApplication(application);
				}
			}

			// get all resources from listOldApplications that are not in
			// listNewApplications
			// = resources
			// removed
			List<Application> listApplicationToDelete = new ArrayList<>();

			for (Application application : this.applications) {
				if (!listNewApplications.contains(application)) {
					listApplicationToDelete.add(application);
				}
			}

			for (Application application : listApplicationToDelete) {
				this.removeApplication(application);
			}
		}

	}

	/**
	 * Siren number for the Structure. No particular format.
	 * 
	 * @return the Structure siren number
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
	 * Add a new application to this structure, even if the application is already
	 * in list.
	 * 
	 * @param application : the new application to add
	 * @return the modified structure
	 */
	public Structure addApplication(Application application) {
		this.applications.add(application);
		return this;
	}

	/**
	 * Add new applications to this structure, even if applications are already in
	 * list.
	 * 
	 * @param applications : list of applications to add
	 * @return the modified structure
	 */
	public Structure addApplications(List<Application> applications) {
		this.applications.addAll(applications);
		return this;
	}

	/**
	 * Remove a given application from the structure departments list. If the
	 * application does not exist, nothing is done
	 * 
	 * @param application : application to remove
	 * @return the modified structure
	 */
	public Structure removeApplication(Application application) {
		if ((this.applications != null) && (this.applications.contains(application))) {
			this.applications.remove(application);
		}
		return this;
	}

	/**
	 * Remove all applications from structure applications list
	 * 
	 * @return the modified structure
	 */
	public Structure clearApplications() {
		this.applications.clear();
		return this;
	}

	/**
	 * Determines if the structure is enabled. i.e. if the structure members can
	 * access to applications. Cannot be null.
	 * 
	 * @return true if the structure is enabled, false otherwise
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Website address of the structure. No particular format
	 * 
	 * @return the structure website
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
	 * IconUrl of the structure. i.e. the url where the icon associated to the
	 * structure can be found. No particular format
	 * 
	 * @return the url of the icon for this structure
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	/**
	 * LogoUrl of the structure. i.e. the url where the logo associated to the
	 * structure can be found. No particular format
	 * 
	 * @return the url of the logo for this structure
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * Address of this structure. Should be a valid address object. Cannot be null.
	 * 
	 * @return the address for this structure.
	 */
	@NotNull
	@Valid
	@Embedded
	@Column(nullable = false)
	@XmlElement(required = true)
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * Type of this structure. i.e. the real and concrete type for this structure
	 * 
	 * @see StructureType
	 * 
	 * @return the type of this structure
	 */
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlTransient
	@JsonIgnore
	public StructureType getStructureType() {
		return structureType;
	}

	public void setStructureType(StructureType structureType) {
		this.structureType = structureType;
	}

	public void setWorkforce(Integer workforce) {
		this.workforce = workforce;
	}

	/**
	 * Workforce for this structure. i.e. number of people working for this
	 * structure
	 * 
	 * @return the workforce for this structure.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Integer getWorkforce() {
		return workforce;
	}

	/**
	 * Abstract method that should be implemented by extended classes in order to
	 * provide generic mechanism to determine if a structure is a {@link Company}
	 * 
	 * @return true if this structure is a {@link Company}, false otherwise
	 */
	@Transient
	public abstract boolean isCompany();

	@XmlTransient
	@Transient
	@JsonIgnore
	public abstract StructureINSEECat getStrutureLegalCategory();

	/**
	 * Abstract method that should be implemented by extended classes in order to
	 * provide generic mechanism to determine if a structure is a {@link Organism}
	 * 
	 * @return true if this structure is an {@link Organism}, false otherwise
	 */
	@Transient
	public abstract boolean isOrganism();

	public void setParentStructures(List<Structure> newParentStructures) {
		this.parentStructures = newParentStructures;
	}

	/**
	 * List of parent structures which are in relation with the current structure.
	 * i.e. a structure can be an Organism {@link Organism} or a Company
	 * {@link Company}
	 * 
	 * @return the list of parent structures in relation with this structure.
	 */
	@ManyToMany(mappedBy = "childStructures")
	@XmlElement(name = "parentStructures", required = false)
	@XmlJavaTypeAdapter(ParentStructuresAdapter.class)
	@Column(nullable = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<Structure> getParentStructures() {
		return parentStructures;
	}

	public void setChildStructures(List<Structure> newChildStructures) {
		this.childStructures = newChildStructures;
	}

	/**
	 * List of child structures which are in relation with the current structure.
	 * i.e. a structure can be an Organism {@link Organism} or a Company
	 * {@link Company}
	 * 
	 * @return the list of child structures in relation with this structure.
	 */
	@ManyToMany
	@XmlElement(name = "childStructures", required = false)
	@XmlJavaTypeAdapter(ChildStructuresAdapter.class)
	@Column(nullable = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<Structure> getChildStructures() {
		return childStructures;

	}

	public abstract void setStrutureLegalCategory(StructureINSEECat strutureLegalCategory);

	// HELPERS
	public Structure addChildStructure(Structure childStructure) {
		this.childStructures.add(childStructure);
		return this;
	}

	public Structure addChildStructures(List<Structure> childStructures) {
		this.childStructures.addAll(childStructures);
		return this;
	}

	public Structure removeChildStructure(Structure childStructure) {
		this.childStructures.remove(childStructure);
		return this;
	}

	public Structure clearChildStructures() {
		this.childStructures.clear();
		return this;
	}

	/**
	 * Username used in alfresco (multiTenants) apps (iparapheur)
	 * 
	 * @return
	 */
	@Column(unique = true)
	@XmlElement(required = false)
	public String getTenantIdentifier() {
		return tenantIdentifier;
	}

	public void setTenantIdentifier(String tenantIdentifier) {
		this.tenantIdentifier = tenantIdentifier;
	}

	public void setTenantIdentifier() {
		if (this.getId() != null) {
			// Add a leading 0 if the id is less than 10, because parapheur requires it
			String idTenant = String.format("%1$2s", String.valueOf(this.getId())).replace(" ", "0");
			this.setTenantIdentifier(MyEc3AlfrescoConstants.getBeginTenantsName() + idTenant);
		}
	}

}
