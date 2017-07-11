package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.talend.avro.schema.editor.model.attributes.PathAttribute;

public class PathAttributeControl extends StringAttributeControl {

	@Override
	protected String getAttributeValueAsString() {
		return getAttribute().getValue(getContext());
	}

	@Override
	protected PathAttribute getAttribute() {
		return (PathAttribute) super.getAttribute();
	}

}
