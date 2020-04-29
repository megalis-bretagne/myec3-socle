package org.myec3.socle.core.domain.sdm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class SdmAgent extends SdmResource {


      @XmlElement(required = false)
      private long id;

      @XmlElement(required = false)
      private long idProfil;

      @XmlElement(required = true)
      private String identifiant;

      @XmlElement(required = true)
      private String email;

      @XmlElement(required = true)
      private String nom;

      @XmlElement(required = true)
      private String prenom;

      @XmlElement(required = true)
      private String acronymeOrganisme;

      @XmlElement(required = true)
      String actif="1";

      @XmlElement(required = false)
      private String telephone;

      @XmlElement(required = false)
      private String fax;

      @JsonInclude(JsonInclude.Include.NON_NULL)
      private SdmService service;

      @XmlElement(required = false)
      private SdmAdresse adresse;

      public SdmAdresse getAdresse() {
            return adresse;
      }

      public void setAdresse(SdmAdresse adresse) {
            this.adresse = adresse;
      }

      public long getId() {
            return id;
      }

      public void setId(long id) {
            this.id = id;
      }

      public long getIdProfil() {
            return idProfil;
      }

      public void setIdProfil(long idProfil) {
            this.idProfil = idProfil;
      }

      public String getIdentifiant() {
            return identifiant;
      }

      public void setIdentifiant(String identifiant) {
            this.identifiant = Objects.toString(identifiant,"");
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

      public String getAcronymeOrganisme() {
            return acronymeOrganisme;
      }

      public void setAcronymeOrganisme(String acronymeOrganisme) {
            this.acronymeOrganisme = Objects.toString(acronymeOrganisme,"");
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

      public String getFax() {
            return fax;
      }

      public void setFax(String fax) {
            this.fax = Objects.toString(fax,"");
      }

      public SdmService getService() {
            return service;
      }

      public void setService(SdmService service) {
            this.service = service;
      }
}
