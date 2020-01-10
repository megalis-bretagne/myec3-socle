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
package org.myec3.socle.core.domain.model.meta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;

/**
 * Description of the StructureTypeApplication object. It extends from
 * {@link Resource} object.
 * 
 * This class defines the relation between a {@link StructureType} and an
 * {@link Application}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
public class StructureTypeApplication implements Serializable, PE {

	private static final long serialVersionUID = -8574764785225088706L;

	private Long id;
	private StructureType structureType;
	private Application application;
	private Boolean subscribable;
	private Boolean defaultSubscription;
	private Boolean manageableRoles;
	private Boolean multipleRoles;

	/**
	 * Default Constructor : call the Resource default constructor : do nothing
	 */
	public StructureTypeApplication() {
	}

	/**
	 * Constructor. Initialize the structureTypeApplication. Define if the current
	 * structureTypeApplication is subscribable and if it's a default subscription
	 * 
	 * @param subscribable        : define if this application is subscribable for
	 *                            this structureType
	 * @param defaultSubscription : define if this application is a default
	 *                            subscription for this structureType
	 */
	public StructureTypeApplication(Boolean subscribable, Boolean defaultSubscription) {
		this.subscribable = subscribable;
		this.defaultSubscription = defaultSubscription;
	}

	/**
	 * Technical id of the StructureTypeApplication
	 * 
	 * @return the unique identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the {@link StructureType} corresponding
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public StructureType getStructureType() {
		return this.structureType;
	}

	public void setStructureType(StructureType structureType) {
		this.structureType = structureType;
	}

	/**
	 * @return the {@link Application}
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public Application getApplication() {
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * @return true if this {@link Application} is subscribable depending on the
	 *         {@link StructureType}
	 */
	@Column(nullable = false)
	public Boolean getSubscribable() {
		if (this.subscribable == null) {
			this.subscribable = Boolean.FALSE;
		}
		return subscribable;
	}

	public void setSubscribable(Boolean subscribable) {
		this.subscribable = subscribable;
	}

	/**
	 * @return true if the {@link Application} is a default subscription for the
	 *         {@link StructureType} defined.
	 */
	@Column(nullable = false)
	public Boolean getDefaultSubscription() {
		if (this.defaultSubscription == null) {
			this.defaultSubscription = Boolean.FALSE;
		}
		return defaultSubscription;
	}

	public void setDefaultSubscription(Boolean defaultSubscription) {
		this.defaultSubscription = defaultSubscription;
	}

	/**
	 * @return true if end users can manage {@link Role} for this
	 *         {@link Application} and this {@link StructureType}. Default value is
	 *         Boolean.FALSE.
	 */
	@Column(nullable = false)
	public Boolean getManageableRoles() {
		if (this.manageableRoles == null) {
			this.manageableRoles = Boolean.FALSE;
		}
		return manageableRoles;
	}

	public void setManageableRoles(Boolean manageableRoles) {
		this.manageableRoles = manageableRoles;
	}

	/**
	 * @return true if end users can have more than one {@link Role} depending on
	 *         the {@link Application} and the {@link StructureType} defined.
	 */
	@Column(nullable = false)
	public Boolean getMultipleRoles() {
		if (this.multipleRoles == null) {
			this.multipleRoles = Boolean.FALSE;
		}
		return multipleRoles;
	}

	public void setMultipleRoles(Boolean multipleRoles) {
		this.multipleRoles = multipleRoles;
	}

}
