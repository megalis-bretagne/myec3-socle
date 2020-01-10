package org.myec3.socle.webapp.renderer;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.myec3.socle.webapp.encoder.MultipleValueEncoder;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MultipleSelectModelRenderer implements SelectModelVisitor {
    private final MarkupWriter _writer;
    private final MultipleValueEncoder _encoder;

    public MultipleSelectModelRenderer(MarkupWriter writer, MultipleValueEncoder encoder) {
        this._writer = writer;
        this._encoder = encoder;
    }

    public void beginOptionGroup(OptionGroupModel groupModel) {
        this._writer.element("optgroup", new Object[]{"label", groupModel.getLabel()});
        this.writeDisabled(groupModel.isDisabled());
        this.writeAttributes(groupModel.getAttributes());
    }

    public void endOptionGroup(OptionGroupModel groupModel) {
        this._writer.end();
    }

    public void option(OptionModel optionModel) {
        Object optionValue = optionModel.getValue();
        if (this._encoder == null) {
            throw new TapestryException("value encoder cannot be null", this, (Throwable)null);
        } else {
            String clientValue = this._encoder.toClient(optionValue);
            this._writer.element("option", new Object[]{"value", clientValue});
            if (this.isOptionSelected(optionModel)) {
                this._writer.attributes(new Object[]{"selected", "selected"});
            }

            this.writeDisabled(optionModel.isDisabled());
            this.writeAttributes(optionModel.getAttributes());
            this._writer.write(optionModel.getLabel());
            this._writer.end();
        }
    }

    private void writeDisabled(boolean disabled) {
        if (disabled) {
            this._writer.attributes(new Object[]{"disabled", "disabled"});
        }

    }

    private void writeAttributes(Map<String, String> attributes) {
        if (attributes != null) {
            Iterator i$ = attributes.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, String> e = (Entry)i$.next();
                this._writer.attributes(new Object[]{e.getKey(), e.getValue()});
            }

        }
    }

    protected boolean isOptionSelected(OptionModel optionModel) {
        return false;
    }
}
