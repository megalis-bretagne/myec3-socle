package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvActivitePrincipale {

    @JsonProperty("code")
    public String code;

    @JsonProperty("nomenclature")
    public String nomenclature;

    @JsonProperty("libelle")
    public String libelle;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(String nomenclature) {
        this.nomenclature = nomenclature;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "ApiGouvActivitePrincipale{" +
                "code='" + code + '\'' +
                ", nomenclature='" + nomenclature + '\'' +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
