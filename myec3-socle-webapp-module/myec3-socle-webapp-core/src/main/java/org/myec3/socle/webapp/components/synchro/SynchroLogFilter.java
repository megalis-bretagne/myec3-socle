package org.myec3.socle.webapp.components.synchro;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Component to display form filter for SynchroLog
 * Use in organism/synchro for example
 */
public class SynchroLogFilter extends AbstractPage {

    @Component(id="filter_form")
    private Form form;

    @Inject
    private ComponentResources componentResources;

    @Getter
    @Setter
    private String searchStatut;

    @Getter
    @Setter
    private String searchApplication;

    @Getter
    @Setter
    private String searchIdentifier;

    @Getter
    @Setter
    private String searchResourceType;

    /**
     * List of log to apply filter
     */
    @Parameter(required = true)
    @Property
    private List<SynchronizationLogDTO> toFilter;

    @Parameter(required = true)
    @Property
    private Map<ResourceType,String> resourceTypeModel;

    /**
     * List filter
     */
    @Getter
    private List<SynchronizationLogDTO> synchroLogMatching;


    /**
     * Event action to filter
     */
    @OnEvent(value = EventConstants.VALIDATE, component = "filter_form")
    public void onValidate() {
        this.synchroLogMatching = new ArrayList<>();

        this.synchroLogMatching = this.toFilter.stream()
                .filter(logDTO -> filterOnStatut(logDTO)
                        && filterOnIdentifier(logDTO)
                        && filterOnApplication(logDTO)
                        && filterOnResource(logDTO))
                .collect(Collectors.toList());

        componentResources.triggerEvent("logFilterDone", null, null);
    }

    /**
     * Encoder for select resourceInput
     * @return GenericListEncoder
     */
    public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
        return new GenericListEncoder<>(new ArrayList<>(resourceTypeModel.keySet()));
    }

    public List<String> getApplicationModel() {
       return toFilter.stream().map(logDTO -> logDTO.getSynchronizationLog().getApplicationName())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Apply Filter on resourceType
     * @param logDTO {@link SynchronizationLogDTO} to match
     * @return true if match
     */
    private boolean filterOnResource(SynchronizationLogDTO logDTO) {
        if (StringUtils.isEmpty(searchResourceType)) {
            return true;
        }
        try {
            ResourceType typeToFind = ResourceType.valueOf(searchResourceType);
            return typeToFind.equals(logDTO.getSynchronizationLog().getResourceType());
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * Apply Filter on Application
     * @param logDTO {@link SynchronizationLogDTO} to match
     * @return true if match
     */
    private boolean filterOnApplication(SynchronizationLogDTO logDTO) {
        if (StringUtils.isEmpty(searchApplication)) {
            return true;
        }
        return searchApplication.equals(logDTO.getSynchronizationLog().getApplicationName());
    }

    /**
     * Apply Filter on Statut
     * @param logDTO {@link SynchronizationLogDTO} to match
     * @return true if match
     */
    private boolean filterOnStatut(SynchronizationLogDTO logDTO) {
        if (StringUtils.isEmpty(searchStatut)) {
            return true;
        }
        return logDTO.getSynchronizationLog().getStatut().equals(searchStatut);
    }

    /**
     * Apply filter on Idenfier
     * @param logDTO    {@link SynchronizationLogDTO} to match
     * @return  true if match
     */
    private boolean filterOnIdentifier(SynchronizationLogDTO logDTO) {
        if (StringUtils.isEmpty(searchIdentifier)) {
            return true;
        }
        return logDTO.getStructureEmail().contains(searchIdentifier)
                || logDTO.getUsername().contains(searchIdentifier);
    }
}
