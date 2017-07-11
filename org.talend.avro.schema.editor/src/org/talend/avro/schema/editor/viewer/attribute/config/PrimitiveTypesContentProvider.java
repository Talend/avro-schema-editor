package org.talend.avro.schema.editor.viewer.attribute.config;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class PrimitiveTypesContentProvider implements MultiChoiceValueContentProvider<PrimitiveType> {

	@Override
	public PrimitiveType[] getContent(AvroAttribute<MultiChoiceValue<PrimitiveType>> attribute) {
		
		AvroNode node = attribute.getHolder();
		NodeType type = node.getType();
		
		if (type == NodeType.PRIMITIVE_TYPE) {
			
			// case of a primitive type node under an union node
			UnionNode unionNode = (UnionNode) node.getParent();
			PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(node);
			List<PrimitiveType> freePrimitiveTypes = ModelUtil.getFreePrimitiveTypes(unionNode, false, primitiveType);
			return freePrimitiveTypes.toArray(new PrimitiveType[freePrimitiveTypes.size()]);					
			
		} else {
			
			// Field, Map or Array cases
			// We just filter Null type
			return PrimitiveType.valuesWithoutNull();
			
		}
		
	}	
	
}
