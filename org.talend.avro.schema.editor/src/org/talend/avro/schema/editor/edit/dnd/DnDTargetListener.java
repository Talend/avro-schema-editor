package org.talend.avro.schema.editor.edit.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.SchemaViewerDropPolicy;
import org.talend.avro.schema.editor.viewer.SchemaViewerNodeConverter;

/**
 * Base implementation of a DropTargetListener for a {@link SchemaViewer}. 
 * 
 * @author timbault
 *
 */
public class DnDTargetListener implements DropTargetListener {
		
	private SchemaViewer schemaViewer;
	
	private SchemaViewerNodeConverter nodeConverter;
	
	private TargetPosition position = null;
	
	private SchemaViewerDropPolicy dropPolicy;
		
	public DnDTargetListener(SchemaViewer schemaViewer, SchemaViewerNodeConverter nodeConverter, SchemaViewerDropPolicy dropPolicy) {
		super();
		this.schemaViewer = schemaViewer;
		this.nodeConverter = nodeConverter;
		this.dropPolicy = dropPolicy;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		// nothing to do
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		// nothing to do
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		// nothing to do
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
        
		IStructuredSelection structuredSelection = (IStructuredSelection) LocalSelectionTransfer
                .getTransfer().getSelection();
		
		if (structuredSelection.size() == 1) {
			
			Object element = structuredSelection.getFirstElement();
			
			if (element instanceof AvroNode) {

				AvroNode sourceNode = (AvroNode) element;

				TreeItem item = (TreeItem) event.item;
				if (item != null) {					
					
					AvroNode targetNode = nodeConverter.convertToAvroNode(item.getData());
					
					if (targetNode == sourceNode) {
						
						event.detail = DND.DROP_NONE;
						
					} else {
					
						int tolerance = dropPolicy.getTargetPositionTolerance();
						
						Point pt = schemaViewer.getTreeViewer().getTree().toControl(event.x, event.y);
						Rectangle bounds = item.getBounds();

						if (pt.y < bounds.y + tolerance) {
							position = TargetPosition.BEFORE;
							event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
						}
						else if (pt.y > bounds.y + bounds.height - tolerance) {
							position = TargetPosition.AFTER;
							event.feedback |= DND.FEEDBACK_INSERT_AFTER;
						}
						else {
							position = TargetPosition.UPON;
							event.feedback |= DND.FEEDBACK_SELECT;
						}

						event.detail = dropPolicy.dragOver(sourceNode, targetNode, position);											
						
					}
				}
				
			} else {
				event.detail = DND.DROP_NONE;
			}
            
		}

	}

	@Override
	public void drop(DropTargetEvent event) {
		
		IStructuredSelection structuredSelection = (IStructuredSelection) LocalSelectionTransfer
                .getTransfer().getSelection();
		
		if (structuredSelection.size() == 1) {

			AvroNode sourceNode = (AvroNode) structuredSelection.getFirstElement();
			TreeItem item = (TreeItem) event.item;
			if (item != null) {
				AvroNode targetNode = nodeConverter.convertToAvroNode(item.getData());
				dropPolicy.drop(sourceNode, targetNode, position);
			}
			
		}
		
	}	
	
	@Override
	public void dropAccept(DropTargetEvent event) {
		// nothing to do
	}

}
