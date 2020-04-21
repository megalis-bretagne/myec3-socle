package org.myec3.socle.core.domain.sdm.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

public class SdmResource {

    @XmlElement(required = true)
    private String idExterne;

    public String getIdExterne() {
        return idExterne;
    }
    public void setIdExterne(String idExterne) {
        this.idExterne = Objects.toString(idExterne,"");
    }
}
