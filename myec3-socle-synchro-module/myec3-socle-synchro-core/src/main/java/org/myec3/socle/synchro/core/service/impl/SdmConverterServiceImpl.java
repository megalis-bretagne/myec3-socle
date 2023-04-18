package org.myec3.socle.synchro.core.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.*;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.service.SdmConverterService;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("sdmConverterService")
public class SdmConverterServiceImpl implements SdmConverterService {


    private static final Logger logger = LoggerFactory.getLogger(SdmConverterService.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;


    @Override
    public SdmAgent convertToSdmAgent(AgentProfile resource) {
        SdmAgent agentSDM = new SdmAgent();
        agentSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        agentSDM.setIdentifiant(resource.getUsername());

        //mapping du role
        if (resource.getRoles() != null && !resource.getRoles().isEmpty()) {
            for (Role role : resource.getRoles()) {
                if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(role.getApplication().getName())) {
                    agentSDM.setIdProfil(role.getExternalId());
                    break;
                }
            }
        }

        agentSDM.setEmail(resource.getEmail());
        agentSDM.setNom(resource.getUser().getLastname());
        agentSDM.setPrenom(resource.getUser().getFirstname());

        int myInt = resource.isEnabled() ? 1 : 0;
        agentSDM.setActif(String.valueOf(myInt));

        if (!StringUtils.isEmpty(resource.getPhone())) {
            agentSDM.setTelephone(resource.getPhone());
        }
        if (!StringUtils.isEmpty(resource.getCellPhone())) {
            agentSDM.setTelephone(resource.getCellPhone());
        }
        agentSDM.setFax(resource.getFax());


        //On réalise une requete sur SynchroIdentifiantExterne pour récupérer l'acronyme retourné par la SDM
        if (resource.getOrganismDepartment() != null && resource.getOrganismDepartment().getOrganism() != null) {
            SynchroIdentifiantExterne synchroIdentifiantExterneOrganisme = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganismDepartment().getOrganism().getId(), ResourceType.ORGANISM);
            agentSDM.setAcronymeOrganisme(synchroIdentifiantExterneOrganisme.getAcronyme());
        }

        agentSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));
        // on ne renseigne pas le service dans le cas d'un service root
        if (BooleanUtils.isFalse(resource.getOrganismDepartment().isRootDepartment())) {
            SdmService serviceSDM = new SdmService();
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganismDepartment().getId(), ResourceType.ORGANISM_DEPARTMENT);
            if (synchroIdentifiantExterne != null) {
                serviceSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
            } else {
                logger.warn("Agent {} n'a pas de ORGANISM_DEPARTMENT SDM dans la table synchroIdentifiantExterneService pour l'idSocle {}", resource.getId(), resource.getOrganismDepartment().getId());
            }
            agentSDM.setService(serviceSDM);
        }

        return agentSDM;
    }


    @Override
    public SdmEntreprise convertSdmEntreprise(Company resource) {
        SdmEntreprise entrepriseSDM = new SdmEntreprise();
        entrepriseSDM.setIdExterne(String.valueOf(resource.getExternalId()));
        entrepriseSDM.setSiren(resource.getSiren());
        if (BooleanUtils.isTrue(resource.getForeignIdentifier())) {
            entrepriseSDM.setSirenEtranger(resource.getNationalID());
            entrepriseSDM.setPaysenregistrement(resource.getRegistrationCountry().name());
        }
        entrepriseSDM.setFormeJuridique(resource.getLegalCategory().getLabel());
        entrepriseSDM.setCodeAPE(resource.getApeCode());
        entrepriseSDM.setEmail(resource.getEmail());
        entrepriseSDM.setRaisonSociale(resource.getLabel());
        //pas de mapping trouvé pour les deux champs ci-dessous
        entrepriseSDM.setCapitalSocial("");
        entrepriseSDM.setEffectif("");
        entrepriseSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));

        return entrepriseSDM;
    }

    @Override
    public SdmInscrit convertToSdmInscrit(EmployeeProfile resource) {

        SdmInscrit inscritSDM = new SdmInscrit();
        inscritSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        //mapping du role
        if (resource.getRoles() != null && !resource.getRoles().isEmpty()) {
            for (Role role : resource.getRoles()) {
                if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(role.getApplication().getName())) {
                    inscritSDM.setProfil(role.getExternalId());
                    break;
                }
            }
        }

        inscritSDM.setLogin(resource.getUsername());
        inscritSDM.setEmail(resource.getEmail());
        inscritSDM.setNom(resource.getUser().getLastname());
        inscritSDM.setPrenom(resource.getUser().getFirstname());
        int myInt = resource.isEnabled() ? 1 : 0;
        inscritSDM.setActif(String.valueOf(myInt));
        inscritSDM.setTelephone(resource.getPhone());
        inscritSDM.setMotDePasse(resource.getUser().getPassword());
        inscritSDM.setTypeHash("sha256");
        inscritSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));
        //inscritSDM.setInscritAnnuaireDefense();

        if (resource.getEstablishment() != null) {
            inscritSDM.setSiret(resource.getEstablishment().getSiret());
            List<SynchroIdentifiantExterne> synchro = synchroIdentifiantExterneService.findListByIdSocle(resource.getEstablishment().getId(), ResourceType.ESTABLISHMENT);
            if (synchro != null && !synchro.isEmpty()) {
                inscritSDM.setIdEtablissement(synchro.get(0).getIdAppliExterne());
                if (synchro.size() > 1) {
                    logger.warn("Establishment id: {} a plusieurs IdAppliExterne en bdd ", resource.getId());
                    for (SynchroIdentifiantExterne s : synchro) {
                        logger.warn(" idSocle {} IdAppliExterne {} ", s.getIdSocle(), s.getIdAppliExterne());
                    }
                }

            } else {
                logger.warn("EmployeeProfile id: {} n'a pas de ESTABLISHMENT SDM dans la table synchroIdentifiantExterneService pour l'idSocle ", resource.getId(), resource.getEstablishment().getId());
            }
        } else {
            logger.warn("EmployeeProfile id: {} n'a pas de resource.getEstablishment().getId() il est donc impossible de rechercher dans la table synchroIdentifiantExterneService", resource.getId());
        }
        return inscritSDM;
    }

    @Override
    public SdmEtablissement convertSdmEtablissement(Establishment resource) {

        SdmEtablissement etablissementSDM = new SdmEtablissement();
        etablissementSDM.setIdExterne(String.valueOf(resource.getExternalId()));
        int siege = resource.getIsHeadOffice() ? 1 : 0;
        etablissementSDM.setSiege(String.valueOf(siege));

        etablissementSDM.setSiret(resource.getSiret());

        if (resource.getCompany() != null && resource.getCompany().getId() != null) {
            List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getCompany().getId(), ResourceType.COMPANY);
            if (resource.getCompany().getForeignIdentifier()) {
                etablissementSDM.setCodeEtablissement(resource.getCompany().getNationalID());
            }

            if (synchroIdentifiantExterne != null && !synchroIdentifiantExterne.isEmpty()) {
                etablissementSDM.setIdEntreprise(String.valueOf(synchroIdentifiantExterne.get(0).getIdAppliExterne()));

                if (synchroIdentifiantExterne.size() > 1) {
                    logger.warn("Company id: {} a plusieurs IdAppliExterne en bdd ", resource.getCompany().getId());
                    for (SynchroIdentifiantExterne s : synchroIdentifiantExterne) {
                        logger.warn(" idSocle {} IdAppliExterne {} ", s.getIdSocle(), s.getIdAppliExterne());
                    }
                }
            } else {
                logger.warn("Establishment id: {} n'a pas de COMPANY SDM dans la table synchroIdentifiantExterneService pour l'idSocle ", resource.getId(), resource.getCompany().getId());
            }
        }
        etablissementSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));
        return etablissementSDM;
    }

    @Override
    public SdmService convertToSdmService(OrganismDepartment resource) {
        SdmService serviceSDM = new SdmService();
        serviceSDM.setIdExterne(String.valueOf(resource.getExternalId()));
        serviceSDM.setLibelle(resource.getLabel());
        if (StringUtils.isEmpty(resource.getAbbreviation())) {
            serviceSDM.setSigle("/");
        } else {
            serviceSDM.setSigle(resource.getAbbreviation());
        }

        if (resource.getOrganism() != null) {
            serviceSDM.setSiren(resource.getOrganism().getSiren());
            serviceSDM.setComplement(resource.getOrganism().getNic());
            serviceSDM.setFormeJuridique(resource.getOrganism().getStrutureLegalCategory().toString());
            serviceSDM.setFormeJuridiqueCode(resource.getOrganism().getLegalCategory().toString());
            SynchroIdentifiantExterne synchroOrganism = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganism().getId(), ResourceType.ORGANISM);
            if (synchroOrganism != null) {
                serviceSDM.setAcronymeOrganisme(synchroOrganism.getAcronyme());
            } else {
                serviceSDM.setAcronymeOrganisme(resource.getOrganism().getAcronym());
            }
        }
        serviceSDM.setEmail(resource.getEmail());

        if (!resource.isRootDepartment() && resource.getParentDepartment() != null) {
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getParentDepartment().getId(), ResourceType.ORGANISM_DEPARTMENT);
            if (synchroIdentifiantExterne != null) {
                serviceSDM.setIdExterneParent(resource.getParentDepartment().getId());
                serviceSDM.setIdParent(synchroIdentifiantExterne.getIdAppliExterne());
            }
        }
        return serviceSDM;
    }

    @Override
    public SdmOrganisme convertSdmOrganisme(Organism resource) {
        SdmOrganisme organismeSDM = new SdmOrganisme();
        organismeSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        organismeSDM.setId(0l);
        organismeSDM.setAcronyme(resource.getAcronym());
        organismeSDM.setEmail(resource.getEmail());
        organismeSDM.setUrl(resource.getWebsite());
        organismeSDM.setTel(resource.getPhone());
        organismeSDM.setSigle(resource.getLabel());
        organismeSDM.setCategorieInsee(resource.getLegalCategory().getId());
        organismeSDM.setDenomination(resource.getLabel());
        organismeSDM.setSiren(resource.getSiren());
        organismeSDM.setNic(resource.getNic());
        organismeSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));

        return organismeSDM;
    }


    @Override
    public SdmAdresse convertToSdmAdresse(Address resourceAddress) {
        if (resourceAddress != null) {
            SdmAdresse adresseSDM = new SdmAdresse();
            adresseSDM.setCodePostal(resourceAddress.getPostalCode());
            if (resourceAddress.getCountry() != null) {
                adresseSDM.setPays(resourceAddress.getCountry().getLabel());
            }
            adresseSDM.setRue(resourceAddress.getPostalAddress());
            adresseSDM.setVille(resourceAddress.getCity());
            if (resourceAddress.getCountry() != null) {
                adresseSDM.setAcronymePays(resourceAddress.getCountry().name());
            }
            return adresseSDM;
        } else {
            return null;
        }
    }


}
