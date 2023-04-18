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

import org.apache.commons.lang3.BooleanUtils;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmAgent;
import org.myec3.socle.core.domain.sdm.model.SdmService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.ResponseMessage;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link AgentProfile}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * <p>
 * This class use a REST client to send the resource.
 *
 *x
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @see ResourcesSynchronizationJob<AgentProfile>
 * @see org.myec3.socle.ws.client.ResourceWsClient<AgentProfile>
 */
@Component
public class AgentSynchronizationJob extends
        ResourcesSynchronizationJob<AgentProfile> {

    private static final Logger logger = LoggerFactory.getLogger(AgentSynchronizationJob.class);

    private static final String SUFFIX_MONOTENANT = "@monotenant.megalis";

    @Value("#{'${myec3.synchro.applications.monotenant.ids}'.split(',')}")
    private List<Integer> idsApplicationMonotenant;

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
    public ResponseMessage create(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = sdmConverterService.convertToSdmAgent(resource);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            return sdmWsClient.post(resource, agentSDM, synchronizationSubscription);
        } else {
            if (isMonotenant(synchronizationSubscription.getApplication())) {
                resource.setAlfUserName(resource.getId() + SUFFIX_MONOTENANT);
            }
            return resourceWsClient.post(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage delete(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = sdmConverterService.convertToSdmAgent(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getUser().getId(), ResourceType.AGENT_PROFILE);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne != null) {
                agentSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                agentSDM.setActif("0");
                return sdmWsClient.delete(resource, valuedEmptyFieldForDelete(agentSDM), synchronizationSubscription);
            } else {
                logger.warn("AgentProfile.User id: {} n'a pas d'idAppliExterne (SDM) dans la table synchroIdentifiantExterneService", resource.getUser().getId());
                return new ResponseMessage(HttpStatus.BAD_REQUEST, new Error(ErrorCodeType.INTERNAL_CLIENT_ERROR, "Id Externe null",
                        "L'agent n'a pas d'idApplicationExterne (SDM)", resource.getId(), MethodType.DELETE));
            }
        } else {
            if (isMonotenant(synchronizationSubscription.getApplication())) {
                resource.setAlfUserName(resource.getId() + SUFFIX_MONOTENANT);
            }
            return resourceWsClient.delete(resource, synchronizationSubscription);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage update(AgentProfile resource,
                                  SynchronizationSubscription synchronizationSubscription,
                                  ResourceWsClient resourceWsClient) {
        if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(synchronizationSubscription.getApplication().getName())) {
            SdmAgent agentSDM = sdmConverterService.convertToSdmAgent(resource);
            SynchroIdentifiantExterne synchroIdentifiantExterne = synchroIdentifiantExterneService.findByIdSocle(resource.getUser().getId(), ResourceType.AGENT_PROFILE);
            SdmWsClientImpl sdmWsClient = (SdmWsClientImpl) resourceWsClient;
            if (synchroIdentifiantExterne != null) {
                agentSDM.setId(synchroIdentifiantExterne.getIdAppliExterne());
                return sdmWsClient.put(resource, agentSDM, synchronizationSubscription);
            } else {
                return sdmWsClient.post(resource, agentSDM, synchronizationSubscription);
            }

        } else {
            if (isMonotenant(synchronizationSubscription.getApplication())) {
                resource.setAlfUserName(resource.getId() + SUFFIX_MONOTENANT);
            }
            return resourceWsClient.put(resource, synchronizationSubscription);
        }
    }


    /**
     * Valued mandatory field for delete.
     * The values of these fields must be filled in to perform a delete on Sdm Web service.
     * Whatever the values
     *
     * @param sdmAgent Agent to delete
     * @return Agent with dumb value
     */
    private SdmAgent valuedEmptyFieldForDelete(SdmAgent sdmAgent) {
        if (sdmAgent.getIdProfil() == 0) {
            sdmAgent.setIdProfil(1);
        }

        return sdmAgent;
    }

    /**
     * Check if application is monotenant
     * @param application the application to check
     * @return  true if sucess
     */
    private boolean isMonotenant(Application application) {
        if (application == null || application.getId() == null) {
            return false;
        }
        return idsApplicationMonotenant.contains(application.getId().intValue());
    }
}
