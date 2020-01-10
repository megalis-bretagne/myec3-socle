/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * This class represents a competence for agents
 *
 * @author charles.bourree@wordline.com
 */

@Entity
@XmlRootElement
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Synchronized
public class Competence implements Serializable, PE, Comparable {

	private static final long serialVersionUID = 2822237455030315914L;

	private String name;

	private Set<AgentProfile> agentProfiles = new HashSet<>();

	private Long id;

	@XmlElement(required = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(mappedBy = "competences")
	@XmlElement(required = false)
	@JsonIgnore
	public Set<AgentProfile> getAgentProfiles() {
		return agentProfiles;
	}

	public void setAgentProfiles(Set<AgentProfile> agentProfiles) {
		this.agentProfiles = agentProfiles;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlElement(required = true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Object o) {
		return ((Competence) o).getName().compareTo(this.getName());
	}
}
