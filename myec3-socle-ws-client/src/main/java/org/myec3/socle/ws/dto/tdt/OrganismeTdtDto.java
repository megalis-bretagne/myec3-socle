package org.myec3.socle.ws.dto.tdt;

import com.fasterxml.jackson.annotation.JsonSetter;

public class OrganismeTdtDto {

	private String id;

	private String name;

	@JsonSetter("authority_group_id")
	private String authorityGroupId;

	private String address;

	private String city;

	@JsonSetter("postal_code")
	private String postalCode;

	private String telephone;

	private String siren;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthorityGroupId() {
		return authorityGroupId;
	}

	public void setAuthorityGroupId(String authorityGroupId) {
		this.authorityGroupId = authorityGroupId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}
}
