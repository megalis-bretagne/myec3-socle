package org.myec3.socle.core.domain.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

/**
 * Created by a602499 on 16/12/2016.
 */
@Entity
@XmlRootElement
@Audited
public class AgentManagedApplication implements Serializable, PE {

	private static final long serialVersionUID = 2822237455030315913L;

	private Long id;

	private AgentProfile agentProfile;
	private Organism organism;
	private Application managedApplication;

	public AgentManagedApplication() {
	}

	public AgentManagedApplication(AgentProfile agentProfile, Organism organism, Application managedApplication) {
		this.agentProfile = agentProfile;
		this.organism = organism;
		this.managedApplication = managedApplication;
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

	@ManyToOne
	@XmlElement(required = false)
	public AgentProfile getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(AgentProfile agentProfile) {
		this.agentProfile = agentProfile;
	}

	@ManyToOne
	@XmlElement(required = false)
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	@ManyToOne
	@XmlElement(required = false)
	public Application getManagedApplication() {
		return managedApplication;
	}

	public void setManagedApplication(Application managedApplication) {
		this.managedApplication = managedApplication;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		AgentManagedApplication other = (AgentManagedApplication) o;

		if (this.agentProfile.equals(other.agentProfile) && this.managedApplication.equals(other.managedApplication)
				&& this.organism.equals(other.organism)) {
			return true;
		}

		return false;
	}
}
