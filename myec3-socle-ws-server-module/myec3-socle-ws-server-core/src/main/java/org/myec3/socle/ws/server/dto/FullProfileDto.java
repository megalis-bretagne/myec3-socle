package org.myec3.socle.ws.server.dto;


import org.myec3.socle.core.domain.model.Profile;

/**
 * Contain main information of Profile + User
 * User for MadMax
 */
public class FullProfileDto {
	
	private String firstname;
	private String lastname;
	private String function;
	private String address;
	private String postalCode;
	private String city;
	private String country;
	private String email;
	private String cellPhone;
	private String phone;
	private String fax;
	private String username;
	private String grade;
	private String userId;
	
	public FullProfileDto(Profile profile) {
		if (profile.getUser() != null) { 
			this.firstname = profile.getUser().getFirstname();
			this.lastname = profile.getUser().getLastname();
			this.userId = profile.getUser().getId().toString();
		}
		
		if (profile.getAddress() != null) {
			this.address = profile.getAddress().getPostalAddress();
			this.postalCode = profile.getAddress().getPostalCode();
			this.city = profile.getAddress().getCity();
			if (profile.getAddress().getCountry() != null) {
				this.country = profile.getAddress().getCountry().name();
			}
		}

		this.function = profile.getFunction();

		this.email = profile.getEmail();
		this.cellPhone = profile.getCellPhone();
		this.phone = profile.getPhone();
		this.fax = profile.getFax();
		this.username = profile.getUsername();
			
		
		if (profile.getGrade() != null) {
			this.grade = profile.getGrade().getLabel();
		}
	}

	public FullProfileDto() {
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
