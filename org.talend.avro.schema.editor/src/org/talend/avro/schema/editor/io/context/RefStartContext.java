package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a reference.
 * 
 * @author timbault
 *
 */
public interface RefStartContext {

	/**
	 * 
	 * @param name
	 * @return
	 */
	RefFinishContext ref(String name);
	
}
