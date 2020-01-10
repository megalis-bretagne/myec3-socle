package org.myec3.socle.webapp.mixins;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Simple adaptation of
 * https://github.com/ioko-tapestry-commons/ioko-tapestry-commons/blob/master/tapestry-commons/tapestry-mixins/src/main/java/uk/co/ioko/tapestry/mixins/mixins/BoundCheckbox.java
 */
@Import(library = {"boundcheckbox.js"})
public class BoundCheckBox {

    @InjectContainer
    private ClientElement container;

    @Parameter
    private Checkbox master;

    @Environmental
    private JavaScriptSupport renderSupport;

    @AfterRender
    void after(MarkupWriter writer) {
        String masterClientId = master == null ? "" : master.getClientId();
        JSONObject spec = new JSONObject();
        spec.put("clientId", container.getClientId());
        spec.put("masterId", masterClientId);
        renderSupport.addInitializerCall("boundCheckboxLoad", spec);
    }

}
