package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new map element.
 * 
 * @author timbault
 *
 */
public interface MapStartContext extends SchemaContext {
	
	/**
	 * Create a new map element.
	 * 
	 * @return
	 */
	MapContext map();
	
}
