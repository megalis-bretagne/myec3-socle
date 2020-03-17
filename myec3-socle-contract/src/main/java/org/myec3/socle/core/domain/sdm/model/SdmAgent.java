package org.myec3.socle.core.domain.sdm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

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
      boolean actif;

      @XmlElement(required = false)
      private String telephone;

      @XmlElement(required = false)
      private String fax;

      @XmlElement(required = true)
      private Date dateCreation;

      @XmlElement(required = true)
      private Date dateModification;

      //@XmlElement(required = true)
      private SdmService service;

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
            this.identifiant = identifiant;
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

      public String getFax() {
            return fax;
      }

      public void setFax(String fax) {
            this.fax = fax;
      }

      public Date getDateCreation() {
            return dateCreation;
      }

      public void setDateCreation(Date dateCreation) {
            this.dateCreation = dateCreation;
      }

      public Date getDateModification() {
            return dateModification;
      }

      public void setDateModification(Date dateModification) {
            this.dateModification = dateModification;
      }

      public SdmService getService() {
            return service;
      }

      public void setService(SdmService service) {
            this.service = service;
      }
}
