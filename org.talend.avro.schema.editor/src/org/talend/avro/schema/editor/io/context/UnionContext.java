package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents an union definition context.
 * 
 * @author timbault
 *
 */
public interface UnionContext extends RecordStartContext,
										EnumStartContext,
										FixedStartContext,
										PrimitiveTypeStartContext, 
										MapStartContext,
										ArrayStartContext,
										RefStartContext {

}
