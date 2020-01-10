package org.myec3.socle.ws.server.dto;


public class ProfileDto {

	private String name;
	private String email;
	private String username;
	private String firstname;
	private String lastname;
	private Boolean isAgent;

	public ProfileDto(String name, String email, String username, String firstname, String lastname, Boolean isAgent) {
		this.name = name;
		this.email = email;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.isAgent = isAgent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public void setLastname(String username) {
		this.lastname = lastname;
	}

	public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}
}
