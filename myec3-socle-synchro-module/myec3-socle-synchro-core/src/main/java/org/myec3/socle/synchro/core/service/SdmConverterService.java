package org.myec3.socle.synchro.core.service;

import org.apache.commons.lang3.BooleanUtils;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.*;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.springframework.util.StringUtils;

public interface SdmConverterService {


    /**
     * Convert AgentProfile into SdmAgent
     *
     * @param resource  resource
     * @return SdmAgent
     */
    SdmAgent convertToSdmAgent(AgentProfile resource);

    /**
     * Convert Company into SdmEntreprise
     *
     * @param resource  resource
     * @return SdmEntreprise
     */
    SdmEntreprise convertSdmEntreprise(Company resource);

    /**
     * Convert EmployeeProfile into SdmInscrit
     *
     * @param resource  resource
     * @return SdmInscrit
     */
    SdmInscrit convertToSdmInscrit(EmployeeProfile resource);

    /**
     * Convert Establishment into SdmEtablissement
     *
     * @param resource  resource
     * @return SdmEtablissement
     */
    SdmEtablissement convertSdmEtablissement(Establishment resource);

    /**
     * Convert OrganismDepartment into SdmService
     *
     * @param resource  resource
     * @return SdmService
     */
    SdmService convertToSdmService(OrganismDepartment resource);

    /**
     * Convert Organism into SdmOrganisme
     *
     * @param resource  resource
     * @return SdmOrganisme
     */
    SdmOrganisme convertSdmOrganisme(Organism resource);


    /**
     * Convert Address into SdmAdresse
     *
     * @param resource  resource
     * @return SdmAdresse
     */
    SdmAdresse convertToSdmAdresse(Address resource);
}
