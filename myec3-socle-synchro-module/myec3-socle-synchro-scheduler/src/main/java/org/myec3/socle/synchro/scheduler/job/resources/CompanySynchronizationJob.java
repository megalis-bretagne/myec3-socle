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

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmEntreprise;
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

import java.util.List;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link Company}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 *
 * This class use a REST client to send the resource.
 *
 * @see ResourcesSynchronizationJob<Company>
 * @see org.myec3.socle.ws.client.ResourceWsClient<Company>
 *
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 *
 */
@Component
public class CompanySynchronizationJob extends
		ResourcesSynchronizationJob<Company> {

	private static final Logger logger = LoggerFactory.getLogger(CompanySynchronizationJob.class);

	@Autowired
	@Qualifier("synchroIdentifiantExterneService")
	private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(Company resource,
								  SynchronizationSubscription synchronizationSubscription,
								  ResourceWsClient resourceWsClient) {
		if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
			SdmEntreprise entrepriseSDM = convertSdmEntreprise(resource);
			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			return sdmWsClient.post(resource, entrepriseSDM, synchronizationSubscription);
		}
		return resourceWsClient.post(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(Company resource,
								  SynchronizationSubscription synchronizationSubscription,
								  ResourceWsClient resourceWsClient) {
		if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {

			SdmEntreprise entrepriseSDM = convertSdmEntreprise(resource);
			List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getId(), ResourceType.COMPANY);

			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty()){
				entrepriseSDM.setId(synchroIdentifiantExterne.get(0).getIdAppliExterne());
				if (synchroIdentifiantExterne.size()>1){
					logger.warn("Company id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
					for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
						logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
					}
				}
				//todo pas de flag actif dans le model SDM comment faire pour désactiver une entreprise ??
				//entrepriseSDM.setActif(false);
				return sdmWsClient.put(resource, entrepriseSDM, synchronizationSubscription);
			}else{
				logger.warn("Company id: {} n'a pas d'idApplicationExterne (SDM) dans la table synchroIdentifiantExterneService",resource.getId());
				//todo return null à voir si ça fonctionne dans ce cas
				return null;
			}
		} else {
			return resourceWsClient.delete(resource, synchronizationSubscription);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(Company resource,
								  SynchronizationSubscription synchronizationSubscription,
								  ResourceWsClient resourceWsClient) {

		if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
			SdmEntreprise entrepriseSDM = convertSdmEntreprise(resource);
			List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getId(), ResourceType.COMPANY);

			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty()){
				entrepriseSDM.setId(synchroIdentifiantExterne.get(0).getIdAppliExterne());
				if (synchroIdentifiantExterne.size()>1){
					logger.warn("Company id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
					for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
						logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
					}
				}
				return sdmWsClient.put(resource, entrepriseSDM, synchronizationSubscription);
			}else{
				return sdmWsClient.post(resource, entrepriseSDM, synchronizationSubscription);
			}
		} else {
			return resourceWsClient.put(resource, synchronizationSubscription);
		}
	}


	private SdmEntreprise convertSdmEntreprise(Company resource) {
		SdmEntreprise entrepriseSDM = new SdmEntreprise();
		entrepriseSDM.setIdExterne(String.valueOf(resource.getExternalId()));
		entrepriseSDM.setSiren(resource.getSiren());
		entrepriseSDM.setFormeJuridique(resource.getLegalCategory().getLabel());
		entrepriseSDM.setCodeAPE(resource.getApeCode());
		entrepriseSDM.setEmail(resource.getEmail());
		entrepriseSDM.setRaisonSociale(resource.getName());
		//pas de mapping trouvé pour les deux champs ci-dessous
		entrepriseSDM.setCapitalSocial("");
		entrepriseSDM.setEffectif("");
		entrepriseSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));

		return entrepriseSDM;
	}
}
