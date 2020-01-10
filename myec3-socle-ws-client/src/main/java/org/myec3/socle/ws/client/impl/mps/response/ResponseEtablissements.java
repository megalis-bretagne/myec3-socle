package org.myec3.socle.ws.client.impl.mps.response;

import org.myec3.socle.core.domain.model.Establishment;

import javax.ws.rs.core.Response.Status;

/* Java Class used for the Etablissements WS response */
public class ResponseEtablissements {

	private Establishment etablissement;
	private Boolean gateway_error;
	private Status gateway_error_code;

	public Establishment getEtablissement(){
		return this.etablissement;
	}

	public void setEtablissement(Establishment etablissement){
		this.etablissement = etablissement;
	}

	public Status getGateway_error_code() {
		return gateway_error_code;
	}

	public void setGateway_error_code(Status gateway_error_code) {
		this.gateway_error_code = gateway_error_code;
	}

	@Override
	public String toString() {
		return "ResponseEtablissements{" +
				"etablissement=" + etablissement +
				", gateway_error_code=" + gateway_error_code +
				'}';
	}

}
