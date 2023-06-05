package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvFormeJuridique {

    @JsonProperty("code")
    public String code;

    @JsonProperty("libelle")
    public String libelle;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
