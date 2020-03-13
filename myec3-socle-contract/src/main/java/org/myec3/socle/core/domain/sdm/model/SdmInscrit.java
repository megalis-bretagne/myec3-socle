package org.myec3.socle.core.domain.sdm.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class SdmInscrit extends SdmResource {

    @XmlElement(required = false)
    private long id;

    @XmlElement(required = true)
    private long idEtablissement;

    @XmlElement(required = true)
    private String login;

    @XmlElement(required = true)
    private String motdePasse;

    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String nom;

    @XmlElement(required = true)
    private String prenom;

    @XmlElement(required = true)
    private String acronymeOrganisme;

    @XmlElement(required = true)
    boolean actif;

    @XmlElement(required = false)
    private String telephone;

    @XmlElement(required = true)
    private String siret;

    @XmlElement(required = false)
    private int inscritAnnuaireDefense;

    @XmlElement(required = false)
    private String typeHash;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdEtablissement() {
        return idEtablissement;
    }

    public void setIdEtablissement(long idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotdePasse() {
        return motdePasse;
    }

    public void setMotdePasse(String motdePasse) {
        this.motdePasse = motdePasse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAcronymeOrganisme() {
        return acronymeOrganisme;
    }

    public void setAcronymeOrganisme(String acronymeOrganisme) {
        this.acronymeOrganisme = acronymeOrganisme;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public int getInscritAnnuaireDefense() {
        return inscritAnnuaireDefense;
    }

    public void setInscritAnnuaireDefense(int inscritAnnuaireDefense) {
        this.inscritAnnuaireDefense = inscritAnnuaireDefense;
    }

    public String getTypeHash() {
        return typeHash;
    }

    public void setTypeHash(String typeHash) {
        this.typeHash = typeHash;
    }
}
