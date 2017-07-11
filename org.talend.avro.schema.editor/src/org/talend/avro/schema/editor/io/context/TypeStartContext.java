package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a type definition context. 
 * <p>
 * It allows to specify the type on the current schema element (which could be a field, a map, an array, etc).
 * <p>
 * From this context it is possible to create an union, a record, an enumeration, a fixed, an array, a map, a primitive type or a reference.
 * 
 * @author timbault
 *
 */
public interface TypeStartContext extends UnionStartContext, 
										RecordStartContext,
										EnumStartContext,
										FixedStartContext,
										PrimitiveTypeStartContext, 
										MapStartContext,
										ArrayStartContext,
										RefStartContext {

}
