package org.talend.avro.schema.editor.viewer;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Label provider used by the schema viewer component.
 * 
 * @author timbault
 *
 */
public interface SchemaViewerLabelProvider {
	
	String getText(AvroNode node);

    String getToolTipText(AvroNode node);

    Image getImage(AvroNode node);

    StyleRange[] getStyleRanges(AvroNode node);
	
    /**
     * Returns the cell background color. Returns null to use the default background color.
     * 
     * @param node
     * @return
     */
    Color getBackgroundColor(AvroNode node);
    
    /**
     * Dispose the resources
     */
    void dispose();
    
}
