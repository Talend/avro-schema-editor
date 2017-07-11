package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.NodeType;

public class ArrayOrMapValue extends MultiChoiceValue<NodeType> {

	public ArrayOrMapValue(NodeType initialType) {
		super(NodeType.class, initialType, NodeType.ARRAY_OR_MAP);
	}

	public void setArray() {
		setValue(NodeType.ARRAY);
	}
	
	public void setMap() {
		setValue(NodeType.MAP);
	}
	
	@Override
	public ArrayOrMapValue getACopy() {		
		return new ArrayOrMapValue(getValue());
	}
	
}
