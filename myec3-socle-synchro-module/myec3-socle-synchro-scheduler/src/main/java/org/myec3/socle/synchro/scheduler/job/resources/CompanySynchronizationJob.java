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
import org.myec3.socle.core.domain.sdm.model.SdmAdresse;
import org.myec3.socle.core.domain.sdm.model.SdmEntreprise;
import org.myec3.socle.core.domain.sdm.model.SdmEtablissement;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link Company}. This class implements abstract methods declared into
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
public class CompanySynchronizationJob extends
		ResourcesSynchronizationJob<Company> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(Company resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {


		if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
			SdmEntreprise entrepriseSDM = new SdmEntreprise();
			//zéro car creation
			entrepriseSDM.setId(0);
			entrepriseSDM.setSiren(resource.getSiren());
			//entrepriseSDM.setEffectif(resource.get);
			entrepriseSDM.setFormeJuridique(resource.getApeCode());
			entrepriseSDM.setCodeAPE(resource.getApeCode());
			entrepriseSDM.setDateModification(new Date());
			//entrepriseSDM.setCapitalSocial();

			//entrepriseSDM.setRaisonSociale(resource.getStructureType().getValue());

			for (Establishment establishment :resource.getEstablishments()){

				SdmEtablissement etablissementSDM = new SdmEtablissement();
				etablissementSDM.setId("");
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

			}


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
		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(Company resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.put(resource, synchronizationSubscription);
	}
}
