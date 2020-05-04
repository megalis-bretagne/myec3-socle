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
import org.myec3.socle.core.domain.sdm.model.SdmEtablissement;
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

    private static final Logger logger = LoggerFactory.getLogger(EstablishmentSynchronizationJob.class);

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
            List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty()){
                etablissementSDM.setId(synchroIdentifiantExterne.get(0).getIdAppliExterne());
                if (synchroIdentifiantExterne.size()>1){
                    logger.warn("Establishment id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
                    for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
                        logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
                    }
                }
                return sdmWsClient.put(resource, etablissementSDM, synchronizationSubscription);
            }else{
                logger.warn("Establishment id: {} n'a pas d'idApplicationExterne (SDM) dans la table synchroIdentifiantExterneService",resource.getId());
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
    public ResponseMessage update(Establishment resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmEtablissement etablissementSDM = convertSdmEtablissement(resource);
            List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty()){
                etablissementSDM.setId(synchroIdentifiantExterne.get(0).getIdAppliExterne());
                if (synchroIdentifiantExterne.size()>1){
                    logger.warn("Establishment id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
                    for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
                        logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
                    }
                }
                return sdmWsClient.put(resource, etablissementSDM, synchronizationSubscription);
            }else{
                return sdmWsClient.post(resource, etablissementSDM, synchronizationSubscription);
            }
        } else {
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }

    /**
     * Conversion d'un Establishment socle dans un Etablissement pour la SDM
     * @param resource
     * @return
     */
    private SdmEtablissement convertSdmEtablissement(Establishment resource) {

        SdmEtablissement etablissementSDM = new SdmEtablissement();
        etablissementSDM.setIdExterne(String.valueOf(resource.getExternalId()));
        int siege = resource.getIsHeadOffice() ? 1 : 0;
        etablissementSDM.setSiege(String.valueOf(siege));
        etablissementSDM.setSiret(resource.getSiret());

        if (resource.getCompany() !=null && resource.getCompany().getId() != null){
            List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getCompany().getId(), ResourceType.COMPANY);
            if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty() ){
                etablissementSDM.setIdEntreprise(String.valueOf(synchroIdentifiantExterne.get(0).getIdAppliExterne()));

                if (synchroIdentifiantExterne.size()>1){
                    logger.warn("Company id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
                    for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
                        logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
                    }
                }
            }else{
                logger.warn("Establishment id: {} n'a pas de COMPANY SDM dans la table synchroIdentifiantExterneService pour l'idSocle ",resource.getId(),resource.getCompany().getId());
            }
        }
        etablissementSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));
        return etablissementSDM;
    }

}
