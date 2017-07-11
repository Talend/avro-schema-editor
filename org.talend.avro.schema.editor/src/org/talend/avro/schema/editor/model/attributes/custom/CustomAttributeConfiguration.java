package org.talend.avro.schema.editor.model.attributes.custom;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;

/**
 * This provides 
 * 
 * @author timbault
 *
 */
public interface CustomAttributeConfiguration {
	
	boolean isCustomAttribute(AvroContext context, NodeType type, String attributeName);
		
	CustomAttributeIO<?> getCustomAttributeIO(AvroContext context, NodeType type, String attributeName);
	
	AvroAttribute<Object>[] configureAttributes(AvroContext context, AvroNode node, AvroAttributeSet attributes);	
		
}
