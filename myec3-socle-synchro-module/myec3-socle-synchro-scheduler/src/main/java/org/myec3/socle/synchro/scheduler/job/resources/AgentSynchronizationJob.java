/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.synchro.scheduler.job.resources;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import java.util.Date;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link AgentProfile}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * 
 * This class use a REST client to send the resource.
 * 
 * @see ResourcesSynchronizationJob<T>
 * @see org.myec3.socle.ws.client.ResourceWsClient<T>
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@Component
public class AgentSynchronizationJob extends
		ResourcesSynchronizationJob<AgentProfile> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(AgentProfile resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		//todo ajout atexo
		//convertion object
		if ("SDM".equals(synchronizationSubscription.getApplication().getName())){
			SdmAgent agentSDM = new SdmAgent();

			agentSDM.setIdentifiant(resource.getUsername());
			//todo voir ce qu'il faut mettre dans rôle pour la salle des marchés
			agentSDM.setIdProfil(2);
/*
			if (resource.getRoles() !=null && resource.getRoles().size() >0){
				agentSDM.setIdProfil(resource.getRoles().get(0).getExternalId());
			}
*/
			agentSDM.setEmail(resource.getEmail());
			agentSDM.setNom(resource.getUser().getLastname());
			agentSDM.setPrenom(resource.getUser().getFirstname());
			agentSDM.setActif(resource.isEnabled());
			agentSDM.setTelephone(resource.getPhone());
			agentSDM.setFax(resource.getFax());
			agentSDM.setDateCreation(new Date());
			agentSDM.setDateModification(agentSDM.getDateCreation());
			agentSDM.setAcronymeOrganisme(resource.getOrganismDepartment().getOrganism().getAcronym());
			SdmService serviceSDM= new SdmService();
			serviceSDM.setIdExterne(resource.getOrganismDepartment().getExternalId());
			agentSDM.setService(serviceSDM);

			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;

			return sdmWsClient.post(resource,agentSDM, synchronizationSubscription);

		}else {
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

		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(AgentProfile resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		//todo ajout atexo
		//convertion object
		if ("SDM".equals(synchronizationSubscription.getApplication().getName())){
			SdmAgent agentSDM = new SdmAgent();

			agentSDM.setIdentifiant(resource.getUsername());
			//todo voir ce qu'il faut mettre dans rôle pour la salle des marchés
			for (Role role : resource.getRoles()){
				if ("SDM".equals(role.getApplication().getName())){
					agentSDM.setIdProfil(role.getExternalId());
				}
			}


/*
			if (resource.getRoles() !=null && resource.getRoles().size() >0){
				agentSDM.setIdProfil(resource.getRoles().get(0).getExternalId());
			}
*/
			agentSDM.setEmail(resource.getEmail());
			agentSDM.setNom(resource.getUser().getLastname());
			agentSDM.setPrenom(resource.getUser().getFirstname());
			agentSDM.setActif(resource.isEnabled());
			agentSDM.setTelephone(resource.getPhone());
			agentSDM.setFax(resource.getFax());
			agentSDM.setDateCreation(new Date());
			agentSDM.setDateModification(agentSDM.getDateCreation());
			agentSDM.setAcronymeOrganisme(resource.getOrganismDepartment().getOrganism().getAcronym());

			SdmService serviceSDM= new SdmService();
			serviceSDM.setIdExterne(resource.getOrganismDepartment().getExternalId());
			agentSDM.setService(serviceSDM);

			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;

			return sdmWsClient.post(resource,agentSDM, synchronizationSubscription);

		}else {
			return resourceWsClient.put(resource, synchronizationSubscription);
		}

	}

}
