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

import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmOrganisme;
import org.myec3.socle.core.sync.api.*;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SdmConverterService;
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
 * {@link Organism}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 *
 * This class use a REST client to send the resource.
 *
 * @see ResourcesSynchronizationJob<Organism>
 * @see org.myec3.socle.ws.client.ResourceWsClient<Organism>
 *
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 *
 */
@Component
public class OrganismSynchronizationJob extends
        ResourcesSynchronizationJob<Organism> {

    private static final Logger logger = LoggerFactory.getLogger(OrganismSynchronizationJob.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    @Autowired
    @Qualifier("sdmConverterService")
    private SdmConverterService sdmConverterService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage create(Organism resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = sdmConverterService.convertSdmOrganisme(resource);
            //on met l'acronyme à vide car la salle des marchés va nous retourner l'acronyme
            organismeSDM.setAcronyme("");
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.post(resource, organismeSDM, synchronizationSubscription);
        }
        return resourceWsClient.post(resource, synchronizationSubscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage delete(Organism resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = sdmConverterService.convertSdmOrganisme(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByAcronyme(resource.getAcronym(), ResourceType.ORGANISM);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                //organismeSDM.setActif(false);
                organismeSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, organismeSDM, synchronizationSubscription);
            } else {
                logger.warn("Organism id: {} n'a pas d'idApplicationExterne (SDM) dans la table synchroIdentifiantExterneService",resource.getId());
                return new ResponseMessage(HttpStatus.BAD_REQUEST, new Error(ErrorCodeType.INTERNAL_CLIENT_ERROR, "Id Externe null",
                        "Organism n'a pas d'idApplicationExterne (SDM)", resource.getId(), MethodType.DELETE));
            }
        }
        return resourceWsClient.delete(resource, synchronizationSubscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage update(Organism resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = sdmConverterService.convertSdmOrganisme(resource);

            SynchroIdentifiantExterne synchroIdentifiantExterne=null;
            if (!StringUtils.isEmpty(resource.getAcronym())){
                synchroIdentifiantExterne = synchroIdentifiantExterneService.findByAcronyme(resource.getAcronym(), ResourceType.ORGANISM);
            }
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                //organismeSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, organismeSDM, synchronizationSubscription);
            }else{
                organismeSDM.setAcronyme("");
                return sdmWsClient.post(resource, organismeSDM, synchronizationSubscription);
            }
        }
        return resourceWsClient.put(resource, synchronizationSubscription);
    }


}
