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
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

/**
 * Describe the model of a project account.
 * 
 * A project account represents a person who is starting a business but who is
 * not yet a company.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@XmlRootElement
@Audited
public class ProjectAccount extends FunctionalAccount {

	private static final long serialVersionUID = 8087054965651402131L;

	private String login;
	private String password;
	private String email;

	/**
	 * Default constructor. Do nothing.
	 */
	public ProjectAccount() {
		super();
	}

	/**
	 * @return the login of the project account. This column cannot be null and must
	 *         be unique.
	 */
	@NotNull
	@Column(nullable = false, unique = true)
	@XmlElement(required = true)
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password of the project account. This column cannot be null.
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email of the project account. This column cannot be null.
	 */
	@NotNull
	@Email
	@Column(nullable = false, unique = false)
	@XmlElement(required = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return true if the {@link FunctionalAccount} is an ProjectAccount.
	 */
	@Override
	@Transient
	public boolean isProjectAccount() {
		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ProjectAccount [login=" + login + ", email=" + email + "]";
	}

}
