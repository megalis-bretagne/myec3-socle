package org.myec3.socle.core.domain.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class PaysImplantation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1752920015341251277L;

	private String code;

	private String value;

	@Transient
	@XmlElement(required = false)
	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	@XmlElement(required = false)
	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}