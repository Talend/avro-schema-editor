package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents an map definition context.
 * 
 * @author timbault
 *
 */
public interface MapContext extends SchemaContext {

	/**
	 * This methods allows to define the type of the current map element.
	 * 
	 * @return
	 */
	TypeStartContext type();
	
}
