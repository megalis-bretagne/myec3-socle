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

import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmAdresse;
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmEtablissement;
import org.myec3.socle.core.domain.sdm.model.SdmService;
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
import java.util.Objects;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link Establishment}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 *
 * This class use a REST client to send the resource.
 *
 * @see ResourcesSynchronizationJob<Establishment>
 * @see org.myec3.socle.ws.client.ResourceWsClient<Establishment>
 *
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 *
 */
@Component
public class EstablishmentSynchronizationJob extends
        ResourcesSynchronizationJob<Establishment> {

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage create(Establishment resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmEtablissement etablissementSDM = convertSdmEtablissement(resource);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.post(resource, etablissementSDM, synchronizationSubscription);
        }
        return resourceWsClient.post(resource, synchronizationSubscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage delete(Establishment resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmEtablissement etablissementSDM = convertSdmEtablissement(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
            etablissementSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
            //etablissementSDM.setActif(false);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.put(resource, etablissementSDM, synchronizationSubscription);
        } else {
            return resourceWsClient.delete(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage update(Establishment resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmEtablissement etablissementSDM = convertSdmEtablissement(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                etablissementSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, etablissementSDM, synchronizationSubscription);
            }else{
                return sdmWsClient.post(resource, etablissementSDM, synchronizationSubscription);
            }
        } else {
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }

    private SdmEtablissement convertSdmEtablissement(Establishment resource) {
        SdmEtablissement etablissementSDM = new SdmEtablissement();

        etablissementSDM.setSiege("");
        etablissementSDM.setSiret(resource.getSiret());
        if (resource.getCompany() !=null && resource.getCompany().getId() != null){
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getCompany().getId() , ResourceType.COMPANY);
            if (synchroIdentifiantExterne !=null){
                etablissementSDM.setIdEntreprise(String.valueOf(synchroIdentifiantExterne.getIdAppliExterne()));
            }
        }

        if (resource.getAddress() != null) {
            SdmAdresse adresseSDM = new SdmAdresse();
            adresseSDM.setCodePostal(Objects.toString(resource.getAddress().getPostalCode(),""));
            if (resource.getAddress().getCountry() != null) {
                adresseSDM.setPays(Objects.toString(resource.getAddress().getCountry().getLabel(),""));
            }
            adresseSDM.setRue(Objects.toString(resource.getAddress().getPostalAddress(),""));
            adresseSDM.setVille(Objects.toString(resource.getAddress().getCity(),""));
            if (resource.getAddress().getCountry() !=null){
                adresseSDM.setAcronymePays(Objects.toString(resource.getAddress().getCountry().name(),""));
            }
            etablissementSDM.setAdresse(adresseSDM);
        }

        etablissementSDM.setDateCreation(new Date());
        etablissementSDM.setDateModification(etablissementSDM.getDateCreation());
        return etablissementSDM;
    }
}
