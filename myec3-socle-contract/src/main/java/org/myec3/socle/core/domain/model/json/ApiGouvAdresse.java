package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvAdresse {

    @JsonProperty("status_diffusion")
    public String statusDiffusion;

    @JsonProperty("complement_adresse")
    public String complementAdresse;

    @JsonProperty("numero_voie")
    public String numeroVoie;

    @JsonProperty("indice_repetition_voie")
    public String indiceRepetitionVoie;

    @JsonProperty("type_voie")
    public String typeVoie;

    @JsonProperty("libelle_voie")
    public String libelleVoie;

    @JsonProperty("code_postal")
    public String codePostal;

    @JsonProperty("libelle_commune")
    public String libelleCommune;

    @JsonProperty("libelle_commune_etranger")
    public String libelleCommunEtranger;

    @JsonProperty("distribution_speciale")
    public String distributionSpeciale;

    @JsonProperty("code_commune")
    public String codeCommune;

    @JsonProperty("code_cedex")
    public String codeCedex;


    @JsonProperty("libelle_cedex")
    public String libelleCedex;


    @JsonProperty("code_pays_etranger")
    public String codePaysEtranger;

    @JsonProperty("libelle_pays_etranger")
    public String libellePaysEtranger;

    @JsonProperty("acheminement_postal")
    public AcheminementPostal acheminementPostal;


    private class AcheminementPostal {

        @JsonProperty("l1")
        public String l1;

        @JsonProperty("l2")
        public String l2;

        @JsonProperty("l3")
        public String l3;

        @JsonProperty("l4")
        public String l4;

        @JsonProperty("l5")
        public String l5;

        @JsonProperty("l6")
        public String l6;

        @JsonProperty("l7")
        public String l7;

        public String getL1() {
            return l1;
        }

        public void setL1(String l1) {
            this.l1 = l1;
        }

        public String getL2() {
            return l2;
        }

        public void setL2(String l2) {
            this.l2 = l2;
        }

        public String getL3() {
            return l3;
        }

        public void setL3(String l3) {
            this.l3 = l3;
        }

        public String getL4() {
            return l4;
        }

        public void setL4(String l4) {
            this.l4 = l4;
        }

        public String getL5() {
            return l5;
        }

        public void setL5(String l5) {
            this.l5 = l5;
        }

        public String getL6() {
            return l6;
        }

        public void setL6(String l6) {
            this.l6 = l6;
        }

        public String getL7() {
            return l7;
        }

        public void setL7(String l7) {
            this.l7 = l7;
        }
    }

    public String getStatusDiffusion() {
        return statusDiffusion;
    }

    public void setStatusDiffusion(String statusDiffusion) {
        this.statusDiffusion = statusDiffusion;
    }

    public String getComplementAdresse() {
        return complementAdresse;
    }

    public void setComplementAdresse(String complementAdresse) {
        this.complementAdresse = complementAdresse;
    }

    public String getNumeroVoie() {
        return numeroVoie;
    }

    public void setNumeroVoie(String numeroVoie) {
        this.numeroVoie = numeroVoie;
    }

    public String getIndiceRepetitionVoie() {
        return indiceRepetitionVoie;
    }

    public void setIndiceRepetitionVoie(String indiceRepetitionVoie) {
        this.indiceRepetitionVoie = indiceRepetitionVoie;
    }

    public String getTypeVoie() {
        return typeVoie;
    }

    public void setTypeVoie(String typeVoie) {
        this.typeVoie = typeVoie;
    }

    public String getLibelleVoie() {
        return libelleVoie;
    }

    public void setLibelleVoie(String libelleVoie) {
        this.libelleVoie = libelleVoie;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getLibelleCommune() {
        return libelleCommune;
    }

    public void setLibelleCommune(String libelleCommune) {
        this.libelleCommune = libelleCommune;
    }

    public String getLibelleCommunEtranger() {
        return libelleCommunEtranger;
    }

    public void setLibelleCommunEtranger(String libelleCommunEtranger) {
        this.libelleCommunEtranger = libelleCommunEtranger;
    }

    public String getDistributionSpeciale() {
        return distributionSpeciale;
    }

    public void setDistributionSpeciale(String distributionSpeciale) {
        this.distributionSpeciale = distributionSpeciale;
    }

    public String getCodeCommune() {
        return codeCommune;
    }

    public void setCodeCommune(String codeCommune) {
        this.codeCommune = codeCommune;
    }

    public String getCodeCedex() {
        return codeCedex;
    }

    public void setCodeCedex(String codeCedex) {
        this.codeCedex = codeCedex;
    }

    public String getLibelleCedex() {
        return libelleCedex;
    }

    public void setLibelleCedex(String libelleCedex) {
        this.libelleCedex = libelleCedex;
    }

    public String getCodePaysEtranger() {
        return codePaysEtranger;
    }

    public void setCodePaysEtranger(String codePaysEtranger) {
        this.codePaysEtranger = codePaysEtranger;
    }

    public String getLibellePaysEtranger() {
        return libellePaysEtranger;
    }

    public void setLibellePaysEtranger(String libellePaysEtranger) {
        this.libellePaysEtranger = libellePaysEtranger;
    }

    public AcheminementPostal getAcheminementPostal() {
        return acheminementPostal;
    }

    public void setAcheminementPostal(AcheminementPostal acheminementPostal) {
        this.acheminementPostal = acheminementPostal;
    }
}
