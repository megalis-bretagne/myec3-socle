package org.myec3.socle.ws.server.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class AgentProfileDto {

	private String firstname;
	private String lastname;
	private String email;
	private String postalCode;
	private String cellPhone;
	private String phone;
	private String grade;
	private String postalAddress;
	private String fax;
	private List<CompetenceDto> competenceDtoList;

	public AgentProfileDto(String lastname, String firstname, String email, String postalCode, String cellPhone, String phone, String grade, String postalAddress, String fax, List<CompetenceDto> competenceDtoList) {
		this.lastname = lastname;
		this.firstname = firstname;
		this.email = email;
		this.postalCode = postalCode;
		this.cellPhone = cellPhone;
		this.phone = phone;
		this.grade = grade;
		this.postalAddress = postalAddress;
		this.fax = fax;
		this.competenceDtoList = competenceDtoList;
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

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public List<CompetenceDto> getCompetenceDtoList() {
		return competenceDtoList;
	}

	public void setCompetenceDtoList(List<CompetenceDto> competenceDtoList) {
		this.competenceDtoList = competenceDtoList;
	}
}
