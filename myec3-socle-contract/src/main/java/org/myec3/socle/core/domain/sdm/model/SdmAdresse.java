package org.myec3.socle.core.domain.sdm.model;

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
        this.rue = rue;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getAcronymePays() {
        return acronymePays;
    }

    public void setAcronymePays(String acronymePays) {
        this.acronymePays = acronymePays;
    }
}
