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
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmInscrit;
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
            SdmInscrit inscritSDM = convertToSdmInscrit(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.EMPLOYEE_PROFILE);
            inscritSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
            inscritSDM.setActif(false);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.put(resource, inscritSDM, synchronizationSubscription);
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
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.EMPLOYEE_PROFILE);
            inscritSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.put(resource, inscritSDM, synchronizationSubscription);
        } else {
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }


    private SdmInscrit convertToSdmInscrit(EmployeeProfile resource) {
        SdmInscrit inscritSDM = new SdmInscrit();
        inscritSDM.setLogin(resource.getUsername());
        inscritSDM.setEmail(resource.getEmail());
        inscritSDM.setNom(resource.getUser().getLastname());
        inscritSDM.setPrenom(resource.getUser().getFirstname());
        inscritSDM.setActif(resource.isEnabled());
        inscritSDM.setTelephone(resource.getPhone());
        inscritSDM.setMotdePasse(resource.getUser().getPassword());
        inscritSDM.setTypeHash("sha256");
        SynchroIdentifiantExterne synchro = synchroIdentifiantExterneService.findByIdSocle(inscritSDM.getIdEtablissement(), ResourceType.ESTABLISHMENT);
        inscritSDM.setIdEtablissement(synchro.getIdSocle());
        inscritSDM.setSiret(resource.getEstablishment().getSiret());
        //inscritSDM.setInscritAnnuaireDefense();
        return inscritSDM;
    }


}
