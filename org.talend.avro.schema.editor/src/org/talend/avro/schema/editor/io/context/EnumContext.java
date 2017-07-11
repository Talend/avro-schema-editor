package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents an enumeration definition context.
 * 
 * @author timbault
 *
 */
public interface EnumContext extends SchemaContext {

	/**
	 * Set the name space of the current enumeration.
	 * 
	 * @param namespace
	 * @return
	 */
	EnumContext namespace(String namespace);
	
	/**
	 * Set the documentation of the current enumeration.
	 * 
	 * @param doc
	 * @return
	 */
	EnumContext doc(String doc);
	
	/**
	 * Set the aliases of the current enumeration.
	 * 
	 * @param aliases
	 * @return
	 */
	EnumContext aliases(String... aliases);
	
	/**
	 * Set the values (symbols in the avro nomenclature) of the current enumeration.
	 * 
	 * @param symbols
	 * @return
	 */
	EnumFinishContext symbols(String... symbols);
	
}
