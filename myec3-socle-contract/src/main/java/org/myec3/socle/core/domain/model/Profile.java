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
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.enums.PrefComMedia;
import org.myec3.socle.core.domain.model.enums.ProfileGrade;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * This class represents a profile, i.e. informations that can be hold by a user
 * with a given profile. This means that a user can be associated to several
 * profiles in order to manage multiple identities. <br/>
 * This class is audited by global audit mechanism.<br/>
 * Instances of classes extenting this class are used to represent a unique
 * identity of a real user on a given time over the MyEc3 applications. In order
 * to provide integration with authentication mechanisms, this class implements
 * Spring Security {@link UserDetails} interface
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 *
 */
@SuppressWarnings("unused")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class Profile extends Resource implements UserDetails {

	private static final long serialVersionUID = 8532554792331548255L;

	private String email;
	private String phone;
	private String fax;
	private String cellPhone;
	private ProfileGrade grade;
	private String function;
	private Boolean enabled;
	private PrefComMedia prefComMedia;
	private User user;
	private Address address;
	private List<Role> roles = new ArrayList<>();
	private ProfileType profileType;
	private Collection<GrantedAuthority> grantedAuthorities;
	private String alfUserName;
	private String technicalIdentifier;
	private String dashboard;

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
	public Profile() {
		super();
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Contructor. Initialize the profile name
	 *
	 * @param name : name of the profile
	 */
	public Profile(String name) {
		super(name);
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Constructor. Initialize the profile name, label, email and function
	 *
	 * @param name     : name of the profile
	 * @param label    : label of the profile
	 * @param email    : email of the profile
	 * @param function : function of the profile
	 */
	public Profile(String name, String label, String email, String function) {
		super(name, label);
		this.email = email;
		this.function = function;
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Constructor. Initialize the profile name, label, email, function and user
	 *
	 * @param name     : name of the profile
	 * @param label    : label of the profile
	 * @param email    : email of the profile
	 * @param function : function of the profile
	 * @param user     : real user associated to this profile
	 */
	public Profile(String name, String label, String email, String function, User user) {
		super(name, label);
		this.email = email;
		this.function = function;
		this.user = user;
		this.createdDate = new Date(System.currentTimeMillis());
	}

	/**
	 * Grade of the profile. For possible values @see ProfileGrade
	 *
	 *
	 * @return the profile's grade
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	@XmlElement(required = false)
	public ProfileGrade getGrade() {
		return grade;
	}

	public void setGrade(ProfileGrade grade) {
		this.grade = grade;
	}

	/**
	 * email of the profile. Should be compliant with the format defined by
	 * {@link Email annotation) and cannot be null
	 *
	 * @return the profile email
	 */
	@NotNull
	@Email
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email != null) {
			this.email = email.toLowerCase();
		} else {
			this.email = email;
		}
	}

	/**
	 * dashboard ordonancement. JSON wich correspond to organisation of profile
	 * dashboard
	 *
	 */
	@Column(columnDefinition = "TEXT", nullable = true)
	@XmlTransient
	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}

	/**
	 * Phone number for the profile. No particular format.
	 *
	 * @return the profile phone number
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
	 * Cell phone number for the profile. No particular format.
	 *
	 * @return the profile cell phone number
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	/**
	 * Fax number for the profile. No particular format.
	 *
	 * @return the profile fax number
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
	 * Title of the business function of the user profile in his structure (ex:
	 * manager, assistant, etc.).
	 *
	 * @return the function of the profile in his structure
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
	 * Determines if the profile is enabled. i.e. if the profile holders can access
	 * to applications. Cannot be null.
	 *
	 * @return true if the profile is enabled, false otherwise
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean getEnabled() {
		if (this.enabled == null) {
			this.enabled = Boolean.TRUE;
		}
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Medium that the user profile prefers to receive official communications.
	 *
	 * @see PrefComMedia
	 *
	 * @return the prefered medium for communication
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	@XmlElement(required = false)
	public PrefComMedia getPrefComMedia() {
		return prefComMedia;
	}

	public void setPrefComMedia(PrefComMedia prefComMedia) {
		this.prefComMedia = prefComMedia;
	}

	/**
	 * Physical user associated to this profile. Cannot be null and should be valid.
	 *
	 * @return the user associated to this profile
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	@XmlElement(required = true)
	@NotNull
	@Valid
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Roles that the profile can hold
	 *
	 * @see Role
	 *
	 * @return the list of all roles that the profile can "play"
	 */
	@XmlElement(name = "role", required = false)
	@XmlElementWrapper(name = "roles")
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<Role> getRoles() {
		return roles;
	}

	@Transient
	@JsonIgnore
	public List<Role> getRolesWithoutHidden() {
		List<Role> notHiddenRoles = new ArrayList<Role>();
		for (Role role : roles) {
			if (!role.isHidden()) {
				notHiddenRoles.add(role);
			}
		}
		return notHiddenRoles;
	}

	/**
	 * Replace the list of roles for this profile.<br/>
	 * Instead of "hardly" replacing all roles, this method remove from the existing
	 * list all roles that are not in newRoles and add to the existing list all
	 * roles that are not already in the list.<br/>
	 * This allow to not send "notifications" for adding a set of roles or for
	 * adding a role already present but only for new or removed roles.
	 *
	 * @param newRoles : new roles to add
	 */
	public void setRoles(List<Role> newRoles) {
		// We must test separately this.roles == null and this.roles.isEmpty
		// else an Exception occure (Failed to lazily initialize a collection
		// of role : profile.roles)
		if ((this.roles == null)) {
			this.roles = newRoles;
		} else if (this.roles.isEmpty()) {
			this.roles = newRoles;
		}

		if (!((this.roles == null)) && !(this.roles.isEmpty())) {
			if (newRoles != null) {
				List<Role> listNewRoles = newRoles;

				// get all resources from listNewRoles that where not in
				// listOldRoles = roles
				// added
				for (Role role : listNewRoles) {
					if (!this.roles.contains(role)) {
						this.addRole(role);
					}
				}

				// get all resources from listOldRoles that are not in
				// listNewRoles
				// = resources
				// removed
				List<Role> listRoleToDelete = new ArrayList<Role>();

				for (Role role : this.roles) {
					if (!listNewRoles.contains(role)) {
						listRoleToDelete.add(role);
					}
				}

				for (Role role : listRoleToDelete) {
					this.removeRole(role);
				}
			}
		}
	}

	/**
	 * Add a new role to this profile, even if the role is already in list.
	 *
	 * @param role : the new role to add
	 * @return the modified profile
	 */
	public Profile addRole(Role role) {
		this.roles.add(role);
		return this;
	}

	/**
	 * Add new roles to this organism, even if roles are already in list.
	 *
	 * @param roles : list of roles to add
	 * @return the modified profile
	 */
	public Profile addRoles(List<Role> roles) {
		this.roles.addAll(roles);
		return this;
	}

	/**
	 * Remove a given role from the organism roles list. If the role does not exist,
	 * nothing is done
	 *
	 * @param role : role to remove
	 * @return the modified profile
	 */
	public Profile removeRole(Role role) {
		if ((this.roles != null) && (this.roles.contains(role))) {
			this.roles.remove(role);
		}
		return this;
	}

	/**
	 * Remove all roles from profile roles list
	 *
	 * @return the modified profile
	 */
	public Profile clearRoles(List<Role> role) {
		this.roles.clear();
		return this;
	}

	/**
	 * Type of this profile. i.e. the real and concrete type for this profile
	 *
	 * @see ProfileType
	 *
	 * @return the type of this profile
	 */
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlTransient
	@JsonIgnore
	public ProfileType getProfileType() {
		return profileType;
	}

	public void setProfileType(ProfileType profileType) {
		this.profileType = profileType;
	}

	/**
	 * Address of this profile. Should be a valid address object. Cannot be null.
	 *
	 * @return the address for this profile.
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

	/**
	 * Abstract method that should be implemented by extended classes in order to
	 * provide generic mechanism to determine if a profile is an
	 * {@link EmployeeProfile}
	 *
	 * @return true if this profile is a {@link EmployeeProfile}, false otherwise
	 */
	@Transient
	public abstract boolean isEmployee();

	/**
	 * Abstract method that should be implemented by extended classes in order to
	 * provide generic mechanism to determine if a profile is an
	 * {@link AgentProfile}
	 *
	 * @return true if this profile is a {@link AgentProfile}, false otherwise
	 */
	@Transient
	public abstract boolean isAgent();

	/**
	 * Abstract method that should be implemented by extended classes in order to
	 * provide generic mechanism to determine if a profile is an
	 * {@link AdminProfile}
	 *
	 * @return true if this profile is a {@link AdminProfile}, false otherwise
	 */
	@Transient
	public abstract boolean isAdmin();

	public void setAuthorities(Collection<GrantedAuthority> grantedAuthorities) {
		this.grantedAuthorities = grantedAuthorities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	@XmlTransient
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		if (this.grantedAuthorities == null) {
			this.grantedAuthorities = new ArrayList<GrantedAuthority>();
		}
		return grantedAuthorities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	@XmlTransient
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	@XmlTransient
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	@XmlTransient
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public String getPassword() {
		return this.getUser().getPassword();
	}

	public void setPassword(String password) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public String getUsername() {
		return this.getUser().getUsername();
	}

	public void setUsername(String username) {
	}

	/**
	 * Alfresco username for this profile
	 */
	@Column(unique = true)
	@XmlElement(required = false)
	public String getAlfUserName() {
		return alfUserName;
	}

	public void setAlfUserName(String alfUserName) {
		this.alfUserName = alfUserName;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getTechnicalIdentifier() {
		return technicalIdentifier;
	}

	public void setTechnicalIdentifier(String technicalIdentifier) {
		this.technicalIdentifier = technicalIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Profile [email=" + email + ", grantedAuthorities=" + grantedAuthorities + ", user=" + user + "]";
	}

}
