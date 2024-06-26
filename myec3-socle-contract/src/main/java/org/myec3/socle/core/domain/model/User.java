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
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.myec3.socle.core.domain.model.adapter.DateAdapter;
import org.myec3.socle.core.domain.model.adapter.TimestampAdapter;
import org.myec3.socle.core.domain.model.enums.Civility;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents a user of MYEC3. This class is audited by global audit
 * mechanism and tagged as "Synchronizable" in order to be synchronized.<br/>
 * This class extends the {@link Resource} class.
 * 
 * This class is synchronized.<br />
 * 
 * This class is audited by global audit mechanism<br />
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class User extends Resource {

	private static final long serialVersionUID = 226040247356973607L;
	private String username;
	private Date creationDate;
	private String temporaryPassword;
	private String certificate;
	private String lastname;
	private String firstname;
	private Boolean enabled = true;
	private Civility civility;
	private SviProfile sviProfile;
	private List<Profile> profiles = new ArrayList<>();
	private List<FunctionalAccount> functionalAccounts = new ArrayList<>();
	private ConnectionInfos connectionInfos;
	private int connectionAttempts;
	private Date birthDate;
	private String birthPlace;
	private String birthCountry;

	/**
	 * Default constructor. Do nothing.
	 */
	public User() {
		super();
		creationDate=new Date(System.currentTimeMillis());
	}

	/**
	 * Constructor. Initialize the user firstname and lastname.
	 * 
	 * @param firstname : first name of the user
	 * @param lastname  : last name of the user
	 */
	public User(String firstname, String lastname) {
		super(firstname + " " + lastname);
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * Civity of the user. For possible values @see Civility.
	 * 
	 * @return the user's civility
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	@XmlElement(required = false)
	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	/**
	 * The first name of the user. it can not be null.
	 * 
	 * @return the user's fisrt name.
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * The last name of the user. it can not be null.
	 * 
	 * @return the user's last name.
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * The username/login of the user. it can not be null and it must be unique.
	 * 
	 * @return the user's userName/Login.
	 */
	@NotNull
	@Column(nullable = false, unique = true)
	@XmlElement(required = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * creation date of user.
	 *
	 * @return modification date of creation.
	 */
	@Column(nullable = true)
	@XmlTransient
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * The temporary password of the user.
	 *
	 * @return user's temporary password.
	 */
	@Transient
	@XmlTransient
	@JsonIgnore
	public String getTemporaryPassword() {
		return temporaryPassword;
	}

	/**
	 * Set a temporary password for the user.
	 */
	public void setTemporaryPassword(String temporaryPassword) {
		this.temporaryPassword = temporaryPassword;
	}

	/**
	 * The certificate of the user. It is the public key of the certificate
	 * 
	 * @return user's certificate public key.
	 */
	@Column(columnDefinition = "TEXT")
	@XmlElement(required = false)
	@JsonIgnore
	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

    /**
     * The svi Profil of the user. A user can have only on SVI Profil.
     * 
     * @return user's SVI profile.
     */
    @OneToOne
    @XmlElement(required = false) 
    @JsonIgnore
    public SviProfile getSviProfile() {
		return sviProfile;
    }

	public void setSviProfile(SviProfile sviProfile) {
		this.sviProfile = sviProfile;
	}

    /**
     * Profils attached to a user.
     * 
     * @return user profils.
     */
    @OneToMany(mappedBy = "user")
    @XmlTransient
    @JsonIgnore
    public List<Profile> getProfiles() {
		return profiles;
    }

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

    /**
     * Functional accounts of a user. a user can have one or many functional
     * accounts.
     * 
     * @return list of functional accounts of a user.
     */
    @OneToMany(mappedBy = "user")
    @XmlTransient 
    @JsonIgnore
    public List<FunctionalAccount> getFunctionalAccounts() {
	return functionalAccounts;
    }

	public void setFunctionalAccounts(List<FunctionalAccount> functionalAccounts) {
		this.functionalAccounts = functionalAccounts;
	}

	/**
	 * Methode that adds a new profile to a user.
	 * 
	 * @param profile to add.
	 * @return Updated user.
	 */
	public User addProfile(Profile profile) {
		this.profiles.add(profile);
		return this;
	}

	/**
	 * Methode that adds a list profiles to the user.
	 * 
	 * @param list of profiles to add
	 * @return Updated user.
	 */
	public User addProfiles(List<Profile> profiles) {
		this.profiles.addAll(profiles);
		return this;
	}

	/**
	 * Methode that removes a profile from the user profiles.
	 * 
	 * @param profile to remove
	 * @return Updated user.
	 */
	public User removeProfile(Profile profile) {
		this.profiles.remove(profile);
		return this;
	}

	/**
	 * Methode that deletes all the profiles of the user.
	 * 
	 * @return Updated user.
	 */
	public User clearProfiles() {
		this.profiles.clear();
		return this;
	}

	/**
	 * Methode initialise the current user with the user in param.
	 * 
	 * @param user for initialisation.
	 * 
	 * @return Updated user.
	 */
	public void reattach(User user) {
		this.setCivility(user.getCivility());
		this.setEnabled(user.isEnabled());
		this.setExternalId(user.getExternalId());
		this.setFirstname(user.getFirstname());
		this.setLastname(user.getLastname());
		this.setName(user.getName());

		// if the certificate have been updated
		if (user.getCertificate() != null) {
			this.setCertificate(user.getCertificate());
		}

		this.setUsername(user.getUsername());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "User [username=" + username + ", lastname=" + lastname
				+ ", firstname=" + firstname + ", civility=" + civility
				+ ", sviProfile=" + sviProfile + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (sviProfile == null) {
			if (other.sviProfile != null)
				return false;
		} else if (!sviProfile.equals(other.sviProfile))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result
				+ ((sviProfile == null) ? 0 : sviProfile.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@OneToOne(cascade = CascadeType.ALL)
	@XmlTransient
	@JsonIgnore
	public ConnectionInfos getConnectionInfos() {
		return connectionInfos;
	}

	public void setConnectionInfos(ConnectionInfos connectionInfos) {
		this.connectionInfos = connectionInfos;
	}

	@XmlTransient
	@JsonIgnore
	public int getConnectionAttempts() {
		return connectionAttempts;
	}

	public void setConnectionAttempts(int connectionAttempts) {
		this.connectionAttempts = connectionAttempts;
	}

	@Column(nullable = true)
	@Type(type = "date")
	@XmlElement(required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getBirthCountry() {
		return birthCountry;
	}

	public void setBirthCountry(String birthCountry) {
		this.birthCountry = birthCountry;
	}
}
