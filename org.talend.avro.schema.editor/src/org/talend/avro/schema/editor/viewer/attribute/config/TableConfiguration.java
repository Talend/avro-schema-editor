package org.talend.avro.schema.editor.viewer.attribute.config;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * This interface provides configuration data for a JFace table viewer. 
 * 
 * @author timbault
 *
 */
public interface TableConfiguration {	
	
	/**
	 * Returns the style of the table (see JFace table viewer doc to know which styles are available)
	 * 
	 * @return
	 */
	int getStyle();
	
	boolean isHeaderVisible();
	
	boolean areLinesVisible();
	
	CellLabelProvider getCellLabelProvider(String columnKey);
		
	ViewerComparator getComparator();
	
}
