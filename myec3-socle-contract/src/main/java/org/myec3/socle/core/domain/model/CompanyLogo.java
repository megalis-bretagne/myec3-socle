package org.myec3.socle.core.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Embeddable
public class CompanyLogo implements Serializable {


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