package org.talend.avro.schema.editor.io.context;

import org.talend.avro.schema.editor.model.PrimitiveType;

/**
 * This interface represents a schema context which allows to create a new primitive type element.
 * 
 * @author timbault
 *
 */
public interface PrimitiveTypeStartContext extends SchemaContext {

	/**
	 * Create a new primitive type element.
	 * 
	 * @param primitiveType
	 * @return
	 */
	PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType);		
	
}
