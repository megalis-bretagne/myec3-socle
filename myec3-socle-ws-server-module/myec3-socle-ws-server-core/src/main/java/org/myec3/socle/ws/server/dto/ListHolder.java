package org.myec3.socle.ws.server.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;

@XmlRootElement
@XmlSeeAlso({ Organism.class, AgentManagedApplication.class, Application.class, EmployeeProfile.class,
		AgentProfile.class, Establishment.class })
public class ListHolder<T> {

	private List<T> containedList = new ArrayList<>();

	/**
	 * Default Constructor for Jaxb
	 */
	public ListHolder() {
		// Empty OK
	}

	public ListHolder(List<T> containedList) {
		this.containedList = containedList;
	}

	@XmlElement
	public List<T> getContainedList() {
		return containedList;
	}

	public void setContainedList(List<T> containedList) {
		this.containedList = containedList;
	}
}
