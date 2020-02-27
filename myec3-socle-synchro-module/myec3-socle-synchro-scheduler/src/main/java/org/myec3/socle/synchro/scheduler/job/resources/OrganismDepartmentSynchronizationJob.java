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

import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.sdm.model.SdmAdresse;
import org.myec3.socle.core.domain.sdm.model.SdmOrganisme;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.impl.SdmWsClientImpl;
import org.springframework.stereotype.Component;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link OrganismDepartment}. This class implements abstract methods declared
 * into ResourcesSynchronizationJob.
 * 
 * This class use a REST client to send the resource.
 * 
 * @see ResourcesSynchronizationJob
 * @see org.myec3.socle.ws.client.ResourceWsClient
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@Component
public class OrganismDepartmentSynchronizationJob extends
		ResourcesSynchronizationJob<OrganismDepartment> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(OrganismDepartment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
			SdmOrganisme organismeSDM = new SdmOrganisme();


			organismeSDM.setId(0);
			organismeSDM.setAcronyme(resource.getAcronym());
			organismeSDM.setSigle(resource.getLabel());

			organismeSDM.setCategorieInsee(resource.getOrganism().getTenantIdentifier());
			organismeSDM.setDenomination(resource.getName());
			organismeSDM.setSiren(resource.getSiren());
			organismeSDM.setNic(resource.getOrganism().getNic());

			if (resource.getAddress() != null){
				SdmAdresse adresseSDM = new SdmAdresse ();
				adresseSDM.setCodePostal(resource.getAddress().getPostalCode());
				if (resource.getAddress().getCountry() !=null){
					adresseSDM.setPays(resource.getAddress().getCountry().getLabel());
				}
				adresseSDM.setRue(resource.getAddress().getStreetName());
				adresseSDM.setVille(resource.getAddress().getCity());
				adresseSDM.setAcronymePays(resource.getAddress().getInsee());

				organismeSDM.setAdresse(adresseSDM);
			}

			SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;

			return sdmWsClient.post(resource, organismeSDM, synchronizationSubscription);
		}

		return resourceWsClient.post(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(OrganismDepartment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(OrganismDepartment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.put(resource, synchronizationSubscription);
	}

}
