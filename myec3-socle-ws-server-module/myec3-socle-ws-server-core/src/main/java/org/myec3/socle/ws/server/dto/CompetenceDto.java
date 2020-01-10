package org.myec3.socle.ws.server.dto;

public class CompetenceDto {
	private String name;

	public CompetenceDto(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
