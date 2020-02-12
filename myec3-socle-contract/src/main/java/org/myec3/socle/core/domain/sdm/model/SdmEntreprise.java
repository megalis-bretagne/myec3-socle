package org.myec3.socle.core.domain.sdm.model;

import java.util.List;

public class SdmEntreprise {

    private String id;
    private String siren;
    private String effectif;
    private SdmAdresse adresse;
    private String formeJuridique;
    private String codeAPE;
    private String dateModification;
    private String capitalSocial;
    private String raisonSociale;
    private String email;
    private List<SdmEtablissement> etablisssements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getEffectif() {
        return effectif;
    }

    public void setEffectif(String effectif) {
        this.effectif = effectif;
    }

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getCodeAPE() {
        return codeAPE;
    }

    public void setCodeAPE(String codeAPE) {
        this.codeAPE = codeAPE;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }

    public String getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(String capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SdmEtablissement> getEtablisssements() {
        return etablisssements;
    }

    public void setEtablisssements(List<SdmEtablissement> etablisssements) {
        this.etablisssements = etablisssements;
    }
}
