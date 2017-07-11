package org.talend.avro.schema.editor.viewer.attribute.column;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

public class DefaultEditingPolicyImpl implements EditingPolicy {

	@Override
	public boolean isEditable(AvroNode node, String attributeName) {
		AvroAttribute<?> attribute = node.getAttributes().getAttribute(attributeName);
		if (attribute != null && attribute.isEnabled()) {
			return isAttributeEditable(node, attributeName);
		}
		return false;
	}
	
	protected boolean isAttributeEditable(AvroNode node, String attributeName) {
		NodeType type = node.getType();
		switch (attributeName) {
		case AvroAttributes.NAME:
			return type.isNamed();
		}
		return true;
	}
	
}
