package org.myec3.socle.core.domain.sdm.model;

import java.util.Date;

public class SdmAgent {

      private Long idProfil;
      private Long identifiant;
      private String email;
      private String nom;
      private String prenom;
      private String acronymeOrganisme;
      boolean actif;
      private String telephone;
      private String fax;
      private Date dateCreation;
      private Date dateModification;
      private SdmService service;

      public Long getIdProfil() {
            return idProfil;
      }

      public void setIdProfil(Long idProfil) {
            this.idProfil = idProfil;
      }

      public Long getIdentifiant() {
            return identifiant;
      }

      public void setIdentifiant(Long identifiant) {
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
