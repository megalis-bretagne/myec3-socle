package org.myec3.socle.ws.client.impl.mps.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Person;

public class ResponseEntreprises {

	private Company entreprise;
	private Establishment etablissement_siege;

	private Map<String, Object> otherProperties = new HashMap<String, Object>();

	public Company getEntreprise(){
		return this.entreprise;
	}

	public void setEntreprise(Company entreprise){
		this.entreprise = entreprise;
	}

	public Establishment getEtablissement_siege(){
		return this.etablissement_siege;
	}

	public void setEtablissement_siege(Establishment etablissement_siege) {
		this.etablissement_siege = etablissement_siege;
	}

	public Map<String, Object> getOtherProperties() {
		return otherProperties;
	}

	public void setOtherProperties(Map<String, Object> otherProperties) {
		this.otherProperties = otherProperties;
	}

	@Override
	public String toString() {
		return "ResponseEntreprises{" +
				"entreprise=" + entreprise +
				", etablissement_siege=" + etablissement_siege +
				", otherProperties=" + otherProperties +
				'}';
	}


}
