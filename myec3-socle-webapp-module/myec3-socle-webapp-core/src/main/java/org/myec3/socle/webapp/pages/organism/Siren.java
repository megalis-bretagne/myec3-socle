package org.myec3.socle.webapp.pages.organism;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEntreprises;

public class Siren {

    @Inject
    @Service("organismService")
    private OrganismService organismService;

    @Inject
    private Messages messages;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String errorMessage;

    @Component(id = "siret_form")
    private Form siretForm;

    @Property
    private Organism organism;

    @InjectPage
    private Create createPage;

    private MpsWsClient mpsWsClient = new MpsWsClient();

    @OnEvent(EventConstants.ACTIVATE)
    public Object activation() {
        organism = new Organism();
        organism.setAddress(new Address());
        organism.getAddress().setCity("");
        organism.getAddress().setPostalAddress("");
        organism.getAddress().setPostalCode("");
        organism.getAddress().setCountry(Country.FR);
        return Boolean.TRUE;
    }

    @OnEvent(EventConstants.SUCCESS)
    public Object onSuccess() {
        this.createPage.setOrganism(this.organism);
        return this.createPage;
    }

    // Form events
    @OnEvent(value = EventConstants.VALIDATE, component = "siret_form")
    public boolean onValidate() {
        if (BooleanUtils.isFalse(this.organismService.isSiretValid(organism.getSiren(), organism.getNic()))) {
            this.siretForm.recordError(this.messages
                    .get("invalid-siret-error"));
            return false;
        }

        if (null != this.organismService.findBySiren(organism.getSiren())) {
            this.siretForm.recordError(this.messages.get(
                "organism-exists-error"));
            return false;
        }

        completeOrganismInfo();
        return true;
    }

    @OnEvent(EventConstants.CANCELED)
    public Object onFormCancel() {
        return Index.class;
    }

    /**
     * Get Information
     */
    private void completeOrganismInfo() {
        try {
            ResponseEntreprises infos = mpsWsClient.getInfoEntreprises(organism.getSiren());
            this.organism.setLabel(infos.getEntreprise().getLabel());
            Address address = infos.getEtablissement_siege().getAddress();

            // complete postalAddress with streetNumber/Street type and streetName
            address.setPostalAddress(address.getStreetNumber()+" "+address.getStreetType()+" "+address.getStreetName());
            this.organism.setAddress(address);
        } catch (Exception e) {
            this.errorMessage = this.messages.get("mps-error-message");
        }
    }
}