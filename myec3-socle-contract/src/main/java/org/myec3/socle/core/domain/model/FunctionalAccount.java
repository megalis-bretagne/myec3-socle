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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

/**
 * This class describe the model of a functionnal account. A functional account
 * has not joined any {@link Structure}. It should only have access at the
 * platform.<br />
 * 
 * Example : a {@link ProjectAccount}
 * 
 * This class is audited by global audit mechanism<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class FunctionalAccount extends Resource {

	private static final long serialVersionUID = 2860609134118998036L;

	private Boolean enabled;
	private User user;

	/**
	 * @return true if the account is enabled
	 */
	@NotNull
	@Column(nullable = false)
	public Boolean getEnabled() {
		if (this.enabled == null) {
			this.enabled = Boolean.TRUE;
		}
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the user linked at the functionnal account
	 */
	@XmlTransient
	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return true if the functional account is an {@link ProjectAccount}
	 */
	@Transient
	public abstract boolean isProjectAccount();
}
