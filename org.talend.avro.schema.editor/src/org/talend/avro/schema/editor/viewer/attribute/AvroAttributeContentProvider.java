package org.talend.avro.schema.editor.viewer.attribute;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

public class AvroAttributeContentProvider implements AttributeContentProvider {

	@Override
	public AvroAttribute<?>[] getAttributes(Object inputElement) {
		if (inputElement instanceof AvroNode) {
			AvroNode node = (AvroNode) inputElement;
			AvroAttribute<?> refPathAttribute = null;
			if (node.getType().isRef()) {
				refPathAttribute = node.getAttributes().getAttribute(AvroAttributes.PATH);
				node = ((RefNode) node).getReferencedNode();
			}
			AvroAttributeSet attributes = node.getAttributes();
			List<AvroAttribute<?>> sortedAttributes = attributes.getSortedAttributes();
			AvroAttribute<?> referencedPathAttr = attributes.getAttribute(AvroAttributes.PATH);
			if (referencedPathAttr != null && refPathAttribute != null) {
				AttributeUtil.replaceAttribute(sortedAttributes, referencedPathAttr, refPathAttribute);
			}
			return sortedAttributes.toArray(new AvroAttribute<?>[sortedAttributes.size()]);
		}
		return new AvroAttribute<?>[0];
	}

}
