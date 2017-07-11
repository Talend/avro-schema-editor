package org.talend.avro.schema.editor.edit.dnd;

import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.SchemaViewerDropPolicy;

public class AvroSchemaViewerDropPolicy implements SchemaViewerDropPolicy {

	private AvroContext context;
	
	public AvroSchemaViewerDropPolicy(AvroContext context) {
		super();
		this.context = context;
	}
	
	@Override
	public int getTargetPositionTolerance() {
		return 3;
	}

	@Override
	public int dragOver(AvroNode draggedNode, AvroNode targetNode, TargetPosition position) {
		
		int detail = DND.DROP_NONE;
		
		AvroSchemaController schemaController = context.getService(AvroSchemaController.class);				

		if (schemaController.canDnDElement(DragAndDropPolicy.Action.MOVE, draggedNode, targetNode, position)) {
			detail = DND.DROP_MOVE;
		} else if (schemaController.canDnDElement(DragAndDropPolicy.Action.COPY, draggedNode, targetNode, position))  {
			detail = DND.DROP_COPY;
		} else if (schemaController.canDnDElement(DragAndDropPolicy.Action.REFERENCE, draggedNode, targetNode, position))  {
			detail = DND.DROP_MOVE;
		} 
		
		return detail;
	}

	@Override
	public void drop(AvroNode draggedNode, AvroNode targetNode, TargetPosition position) {

		AvroSchemaController schemaController = context.getService(AvroSchemaController.class);

		IEditCommandFactory commandFactory = context.getService(IEditCommandFactory.class);

		List<DragAndDropPolicy.Action> availableActions = new ArrayList<>();
		for (DragAndDropPolicy.Action action : DragAndDropPolicy.Action.values()) {
			if (schemaController.canDnDElement(action, draggedNode, targetNode, position)) {
				availableActions.add(action);
			}
		}

		if (availableActions.size() == 1) {
			// only one action available, just do it
			IEditCommand command = commandFactory.createDnDElementCommand(availableActions.get(0), draggedNode, targetNode, position, Notifications.NOT_REF);
			context.getService(ICommandExecutor.class).execute(command);
		} else if (availableActions.size() > 1) {
			openPopupMenu(availableActions, draggedNode, targetNode, position);
		}		
		
	}
	
	protected void openPopupMenu(List<DragAndDropPolicy.Action> availableActions,
			final AvroNode sourceNode, final AvroNode targetNode, final TargetPosition position) {
		
		Shell shell = Display.getCurrent().getActiveShell();
		final Menu menu = new Menu(shell, SWT.POP_UP);
		
		for (DragAndDropPolicy.Action availableAction : availableActions) {
			final DragAndDropPolicy.Action action = availableAction;
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(action.getLabel());
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					menu.dispose();
					IEditCommand cmd = context.getService(IEditCommandFactory.class)
							.createDnDElementCommand(action, sourceNode, targetNode, position, Notifications.NOT_REF);					
					if (cmd != null) {	
						context.getService(ICommandExecutor.class).execute(cmd);
					}
				}
			});
		}
        
		java.awt.Point location = MouseInfo.getPointerInfo().getLocation();
		int x = location.x;
		int y = location.y;
        menu.setLocation(x, y);
        menu.setVisible(true);
        
	}

}
