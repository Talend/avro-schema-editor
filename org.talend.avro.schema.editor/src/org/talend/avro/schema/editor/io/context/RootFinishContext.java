package org.talend.avro.schema.editor.io.context;

import org.apache.avro.Schema;

/**
 * This interface represents a root finish context, i.e. a context which allows to finalize the root.
 * <p>
 * This context returns the full generated schema.
 * 
 * @author timbault
 *
 */
public interface RootFinishContext extends SchemaContext {

	/**
	 * Exit from the current root definition context and returns the full generated schema.
	 * 
	 * @return
	 */
	Schema endRoot();
	
}
