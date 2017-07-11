package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Default styled cell label provider used for the schema viewer component.
 * 
 * @author timbault
 *
 */
public class SchemaViewerStyledCellLabelProvider extends StyledCellLabelProvider {

	private SchemaViewerLabelProvider labelProvider;
	
	private SchemaViewerNodeConverter nodeConverter;
	
	public SchemaViewerStyledCellLabelProvider(SchemaViewerLabelProvider labelProvider, SchemaViewerNodeConverter nodeConverter) {
		super();
		this.labelProvider = labelProvider;
		this.nodeConverter = nodeConverter;
	}
	
	@Override
    public String getToolTipText(Object element) {
        return labelProvider.getToolTipText(nodeConverter.convertToAvroNode(element));
    }
	
	@Override
    public void update(ViewerCell cell) {
		
        AvroNode node = nodeConverter.convertToAvroNode(cell.getElement());
        
		String text = labelProvider.getText(node);
        Image image = labelProvider.getImage(node);
        StyleRange[] styleRanges = labelProvider.getStyleRanges(node);

        cell.setText(text);
		cell.setImage(image);
        cell.setStyleRanges(styleRanges);

        Color backgroundColor = labelProvider.getBackgroundColor(node);
        if (backgroundColor != null) {
        	cell.setBackground(backgroundColor);
        }
        
        super.update(cell);
    }

	@Override
	public void dispose() {
		labelProvider.dispose();
		super.dispose();
	}
	
}
