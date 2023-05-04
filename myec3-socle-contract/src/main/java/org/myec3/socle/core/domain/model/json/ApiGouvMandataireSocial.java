package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvMandataireSocial {


    @JsonProperty("type")
    public String type;

    @JsonProperty("fonction")
    public String fonction;

    //physique

    @JsonProperty("nom")
    public String nom;

    @JsonProperty("prenom")
    public String prenom;

    @JsonProperty("date_naissance")
    public String dateNaissance;

    @JsonProperty("date_naissance_timestamp")
    public String dateNaissanceTimestamp;

    @JsonProperty("lieu_naissance")
    public String lieu_naissance;

    @JsonProperty("pays_naissance")
    public String paysNaissance;

    @JsonProperty("code_pays_naissance")
    public String codePaysNaissance;

    @JsonProperty("nationalite")
    public String nationalite;

    @JsonProperty("code_nationalite")
    public String codeNationalite;

    //morale

    @JsonProperty("numero_identification")
    public String numeroIdentification;

    @JsonProperty("raison_sociale")
    public String raisonSociale;


    @JsonProperty("code_greffe")
    public String codeGreffe;


    @JsonProperty("libelle_greffe")
    public String libelleGreffe;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getDateNaissanceTimestamp() {
        return dateNaissanceTimestamp;
    }

    public void setDateNaissanceTimestamp(String dateNaissanceTimestamp) {
        this.dateNaissanceTimestamp = dateNaissanceTimestamp;
    }

    public String getLieu_naissance() {
        return lieu_naissance;
    }

    public void setLieu_naissance(String lieu_naissance) {
        this.lieu_naissance = lieu_naissance;
    }

    public String getPaysNaissance() {
        return paysNaissance;
    }

    public void setPaysNaissance(String paysNaissance) {
        this.paysNaissance = paysNaissance;
    }

    public String getCodePaysNaissance() {
        return codePaysNaissance;
    }

    public void setCodePaysNaissance(String codePaysNaissance) {
        this.codePaysNaissance = codePaysNaissance;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getCodeNationalite() {
        return codeNationalite;
    }

    public void setCodeNationalite(String codeNationalite) {
        this.codeNationalite = codeNationalite;
    }

    public String getNumeroIdentification() {
        return numeroIdentification;
    }

    public void setNumeroIdentification(String numeroIdentification) {
        this.numeroIdentification = numeroIdentification;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getCodeGreffe() {
        return codeGreffe;
    }

    public void setCodeGreffe(String codeGreffe) {
        this.codeGreffe = codeGreffe;
    }

    public String getLibelleGreffe() {
        return libelleGreffe;
    }

    public void setLibelleGreffe(String libelleGreffe) {
        this.libelleGreffe = libelleGreffe;
    }
}
