package org.myec3.socle.core.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.adapter.DateAdapter;
import org.myec3.socle.core.domain.model.enums.OrganismMemberStatus;

/**
 * Created by A664936 on 12/04/2017.
 */
@Audited
@Entity
public class OrganismStatus implements Serializable, PE {
	private Long id;
	private OrganismMemberStatus status;
	private Date date;
	private Organism organism;

	public OrganismStatus() {
	}

	public OrganismStatus(OrganismMemberStatus status, Date date, Organism organism) {
		this.status = status;
		this.date = date;
		this.organism = organism;
	}

	public OrganismStatus(Long id, OrganismMemberStatus status, Date date, Organism organism) {
		this.id = id;
		this.status = status;
		this.date = date;
		this.organism = organism;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

  @Enumerated(EnumType.STRING)
	public OrganismMemberStatus getStatus() {
		return status;
	}

	public void setStatus(OrganismMemberStatus status) {
		this.status = status;
	}

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@XmlTransient
	@JoinColumn(nullable = true)
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

}
