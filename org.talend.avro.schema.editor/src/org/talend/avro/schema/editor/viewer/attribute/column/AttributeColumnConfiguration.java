package org.talend.avro.schema.editor.viewer.attribute.column;

import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;

/**
 * This interface provides all the needed parameters to configure the schema viewer component with "columns" layout. 
 * 
 * @author timbault
 *
 */
public interface AttributeColumnConfiguration {

	/**
	 * Returns the names of the attributes which will be displayed as columns (one column per attribute).
	 * 
	 * @return
	 */
	String[] getColumnAttributeNames();
	
	/**
	 * Indicates if the given attribute is the main one.
	 * 
	 * @param attributeName
	 * @return
	 */
	boolean isMainColumn(String attributeName);
	
	/**
	 * Returns the title of the column associated to the given attribute.
	 * 
	 * @param attributeName
	 * @return
	 */
	String getColumnTitle(String attributeName);
	
	/**
	 * Returns the width of the column associated to the given attribute.
	 * 
	 * @param attributeName
	 * @return
	 */
	int getColumnWidth(String attributeName);
	
	int getColumnStyle(String attributeName);
	
	/**
	 * Indicates if the column associated to the given attribute is resizable.
	 * 
	 * @param attributeName
	 * @return
	 */
	boolean isResizable(String attributeName);
	
	/**
	 * Returns the label provider for the column associated to the given attribute.
	 * 
	 * @param attributeName
	 * @return
	 */
	SchemaViewerLabelProvider getColumnLabelProvider(String attributeName);
	
	/**
	 * Returns the editing support for the column associated to the given attribute. It could be null (no edition).
	 * 
	 * @param attributeName
	 * @return
	 */
	SchemaViewerColumnEditingSupport getColumnEditingSupport(String attributeName);
	
}
