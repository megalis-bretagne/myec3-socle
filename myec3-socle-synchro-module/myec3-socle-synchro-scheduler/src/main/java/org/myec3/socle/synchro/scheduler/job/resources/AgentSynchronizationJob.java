/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.synchro.scheduler.job.resources;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link AgentProfile}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * <p>
 * This class use a REST client to send the resource.
 *
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @see ResourcesSynchronizationJob<AgentProfile>
 * @see org.myec3.socle.ws.client.ResourceWsClient<AgentProfile>
 */
@Component
public class AgentSynchronizationJob extends
        ResourcesSynchronizationJob<AgentProfile> {

    private static final Logger logger = LoggerFactory.getLogger(AgentSynchronizationJob.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage create(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = convertToSdmAgent(resource);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.post(resource, agentSDM, synchronizationSubscription);
        } else {
            if (synchronizationSubscription.getApplication().getId() == 7){
                resource.setAlfUserName(resource.getId() + "@monotenant.megalis");
            }
            return resourceWsClient.post(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage delete(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = convertToSdmAgent(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getUser().getId(), ResourceType.AGENT_PROFILE);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                agentSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                agentSDM.setActif("0");
                return sdmWsClient.put(resource, agentSDM, synchronizationSubscription);
            }else{
                logger.warn("AgentProfile.User id: {} n'a pas d'idAppliExterne (SDM) dans la table synchroIdentifiantExterneService",resource.getUser().getId());
                //todo return null à voir si ça fonctionne dans ce cas
                return null;
            }
        } else {
            if (synchronizationSubscription.getApplication().getId() == 7){
                resource.setAlfUserName(resource.getId() + "@monotenant.megalis");
            }
            return resourceWsClient.delete(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage update(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = convertToSdmAgent(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getUser().getId(), ResourceType.AGENT_PROFILE);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                agentSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, agentSDM, synchronizationSubscription);
            }else{
                return sdmWsClient.post(resource, agentSDM, synchronizationSubscription);
            }

        } else {
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }

    /**
     * Conversion d'un AgentProfile socle dans un Agent pour la SDM
     * @param resource
     * @return
     */
    private SdmAgent convertToSdmAgent(AgentProfile resource) {
        SdmAgent agentSDM = new SdmAgent();
        agentSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        agentSDM.setIdentifiant(resource.getUsername());

        //mapping du role
        for (Role role :resource.getRoles() ){
			if ("SDM".equals(role.getApplication().getName())){
				agentSDM.setIdProfil(role.getExternalId());
				break;
			}
		}
        agentSDM.setEmail(resource.getEmail());
        agentSDM.setNom(resource.getUser().getLastname());
        agentSDM.setPrenom(resource.getUser().getFirstname());

        int myInt = resource.isEnabled() ? 1 : 0;
        agentSDM.setActif(String.valueOf(myInt));

        if(!StringUtils.isEmpty(resource.getPhone())){
            agentSDM.setTelephone(resource.getPhone());
        }
        if(!StringUtils.isEmpty(resource.getCellPhone())){
            agentSDM.setTelephone(resource.getCellPhone());
        }
        agentSDM.setFax(resource.getFax());

        //On réalise une requete sur SynchroIdentifiantExterne pour récupérer l'acronyme retourné par la SDM
        SynchroIdentifiantExterne synchroIdentifiantExterneOrganisme = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganismDepartment().getOrganism().getId(), ResourceType.ORGANISM);
        agentSDM.setAcronymeOrganisme(synchroIdentifiantExterneOrganisme.getAcronyme());

        agentSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));


        SdmService serviceSDM = new SdmService();
        SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganismDepartment().getId(), ResourceType.ORGANISM_DEPARTMENT);
        if (synchroIdentifiantExterne !=null){
            serviceSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
        }else{
            logger.warn("Agent {} n'a pas de ORGANISM_DEPARTMENT SDM dans la table synchroIdentifiantExterneService pour l'idSocle ",resource.getId(),resource.getOrganismDepartment().getId());
        }
        agentSDM.setService(serviceSDM);

        return agentSDM;
    }

}
