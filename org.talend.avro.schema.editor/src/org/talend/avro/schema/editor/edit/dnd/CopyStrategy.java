package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;

/**
 * 
 * @author timbault
 *
 */
public interface CopyStrategy {

	enum Kind {
		COPY, REF
	}
	
	boolean deepCopy(DnDContext dndContext);
	
	Kind getKindOfCopy(DnDContext dndContext, AvroNode nodeToCopy);
	
	boolean copyAttributes(DnDContext dndContext, AvroNode nodeToCopy, AvroNode nodeCopy);
	
	AttributeInitializer getAttributeInitializer(DnDContext dndContext, AvroNode nodeToCopy, AvroNode nodeCopy);
	
}
