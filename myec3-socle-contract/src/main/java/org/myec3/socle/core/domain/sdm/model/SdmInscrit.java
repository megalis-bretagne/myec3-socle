package org.myec3.socle.core.domain.sdm.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class SdmInscrit extends SdmResource {

    @XmlElement(required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    @XmlElement(required = true)
    private Long idEtablissement;

    @XmlElement(required = true)
    private String login;

    @XmlElement(required = true)
    private String motDePasse;

    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String nom;

    @XmlElement(required = true)
    private String prenom;

    @XmlElement(required = true)
    String actif="1";

    @XmlElement(required = false)
    private String telephone;

    @XmlElement(required = true)
    private String siret;

    @XmlElement(required = false)
    private int inscritAnnuaireDefense=0;

    @XmlElement(required = false)
    private String typeHash;

    private SdmAdresse adresse;

    @XmlElement(required = true)
    private long profil=1;

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = Objects.toString(motDePasse,"");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEtablissement() {
        return idEtablissement;
    }

    public void setIdEtablissement(Long idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    public long getProfil() {
        return profil;
    }

    public void setProfil(long profil) {
        this.profil = profil;
    }



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = Objects.toString(login,"");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.toString(email,"");
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = Objects.toString(nom,"");
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = Objects.toString(prenom,"");
    }

    public String getActif() {
        return actif;
    }

    public void setActif(String actif) {
        this.actif = actif;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = Objects.toString(telephone,"");
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = Objects.toString(siret,"");
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
        this.typeHash = Objects.toString(typeHash,"");
    }
}
