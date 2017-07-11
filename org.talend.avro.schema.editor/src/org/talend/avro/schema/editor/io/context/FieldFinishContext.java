package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a field finish context, i.e. a context which allows to finalize the definition of a record field.
 * <p>
 * It allows to specify the default value of the current field.
 * 
 * @author timbault
 *
 */
public interface FieldFinishContext extends SchemaContext {

	SchemaContext noDefault();
	
	SchemaContext setDefaultValue(Object defaultValue);
	
}
