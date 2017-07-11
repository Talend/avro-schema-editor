package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents an union finish context, i.e. a context which allows to finalize the current union.
 * <p>
 * 
 * @author timbault
 *
 */
public interface UnionFinishContext {

	/**
	 * Exit from the current union definition context.
	 * 
	 * @return
	 */
	SchemaContext endUnion();
	
}
