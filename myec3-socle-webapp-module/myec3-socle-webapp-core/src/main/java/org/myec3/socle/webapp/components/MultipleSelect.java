package org.myec3.socle.webapp.components;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.EnumSelectModel;
import org.myec3.socle.webapp.encoder.MultipleValueEncoder;
import org.myec3.socle.webapp.renderer.MultipleSelectModelRenderer;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MultipleSelect extends AbstractField {
    @Parameter
    private MultipleValueEncoder encoder;
    @Parameter(
            required = true
    )
    private SelectModel model;
    @Parameter(
            defaultPrefix = "prop",
            value = "true"
    )
    private boolean multiple;
    @Parameter(
            defaultPrefix = "validate"
    )
    private FieldValidator<Object> validate;
    @Parameter(
            required = true,
            principal = true
    )
    private List value;
    @Inject
    private Locale locale;
    @Inject
    private Request request;
    @Inject
    private ComponentResources resources;
    @Environmental
    private ValidationTracker tracker;
    @Inject
    private ComponentDefaultProvider defaultProvider;

    public MultipleSelect() {
    }

    protected void processSubmission(String elementName) {
        String[] primaryKeys = this.request.getParameters(elementName);
        List selectedValues = primaryKeys != null ? this.encoder.toValue(primaryKeys) : CollectionFactory.newList();

        try {
            Iterator i$ = selectedValues.iterator();

            while(i$.hasNext()) {
                Object selectedValue = i$.next();
                this.validate.validate(selectedValue);
            }

            if (this.validate.isRequired() && selectedValues.size() == 0) {
                throw new ValidationException("at least one selection is required");
            }

            this.value = selectedValues;
        } catch (ValidationException var6) {
            this.tracker.recordError(this, var6.getMessage());
        }

    }

    void afterRender(MarkupWriter writer) {
        writer.end();
    }

    void beginRender(MarkupWriter writer) {
        Element element = writer.element("select", new Object[]{"name", this.getControlName(), "id", this.getClientId()});
        if (this.multiple) {
            element.attribute("multiple", "multiple");
        }

        this.resources.renderInformalParameters(writer);
    }

    ValueEncoder defaultEncoder() {
        return this.defaultProvider.defaultValueEncoder("value", this.resources);
    }

    SelectModel defaultModel() {
        Class valueType = this.resources.getBoundType("value");
        if (valueType == null) {
            return null;
        } else {
            return Enum.class.isAssignableFrom(valueType) ? new EnumSelectModel(valueType, this.resources.getContainerMessages()) : null;
        }
    }

    Binding defaultValidate() {
        return this.defaultProvider.defaultValidatorBinding("value", this.resources);
    }

    Binding defaultValue() {
        return this.defaultProvider.defaultBinding("value", this.resources);
    }

    @BeforeRenderTemplate
    void options(MarkupWriter writer) {
        SelectModelVisitor renderer = new MultipleSelect.Renderer(writer);
        if (this.model == null) {
            throw new TapestryException("select model cannot be null", this, (Throwable)null);
        } else {
            this.model.visit(renderer);
        }
    }

    public void setModel(SelectModel model) {
        this.model = model;
    }

    public void setValue(List value) {
        this.value = value;
    }

    public void setValueEncoder(MultipleValueEncoder encoder) {
        this.encoder = encoder;
    }

    private class Renderer extends MultipleSelectModelRenderer {
        public Renderer(MarkupWriter writer) {
            super(writer, MultipleSelect.this.encoder);
        }

        protected boolean isOptionSelected(OptionModel optionModel) {
            boolean hit = false;
            Object testValue = optionModel.getValue();
            if (MultipleSelect.this.value != null) {
                Iterator i$ = MultipleSelect.this.value.iterator();

                while(i$.hasNext()) {
                    Object singleValue = i$.next();
                    hit = testValue == singleValue || testValue != null && testValue.equals(singleValue);
                    if (hit) {
                        break;
                    }
                }
            }

            return hit;
        }
    }
}
