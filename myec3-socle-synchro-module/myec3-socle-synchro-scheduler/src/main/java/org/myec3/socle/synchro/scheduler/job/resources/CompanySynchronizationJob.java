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
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.*;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

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
			SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.COMPANY);
			entrepriseSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			return sdmWsClient.put(resource, entrepriseSDM, synchronizationSubscription);

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
			SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.COMPANY);
			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
			if (synchroIdentifiantExterne !=null){
				entrepriseSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
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

		entrepriseSDM.setSiren(resource.getSiren());
		//entrepriseSDM.setEffectif(resource.get);
		entrepriseSDM.setFormeJuridique(resource.getApeCode());
		entrepriseSDM.setCodeAPE(resource.getApeCode());
		entrepriseSDM.setDateModification(new Date());
		entrepriseSDM.setEmail(resource.getEmail());
		//entrepriseSDM.setCapitalSocial();

		entrepriseSDM.setRaisonSociale(resource.getLegalCategory().toString());

/*		for (Establishment establishment :resource.getEstablishments()){

			SdmEtablissement etablissementSDM = new SdmEtablissement();
			//etablissementSDM.setId("");
			etablissementSDM.setSiege("");
			if (resource.getAddress() != null){
				SdmAdresse adresseSDM = new SdmAdresse ();
				adresseSDM.setCodePostal(resource.getAddress().getPostalCode());
				if (resource.getAddress().getCountry() !=null){
					adresseSDM.setPays(resource.getAddress().getCountry().getLabel());
				}
				adresseSDM.setRue(resource.getAddress().getStreetName());
				adresseSDM.setVille(resource.getAddress().getCity());
				adresseSDM.setAcronymePays(resource.getAddress().getInsee());

				etablissementSDM.setAdresse(adresseSDM);
			}
			etablissementSDM.setDateCreation(new Date());
			etablissementSDM.setDateModification(etablissementSDM.getDateCreation());

		}*/

		if (resource.getAddress() != null){
			SdmAdresse adresseSDM = new SdmAdresse ();
			adresseSDM.setCodePostal(resource.getAddress().getPostalCode());
			if (resource.getAddress().getCountry() !=null){
				adresseSDM.setPays(resource.getAddress().getCountry().getLabel());
			}
			adresseSDM.setRue(resource.getAddress().getStreetName());
			adresseSDM.setVille(resource.getAddress().getCity());
			adresseSDM.setAcronymePays(resource.getAddress().getInsee());

			entrepriseSDM.setAdresse(adresseSDM);
		}
		return entrepriseSDM;
	}
}
