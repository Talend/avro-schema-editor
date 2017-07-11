package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.PrimitiveType;

/**
 * Implementation of an {@link AvroAttribute} with a {@link PrimitiveTypes} value.
 * 
 * @author timbault
 *
 */
public class PrimitiveTypeAttribute extends AvroAttributeImpl<PrimitiveTypes> {

	public PrimitiveTypeAttribute(AvroNode node) {
		this(node, null);
	}
	
	public PrimitiveTypeAttribute(AvroNode node, PrimitiveTypes value) {
		super(node, AvroAttributes.PRIMITIVE_TYPE, AvroAttributes.PRIMITIVE_TYPE_CLASS, value);
	}	

	public void setType(PrimitiveType type) {
		super.getValue().setValue(type);
	}
	
	@Override
	public PrimitiveTypes getValue() {
		return (PrimitiveTypes) super.getValue().getACopy();
	}

	@Override
	public void setValue(PrimitiveTypes value) {
		super.getValue().apply(value);
	}

	@Override
	public PrimitiveTypes getCopyOfValue() {
		return getValue().getACopy();
	}	
	
}
