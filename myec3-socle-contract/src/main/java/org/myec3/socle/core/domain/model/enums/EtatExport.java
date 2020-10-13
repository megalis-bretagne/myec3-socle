package org.myec3.socle.core.domain.model.enums;


public enum EtatExport {

	OK("Réalisé"),AF("Demandé"), KO("En erreur"),AN("annulé"), EC("En cours'");

	private final String label;

	private EtatExport(String label) {
		this.label = label;
	}


	public String getLabel() {
		return label;
	}


	@Override
	public String toString() {
		return this.label;
	}
}
