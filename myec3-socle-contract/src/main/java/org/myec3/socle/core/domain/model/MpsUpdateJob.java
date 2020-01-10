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
import javax.persistence.Id;

/**
 * This class represents an update of {@link Company} or
 * {@link Establishment}.<br>
 * 
 * @author Matthieu Gaspard <matthieu.gaspard@worldline.com>
 * 
 */

@Entity
public class MpsUpdateJob implements PE {

	private Long id;
	private String type;
	private String priority;
	private String machineName;

	public MpsUpdateJob() {

	}

	public MpsUpdateJob(Long id, String type, String priority, String machineName) {
		this.id = id;
		this.type = type;
		this.priority = priority;
		this.machineName = machineName;
	}

	@Id
	@Column(nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(nullable = false)
	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Column(nullable = false)
	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	@Override
	public String toString() {
		return "MpsUpdateJob [id=" + id + ", type=" + type + ", priority=" + priority + ", machineName=" + machineName
				+ "]";
	}

}
