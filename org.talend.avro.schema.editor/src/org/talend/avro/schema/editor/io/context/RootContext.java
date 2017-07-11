package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a root definition context. It allows to create record or union schema.
 * 
 * @author timbault
 *
 */
public interface RootContext extends RecordStartContext, UnionStartContext, EnumStartContext, FixedStartContext {

}
