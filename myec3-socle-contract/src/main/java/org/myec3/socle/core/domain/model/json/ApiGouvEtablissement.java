package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvEtablissement {

    @JsonProperty("siret")
    public String siret;

    @JsonProperty("siege_social")
    public String siegeSocial;

    @JsonProperty("categorie_entreprise")
    public String categorie_entreprise;

    @JsonProperty("etat_administratif")
    public String etatAdministratif;

    @JsonProperty("date_fermeture")
    public String dateFermeture;

    @JsonProperty("enseigne")
    public String enseigne;

    @JsonProperty("diffusable_commercialement")
    public String diffusableCommercialement;

    @JsonProperty("status_diffusion")
    public String statusDiffusion;

    @JsonProperty("date_creation")
    public String dateCreation;

    @JsonProperty("activite_principale")
    public ApiGouvActivitePrincipale activitePrincipale;

    @JsonProperty("tranche_effectif_salarie")
    public ApiGouvTrancheEffectifSalarie trancheEffectifSalarie;

    @JsonProperty("unite_legale")
    public ApiGouvUniteLegale uniteLegale;

    @JsonProperty("adresse")
    public ApiGouvAdresse adresse;

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getSiegeSocial() {
        return siegeSocial;
    }

    public void setSiegeSocial(String siegeSocial) {
        this.siegeSocial = siegeSocial;
    }

    public String getCategorie_entreprise() {
        return categorie_entreprise;
    }

    public void setCategorie_entreprise(String categorie_entreprise) {
        this.categorie_entreprise = categorie_entreprise;
    }

    public String getEtatAdministratif() {
        return etatAdministratif;
    }

    public void setEtatAdministratif(String etatAdministratif) {
        this.etatAdministratif = etatAdministratif;
    }

    public String getDateFermeture() {
        return dateFermeture;
    }

    public void setDateFermeture(String dateFermeture) {
        this.dateFermeture = dateFermeture;
    }

    public String getEnseigne() {
        return enseigne;
    }

    public void setEnseigne(String enseigne) {
        this.enseigne = enseigne;
    }

    public String getDiffusableCommercialement() {
        return diffusableCommercialement;
    }

    public void setDiffusableCommercialement(String diffusableCommercialement) {
        this.diffusableCommercialement = diffusableCommercialement;
    }

    public String getStatusDiffusion() {
        return statusDiffusion;
    }

    public void setStatusDiffusion(String statusDiffusion) {
        this.statusDiffusion = statusDiffusion;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public ApiGouvActivitePrincipale getActivitePrincipale() {
        return activitePrincipale;
    }

    public void setActivitePrincipale(ApiGouvActivitePrincipale activitePrincipale) {
        this.activitePrincipale = activitePrincipale;
    }

    public ApiGouvTrancheEffectifSalarie getTrancheEffectifSalarie() {
        return trancheEffectifSalarie;
    }

    public void setTrancheEffectifSalarie(ApiGouvTrancheEffectifSalarie trancheEffectifSalarie) {
        this.trancheEffectifSalarie = trancheEffectifSalarie;
    }

    public ApiGouvUniteLegale getUniteLegale() {
        return uniteLegale;
    }

    public void setUniteLegale(ApiGouvUniteLegale uniteLegale) {
        this.uniteLegale = uniteLegale;
    }

    public ApiGouvAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(ApiGouvAdresse adresse) {
        this.adresse = adresse;
    }
}
