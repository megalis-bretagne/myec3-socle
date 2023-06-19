package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvPersonnePhysique {

    @JsonProperty("pseudonyme")
    public String pseudonyme;


    @JsonProperty("prenom_usuel")
    public String prenom_usuel;


    @JsonProperty("prenom_1")
    public String prenom1;


    @JsonProperty("prenom_2")
    public String prenom2;


    @JsonProperty("prenom_3")
    public String prenom3;


    @JsonProperty("prenom_4")
    public String prenom4;


    @JsonProperty("nom_usage")
    public String nomUsage;


    @JsonProperty("nom_naissance")
    public String nomNaissance;


    @JsonProperty("sexe")
    public String sexe;

    public String getPseudonyme() {
        return pseudonyme;
    }

    public void setPseudonyme(String pseudonyme) {
        this.pseudonyme = pseudonyme;
    }

    public String getPrenom_usuel() {
        return prenom_usuel;
    }

    public void setPrenom_usuel(String prenom_usuel) {
        this.prenom_usuel = prenom_usuel;
    }

    public String getPrenom1() {
        return prenom1;
    }

    public void setPrenom1(String prenom1) {
        this.prenom1 = prenom1;
    }

    public String getPrenom2() {
        return prenom2;
    }

    public void setPrenom2(String prenom2) {
        this.prenom2 = prenom2;
    }

    public String getPrenom3() {
        return prenom3;
    }

    public void setPrenom3(String prenom3) {
        this.prenom3 = prenom3;
    }

    public String getPrenom4() {
        return prenom4;
    }

    public void setPrenom4(String prenom4) {
        this.prenom4 = prenom4;
    }

    public String getNomUsage() {
        return nomUsage;
    }

    public void setNomUsage(String nomUsage) {
        this.nomUsage = nomUsage;
    }

    public String getNomNaissance() {
        return nomNaissance;
    }

    public void setNomNaissance(String nomNaissance) {
        this.nomNaissance = nomNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }


    @Override
    public String toString() {
        return "ApiGouvPersonnePhysique{" +
                "pseudonyme='" + pseudonyme + '\'' +
                ", prenom_usuel='" + prenom_usuel + '\'' +
                ", prenom1='" + prenom1 + '\'' +
                ", prenom2='" + prenom2 + '\'' +
                ", prenom3='" + prenom3 + '\'' +
                ", prenom4='" + prenom4 + '\'' +
                ", nomUsage='" + nomUsage + '\'' +
                ", nomNaissance='" + nomNaissance + '\'' +
                ", sexe='" + sexe + '\'' +
                '}';
    }
}
