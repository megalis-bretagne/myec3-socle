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
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneDeltaService;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import java.util.Date;

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
			SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.AGENT_PROFILE);
			agentSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
			agentSDM.setActif(false);
			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			return sdmWsClient.put(resource, agentSDM, synchronizationSubscription);
        } else {
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
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.AGENT_PROFILE);

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

    private SdmAgent convertToSdmAgent(AgentProfile resource) {
        SdmAgent agentSDM = new SdmAgent();
        agentSDM.setIdentifiant(resource.getUsername());
        //mapping du role
        for (Role role :resource.getRoles() ){
			if ("SDM".equals(role.getApplication().getName())){
				agentSDM.setIdProfil(role.getExternalId());
			}
		}
        agentSDM.setEmail(resource.getEmail());
        agentSDM.setNom(resource.getUser().getLastname());
        agentSDM.setPrenom(resource.getUser().getFirstname());
        agentSDM.setActif(resource.isEnabled());
        agentSDM.setTelephone(resource.getPhone());
        agentSDM.setFax(resource.getFax());
        agentSDM.setDateCreation(new Date());
        agentSDM.setDateModification(agentSDM.getDateCreation());
        agentSDM.setAcronymeOrganisme(resource.getOrganismDepartment().getOrganism().getAcronym());
        SdmService serviceSDM = new SdmService();
        SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getOrganismDepartment().getId(), ResourceType.ORGANISM_DEPARTMENT);
        if (synchroIdentifiantExterne !=null){
            serviceSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
        }

        agentSDM.setService(serviceSDM);
        return agentSDM;
    }

}
