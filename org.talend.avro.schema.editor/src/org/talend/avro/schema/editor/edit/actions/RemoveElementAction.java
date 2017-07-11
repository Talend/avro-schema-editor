package org.talend.avro.schema.editor.edit.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.EditUtils;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This action removes the contextual nodes of the its encapsulated context.
 * 
 * @author timbault
 *
 */
public class RemoveElementAction extends ContextualActionImpl {

	public static final String CMD_ID = "org.talend.avro.schema.editor.edit.RemoveElement"; //$NON-NLS-1$
	
	public RemoveElementAction(String text, int style, int notifications) {
		super(text, style, notifications);
	}
	
	public RemoveElementAction(String text, int notifications) {
		super(text, notifications);
	}

	@Override
	protected boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes) {
		AvroSchemaController controller = getController();
		List<AvroNode> nodes = EditUtils.cleanContextualNodesForRemove(contextualNodes);
		boolean canRemove = !nodes.isEmpty();
		for (AvroNode node : nodes) {			
			if (!controller.canRemoveElement(node)) {
				canRemove = false;
				break;
			}
		}
		return canRemove;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.REMOVE_ELEMENT);
	}

	@Override
	public String getToolTipText() {
		return "Remove selected element(s)";
	}

	@Override
	public void run() {
		List<AvroNode> contextualNodes = getContextualNodes();
		List<AvroNode> nodes = EditUtils.cleanContextualNodesForRemove(contextualNodes);
		IEditCommandFactory commandFactory = getCommandFactory();
		IEditCommand removeCommand = commandFactory.createRemoveElementsCommand(nodes, getNotifications());
		execute(removeCommand);
	}	

}
