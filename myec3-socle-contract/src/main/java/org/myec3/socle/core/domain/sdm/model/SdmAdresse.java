package org.myec3.socle.core.domain.sdm.model;

import java.util.Objects;

public class SdmAdresse {

    private String rue;
    private String codePostal;
    private String ville;
    private String pays;
    private String acronymePays;

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = Objects.toString(rue,"");
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = Objects.toString(codePostal,"");
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = Objects.toString(ville,"");
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = Objects.toString(pays,"");
    }

    public String getAcronymePays() {
        return acronymePays;
    }

    public void setAcronymePays(String acronymePays) {
        this.acronymePays = Objects.toString(acronymePays,"");
    }
}
