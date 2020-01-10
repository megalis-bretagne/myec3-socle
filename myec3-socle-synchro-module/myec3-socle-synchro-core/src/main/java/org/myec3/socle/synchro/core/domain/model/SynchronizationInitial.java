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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;

/**
 * This class represents the first synchronization sent during the
 * synchronization process for a given {@link Resource}.<br />
 * 
 * We use this class in order to :
 * <ul>
 * <li>Increment the number of attempts of a synchronization while the
 * synchronization process is not successful.</li>
 * <li>Know what response has been sent at the distant application during the
 * error handling process</li>
 * </ul>
 * 
 * @see ResourcesSynchronizationJob for the error handling process
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationInitial implements Serializable, PE {

	private static final long serialVersionUID = -1423467720645093979L;

	private Long id;
	private Long initialSynchronizationId;
	private SynchronizationLog synchronizationLog;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationInitial() {
	}

	/**
	 * Contructor. Initialize the initial synchronization id and the
	 * synchronizationLog.
	 * 
	 * @param initialSynchronizationId : Represents the firts synchronization
	 *                                 initial id (first attempts).
	 * 
	 * @param synchronizationLog       : Define the {@link SynchronizationLog}
	 *                                 corresponding at this synchronizationInitial.
	 */
	public SynchronizationInitial(Long initialSynchronizationId, SynchronizationLog synchronizationLog) {
		this.initialSynchronizationId = initialSynchronizationId;
		this.synchronizationLog = synchronizationLog;
	}

	/**
	 * Technical id of the SynchronizationInitial
	 * 
	 * @return the id for this SynchronizationInitial
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
	 * @return the ID of the first synchronization attemps of a {@link Resource}
	 */
	@Column(nullable = false)
	public Long getInitialSynchronizationId() {
		return initialSynchronizationId;
	}

	public void setInitialSynchronizationId(Long initialSynchronizationId) {
		this.initialSynchronizationId = initialSynchronizationId;
	}

	/**
	 * @return the {@link SynchronizationLog} corresponding at this
	 *         synchronizationInitial.
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public SynchronizationLog getSynchronizationLog() {
		return synchronizationLog;
	}

	public void setSynchronizationLog(SynchronizationLog synchronizationLog) {
		this.synchronizationLog = synchronizationLog;
	}
}
