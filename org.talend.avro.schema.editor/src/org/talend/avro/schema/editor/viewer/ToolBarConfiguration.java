package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.action.ToolBarManager;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Provides all the needed configuration parameters for the {@link SchemaViewer} toolbars.
 * <p>
 *  
 * @author timbault
 * @see SchemaViewer
 *
 */
public interface ToolBarConfiguration {

	/**
	 * Returns the top toolbar identifiers. If empty no toolbar will be displayed at the top of the schema viewer. 
	 * 
	 * @return
	 */
	String[] getTopToolBarIds();
	
	/**
	 * Returns the bottom toolbar identifiers. If empty no toolbar will be displayed at the bottom of the schema viewer.
	 * 
	 * @return
	 */
	String[] getBottomToolBarIds();
	
	/**
	 * Fill the specified toolbar with actions and/or contribution items.
	 * 
	 * @param manager
	 * @param toolBarId
	 */
	void fillToolBar(ToolBarManager manager, String toolBarId);
	
	/**
	 * Returns the style of the specified toolbar.
	 * 
	 * @param toolBarId
	 * @return
	 */
	int getToolBarStyle(String toolBarId);
	
	/**
	 * Return true in order to display a label title at the left of the specified toolbar. 
	 *  
	 * @param toolBarId
	 * @return
	 */
	boolean hasTitle(String toolBarId);
	
	/**
	 * Return the title to be displayed at the left of the specified toolbar.
	 * 
	 * @param toolBarId
	 * @param node
	 * @return
	 */
	String getTitle(String toolBarId, AvroNode node);
	
}
