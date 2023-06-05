package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvUniteLegale {

    @JsonProperty("siren")
    public String siren;

    @JsonProperty("siret_siege_social")
    public String siretSiegeSocial;

    @JsonProperty("categorie_entreprise")
    public String categorieEntreprise;

    @JsonProperty("type")
    public String type;

    @JsonProperty("diffusable_commercialement")
    public String diffusableCommercialement;

    @JsonProperty("status_diffusion")
    public String statusDiffusion;

    @JsonProperty("etat_administratif")
    public String etatAdministratif;

    @JsonProperty("economie_sociale_et_solidaire")
    public String economieSocialeEtSolidaire;

    @JsonProperty("date_cessation")
    public String dateCessation;

    @JsonProperty("date_creation")
    public String dateCreation;


    @JsonProperty("personne_morale_attributs")
    public ApiGouvPersonneMorale personneMoraleAttributs;

    @JsonProperty("personne_physique_attributs")
    public ApiGouvPersonnePhysique personnePhysiqueAttributs;

    @JsonProperty("forme_juridique")
    public ApiGouvFormeJuridique formeJuridique;

    @JsonProperty("activite_principale")
    public ApiGouvActivitePrincipale activitePrincipale;

    @JsonProperty("tranche_effectif_salarie")
    public ApiGouvTrancheEffectifSalarie trancheEffectifSalarie;

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getSiretSiegeSocial() {
        return siretSiegeSocial;
    }

    public void setSiretSiegeSocial(String siretSiegeSocial) {
        this.siretSiegeSocial = siretSiegeSocial;
    }

    public String getCategorieEntreprise() {
        return categorieEntreprise;
    }

    public void setCategorieEntreprise(String categorieEntreprise) {
        this.categorieEntreprise = categorieEntreprise;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getEtatAdministratif() {
        return etatAdministratif;
    }

    public void setEtatAdministratif(String etatAdministratif) {
        this.etatAdministratif = etatAdministratif;
    }

    public String getEconomieSocialeEtSolidaire() {
        return economieSocialeEtSolidaire;
    }

    public void setEconomieSocialeEtSolidaire(String economieSocialeEtSolidaire) {
        this.economieSocialeEtSolidaire = economieSocialeEtSolidaire;
    }

    public String getDateCessation() {
        return dateCessation;
    }

    public void setDateCessation(String dateCessation) {
        this.dateCessation = dateCessation;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public ApiGouvPersonneMorale getPersonneMoraleAttributs() {
        return personneMoraleAttributs;
    }

    public void setPersonneMoraleAttributs(ApiGouvPersonneMorale personneMoraleAttributs) {
        this.personneMoraleAttributs = personneMoraleAttributs;
    }

    public ApiGouvPersonnePhysique getPersonnePhysiqueAttributs() {
        return personnePhysiqueAttributs;
    }

    public void setPersonnePhysiqueAttributs(ApiGouvPersonnePhysique personnePhysiqueAttributs) {
        this.personnePhysiqueAttributs = personnePhysiqueAttributs;
    }

    public ApiGouvFormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(ApiGouvFormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
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
}
