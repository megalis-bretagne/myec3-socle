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

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmOrganisme;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage create(Organism resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = convertSdmOrganisme(resource);
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
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = convertSdmOrganisme(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByAcronyme(resource.getAcronym(), ResourceType.ORGANISM);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                //organismeSDM.setActif(false);
                organismeSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, organismeSDM, synchronizationSubscription);
            }else{
                logger.warn("Organism id: {} n'a pas d'idApplicationExterne (SDM) dans la table synchroIdentifiantExterneService",resource.getId());
                //todo return null à voir si ça fonctionne dans ce cas
                return null;
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
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmOrganisme organismeSDM = convertSdmOrganisme(resource);

            SynchroIdentifiantExterne synchroIdentifiantExterne=null;
            if (!StringUtils.isEmpty(resource.getAcronym())){
                synchroIdentifiantExterne = synchroIdentifiantExterneService.findByAcronyme(resource.getAcronym(), ResourceType.ORGANISM);
            }
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null){
                //organismeSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, organismeSDM, synchronizationSubscription);
            }else{
                return sdmWsClient.post(resource, organismeSDM, synchronizationSubscription);
            }
        }
        return resourceWsClient.put(resource, synchronizationSubscription);
    }


    private SdmOrganisme convertSdmOrganisme(Organism resource) {
        SdmOrganisme organismeSDM = new SdmOrganisme();
        organismeSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        organismeSDM.setId(0);
        organismeSDM.setAcronyme(resource.getAcronym());
        organismeSDM.setEmail(resource.getEmail());
        organismeSDM.setUrl(resource.getWebsite());
        organismeSDM.setTel(resource.getPhone());
        organismeSDM.setSigle(resource.getLabel());
        organismeSDM.setCategorieInsee(resource.getLegalCategory().getId());
        organismeSDM.setDenomination(resource.getLabel());
        organismeSDM.setSiren(resource.getSiren());
        organismeSDM.setNic(resource.getNic());
        organismeSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));

        return organismeSDM;
    }
}
