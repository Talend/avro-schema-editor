package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new union schema.
 * 
 * @author timbault
 *
 */
public interface UnionStartContext extends SchemaContext {

	/**
	 * Create a new union schema. It returns an union definition context.
	 * 
	 * @return
	 */
	UnionContext union();
	
}
