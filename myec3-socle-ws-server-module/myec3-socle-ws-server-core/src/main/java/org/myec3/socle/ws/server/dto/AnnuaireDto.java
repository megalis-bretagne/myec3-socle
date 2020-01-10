package org.myec3.socle.ws.server.dto;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Competence;

import java.util.ArrayList;
import java.util.List;

public class AnnuaireDto {

	private Long nbResults;
	private List<AgentProfileDto> agentProfileDtoList;

	public AnnuaireDto(List<AgentProfile> agentProfileList, Long nbResults) {
		if (agentProfileList != null) {
			this.agentProfileDtoList = new ArrayList<>();
			for (AgentProfile ap : agentProfileList) {
				addAgentProfile(ap);
			}
		}
		this.nbResults = nbResults;
	}

	private void addAgentProfile(AgentProfile ap) {
		List<CompetenceDto> competenceDtoList;
		String postalCode = "";
		String postalAddress = "";
		String grade = "";
		String lastname = "";
		String firstname = "";
		competenceDtoList = new ArrayList<>();

		for (Competence cp : ap.getCompetences()) {
			competenceDtoList.add(new CompetenceDto(cp.getName()));
		}
		if (ap.getAddress() != null) {
			postalAddress = ap.getAddress().getPostalAddress();
			postalCode = ap.getAddress().getPostalCode();
		}
		if (ap.getGrade() != null) {
			grade = ap.getGrade().getLabel();
		}
		if (ap.getUser() != null) {
			lastname = ap.getUser().getLastname();
			firstname = ap.getUser().getFirstname();
		}
		this.agentProfileDtoList.add(new AgentProfileDto(lastname, firstname, ap.getEmail(), postalCode, ap.getCellPhone(), ap.getPhone(),
				grade, postalAddress, ap.getFax(), competenceDtoList));
	}

	public Long getNbResults() {
		return nbResults;
	}

	public void setNbResults(Long nbResults) {
		this.nbResults = nbResults;
	}

	public List<AgentProfileDto> getAgentProfileDtoList() {
		return agentProfileDtoList;
	}

	public void setAgentProfileDtoList(List<AgentProfileDto> agentProfileDtoList) {
		this.agentProfileDtoList = agentProfileDtoList;
	}
}
