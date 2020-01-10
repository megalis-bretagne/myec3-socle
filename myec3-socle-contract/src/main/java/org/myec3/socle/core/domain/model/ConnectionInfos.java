package org.myec3.socle.core.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class ConnectionInfos implements Serializable, PE {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date lastConnectionDate;
	private int nbConnections;
	private long meanTimeBetweenTwoConnections;

	public ConnectionInfos() {
		super();
	}

	public ConnectionInfos(Long date) {
		super();
		this.setLastConnectionDate(new Date(date));
		this.setNbConnections(1);
	}

	@NotNull
	public Date getLastConnectionDate() {
		return lastConnectionDate;
	}

	public void setLastConnectionDate(Date lastConnectionDate) {
		this.lastConnectionDate = lastConnectionDate;
	}

	@NotNull
	public int getNbConnections() {
		return nbConnections;
	}

	public void setNbConnections(int nbConnections) {
		this.nbConnections = nbConnections;
	}

	@Column(nullable = true)
	public long getMeanTimeBetweenTwoConnections() {
		return meanTimeBetweenTwoConnections;
	}

	public void setMeanTimeBetweenTwoConnections(long meanTimeBetweenTwoConnections) {
		this.meanTimeBetweenTwoConnections = meanTimeBetweenTwoConnections;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
