package org.talend.avro.schema.editor.edit.actions;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.EditUtils;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

/**
 * 
 * @author timbault
 *
 */
public class AddElementAction extends ContextualActionImpl {

	public static final String CMD_ID = "org.talend.avro.schema.editor.edit.AddElement"; //$NON-NLS-1$

	public AddElementAction(String text, int style, int notifications) {
		super(text, style, notifications);
	}

	public AddElementAction(String text, int notifications) {
		super(text, notifications);
	}

	@Override
	protected boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes) {
		AvroNode targetNode = null;
		if (contextualNodes.isEmpty()) {
			targetNode = context.getInputNode();
		} else if (contextualNodes.size() == 1) {
			targetNode = contextualNodes.get(0);			
		}
		if (targetNode != null) {
			NodeType[] types = EditUtils.getAddableNodeTypes(targetNode, getController());			
			return types.length > 0;
		}
		return false;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.ADD_ELEMENT);
	}

	@Override
	public String getToolTipText() {
		return "Add new element";
	}

	@Override
	public void run() {
		
		IEditCommandFactory commandFactory = getCommandFactory();
		
		List<AvroNode> contextualNodes = getContextualNodes();	
		
		AvroNode targetNode = null;
		if (contextualNodes.isEmpty()) {
			targetNode = getContext().getInputNode();
		} else if (contextualNodes.size() == 1) {
			targetNode = contextualNodes.get(0);
		}
		
		if (targetNode != null) {
						
			IEditCommand cmd = null;
			
			NodeType[] availableTypes = EditUtils.getAddableNodeTypes(targetNode, getController());
			
			if (availableTypes.length == 1) {
				cmd = commandFactory.createAddElementCommand(targetNode, availableTypes[0], getNotifications());
			} else if (availableTypes.length > 1) {
				openPopupMenu(availableTypes, targetNode, getContext());
			}					
			
			if (cmd != null) {
				execute(cmd);
			}
			
		}
	}	
	
	protected void openPopupMenu(NodeType[] availableTypes, final AvroNode targetNode, final AvroContext context) {
		
		Shell shell = Display.getCurrent().getActiveShell();
		final Menu menu = new Menu(shell, SWT.POP_UP);
		for (NodeType availableType : availableTypes) {
			final NodeType type = availableType;
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(type.getDisplayLabel());
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					menu.dispose();
					IEditCommand cmd = context.getService(IEditCommandFactory.class)
							.createAddElementCommand(targetNode, type, getNotifications());
					context.getService(ICommandExecutor.class).execute(cmd);
				}
			});
		}
        
		Point location = MouseInfo.getPointerInfo().getLocation();
		int x = location.x;
		int y = location.y;
        menu.setLocation(x, y);
        menu.setVisible(true);
        
	}
	
}
