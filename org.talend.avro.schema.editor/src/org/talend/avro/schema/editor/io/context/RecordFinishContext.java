package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a record finish context, i.e. a context which allows to finalize the current record.
 * 
 * @author timbault
 *
 */
public interface RecordFinishContext extends SchemaContext {

	/**
	 * Exit from the current record definition context.
	 * 
	 * @return
	 */
	SchemaContext endRecord();
	
}
