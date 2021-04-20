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

import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmInscrit;
import org.myec3.socle.core.domain.sdm.model.SdmResource;
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
 * {@link EmployeeProfile}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 *
 * This class use a REST client to send the resource.
 *
 * @see ResourcesSynchronizationJob<EmployeeProfile>
 * @see org.myec3.socle.ws.client.ResourceWsClient<EmployeeProfile>
 *
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 *
 */
@Component
public class EmployeeSynchronizationJob extends
        ResourcesSynchronizationJob<EmployeeProfile> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSynchronizationJob.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage create(EmployeeProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmInscrit inscritSDM = convertToSdmInscrit(resource);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.post(resource, inscritSDM, synchronizationSubscription);
        } else {
            return resourceWsClient.post(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage delete(EmployeeProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmResource sdmResourceToDelete = new SdmResource();
            sdmResourceToDelete.setIdExterne(resource.getExternalId().toString());
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;

            return sdmWsClient.delete(resource, sdmResourceToDelete, synchronizationSubscription);
        } else {
            return resourceWsClient.delete(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage update(EmployeeProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {

        if ("SDM".equals(synchronizationSubscription.getApplication().getName())) {
            SdmInscrit inscritSDM = convertToSdmInscrit(resource);
            List<SynchroIdentifiantExterne> synchroIdentifiantExterne = synchroIdentifiantExterneService.findListByIdSocle(resource.getUser().getId(), ResourceType.EMPLOYEE_PROFILE);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne !=null && !synchroIdentifiantExterne.isEmpty()){
                inscritSDM.setId(synchroIdentifiantExterne.get(0).getIdAppliExterne());
                if (synchroIdentifiantExterne.size()>1){
                    logger.warn("Employe id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
                    for(SynchroIdentifiantExterne s:synchroIdentifiantExterne){
                        logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
                    }
                }
                return sdmWsClient.put(resource, inscritSDM, synchronizationSubscription);
            }else{
                return sdmWsClient.post(resource, inscritSDM, synchronizationSubscription);
            }
        } else {
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }

    /**
     * Conversion d'un EmployeeProfile socle dans un Inscrit  pour la SDM
     * @param resource
     * @return
     */
    private SdmInscrit convertToSdmInscrit(EmployeeProfile resource) {

        SdmInscrit inscritSDM = new SdmInscrit();
        inscritSDM.setIdExterne(String.valueOf(resource.getExternalId()));

        //mapping du role
        if (resource.getRoles() != null && !resource.getRoles().isEmpty() ){
            for (Role role :resource.getRoles() ){
                if ("SDM".equals(role.getApplication().getName())){
                    inscritSDM.setProfil(role.getExternalId());
                    break;
                }
            }
        }

        inscritSDM.setLogin(resource.getUsername());
        inscritSDM.setEmail(resource.getEmail());
        inscritSDM.setNom(resource.getUser().getLastname());
        inscritSDM.setPrenom(resource.getUser().getFirstname());
        int myInt = resource.isEnabled() ? 1 : 0;
        inscritSDM.setActif(String.valueOf(myInt));
        inscritSDM.setTelephone(resource.getPhone());
        inscritSDM.setMotDePasse(resource.getUser().getPassword());
        inscritSDM.setTypeHash("sha256");
        inscritSDM.setAdresse(convertToSdmAdresse(resource.getAddress()));
        //inscritSDM.setInscritAnnuaireDefense();

        if(resource.getEstablishment()!=null){
            inscritSDM.setSiret(resource.getEstablishment().getSiret());
            List<SynchroIdentifiantExterne> synchro = synchroIdentifiantExterneService.findListByIdSocle(resource.getEstablishment().getId(), ResourceType.ESTABLISHMENT);
            if (synchro!= null && !synchro.isEmpty() ){
                inscritSDM.setIdEtablissement(synchro.get(0).getIdAppliExterne());
                if (synchro.size()>1){
                    logger.warn("Establishment id: {} a plusieurs IdAppliExterne en bdd ",resource.getId());
                    for(SynchroIdentifiantExterne s:synchro){
                        logger.warn(" idSocle {} IdAppliExterne {} ",s.getIdSocle(),s.getIdAppliExterne());
                    }
                }

            }else{
                logger.warn("EmployeeProfile id: {} n'a pas de ESTABLISHMENT SDM dans la table synchroIdentifiantExterneService pour l'idSocle ",resource.getId(),resource.getEstablishment().getId());
            }
        }else{
            logger.warn("EmployeeProfile id: {} n'a pas de resource.getEstablishment().getId() il est donc impossible de rechercher dans la table synchroIdentifiantExterneService",resource.getId());
        }
        return inscritSDM;
    }


}
