package org.myec3.socle.synchro.core.service;

import org.apache.commons.lang3.BooleanUtils;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.*;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.springframework.util.StringUtils;

public interface SdmConverterService {


    SdmAgent convertToSdmAgent(AgentProfile resource);

    SdmEntreprise convertSdmEntreprise(Company resource);

    SdmInscrit convertToSdmInscrit(EmployeeProfile resource);

    SdmEtablissement convertSdmEtablissement(Establishment resource);

    SdmService convertToSdmService(OrganismDepartment resource);

    SdmOrganisme convertSdmOrganisme(Organism resource);

    SdmAdresse convertToSdmAdresse(Address resourceAddress);
}
