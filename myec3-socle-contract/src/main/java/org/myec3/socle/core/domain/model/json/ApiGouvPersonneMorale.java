package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvPersonneMorale {

    @JsonProperty("raison_sociale")
    public String raisonSociale;

    @JsonProperty("sigle")
    public String sigle;

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    @Override
    public String toString() {
        return "ApiGouvPersonneMorale{" +
                "raisonSociale='" + raisonSociale + '\'' +
                ", sigle='" + sigle + '\'' +
                '}';
    }
}
