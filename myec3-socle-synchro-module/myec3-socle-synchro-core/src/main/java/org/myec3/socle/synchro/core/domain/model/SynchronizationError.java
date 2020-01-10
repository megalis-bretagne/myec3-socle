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
package org.myec3.socle.synchro.core.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.MethodType;

/**
 * This class represents an error which has occured during the synchronization
 * process. <br />
 * This class is used to store in database a queue of errors which have been
 * occured and their number of attempts before that the error is fixed. <br />
 * When the error is fixed the row is deleted from the database. <br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationError implements Serializable, PE {

	private static final long serialVersionUID = 8218564044197550380L;

	private Long id;
	private Long resourceId;
	private MethodType methodType;
	private int nbAttempts;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationError() {
	}

	/**
	 * Contructor. Initialize the resource ID and the methodType used during the
	 * synchronization process.
	 * 
	 * @param resourceId : the ID of the resource concerned by the synchronization
	 * 
	 * @param methodType : the {@link MethodType} used during the synchronization
	 *                   process
	 */
	public SynchronizationError(Long resourceId, MethodType methodType) {
		this.resourceId = resourceId;
		this.methodType = methodType;
	}

	/**
	 * Contructor. Initialize the resource ID, the methodType and the number of
	 * attempts of the synchronization process.
	 * 
	 * @param resourceId : the ID of the resource concerned by the synchronization
	 * 
	 * @param methodType : the {@link MethodType} used during the synchronization
	 *                   process
	 * 
	 * @param nbAttempts : the number of attempts of the synchronization. The
	 *                   maximum number of attempts authorized is definied in a
	 *                   properties file (@See MyEc3SynchroConstants).
	 */
	public SynchronizationError(Long resourceId, MethodType methodType, int nbAttempts) {
		this.resourceId = resourceId;
		this.methodType = methodType;
		this.nbAttempts = nbAttempts;
	}

	/**
	 * Technical id of the SynchronizationError
	 * 
	 * @return the id for this SynchronizationError
	 */
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id for the {@link Resource} concerned by the synchronization
	 *         error.
	 */
	@Column(nullable = false)
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the {@link MethodType} used during synchronization process.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	/**
	 * @return the number of attempts for the concerned synchronization
	 *         process.<br />
	 *         There is a limit defined in {@link MyEc3SynchroConstants}
	 */
	@Column(nullable = false)
	public int getNbAttempts() {
		return nbAttempts;
	}

	public void setNbAttempts(int nbAttempts) {
		this.nbAttempts = nbAttempts;
	}

}
