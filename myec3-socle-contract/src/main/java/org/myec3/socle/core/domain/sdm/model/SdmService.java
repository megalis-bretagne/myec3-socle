package org.myec3.socle.core.domain.sdm.model;

public class SdmService {

    private String id;
    private String idParent;
    private String idExterne;
    private String idExterneParent;
    private String libelle;
    private String acronymeOrganisme;
    private String sigle;
    private String siren;
    private String complement;
    private String email;
    private String formeJuridique;
    private String formeJuridiqueCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getIdExterne() {
        return idExterne;
    }

    public void setIdExterne(String idExterne) {
        this.idExterne = idExterne;
    }

    public String getIdExterneParent() {
        return idExterneParent;
    }

    public void setIdExterneParent(String idExterneParent) {
        this.idExterneParent = idExterneParent;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getAcronymeOrganisme() {
        return acronymeOrganisme;
    }

    public void setAcronymeOrganisme(String acronymeOrganisme) {
        this.acronymeOrganisme = acronymeOrganisme;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getFormeJuridiqueCode() {
        return formeJuridiqueCode;
    }

    public void setFormeJuridiqueCode(String formeJuridiqueCode) {
        this.formeJuridiqueCode = formeJuridiqueCode;
    }
}
