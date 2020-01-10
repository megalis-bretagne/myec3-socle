package org.myec3.socle.ws.server.dto;

public class OrganismDepartmentIdAndApiKeyDto {

	private Long organismDepartmentId;
	private String apiKey;
	private String label;

	public OrganismDepartmentIdAndApiKeyDto(Long organismDepartmentId, String apiKey, String label) {
		this.organismDepartmentId = organismDepartmentId;
		this.apiKey = apiKey;
		this.label = label;
	}

	public Long getOrganismDepartmentId() {
		return organismDepartmentId;
	}

	public void setOrganismDepartmentId(Long organismDepartmentId) {
		this.organismDepartmentId = organismDepartmentId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
