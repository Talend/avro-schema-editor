package org.talend.avro.schema.editor.edit.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.CopyContext;
import org.talend.avro.schema.editor.context.CopyContextListener;
import org.talend.avro.schema.editor.edit.EditUtils;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class PasteElementAction extends ContextualActionImpl {

	public static final String CMD_ID = "org.talend.avro.schema.editor.edit.paste"; //$NON-NLS-1$
	
	private CopyContextListener copyContextListener;
	
	public PasteElementAction(String text, int style, int notifications) {
		super(text, style, notifications);
	}

	public PasteElementAction(String text, int notifications) {
		super(text, notifications);
	}
	
	@Override
	public void init(AvroContext context, boolean link) {
		super.init(context, link);
		if (link && context != null) {
			copyContextListener = new CopyContextListener() {				
				@Override
				public void onCopyContextUpdated(CopyContext context) {
					updateEnableState(getContext());
				}
			};
			context.getCopyContext().addListener(copyContextListener);
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.PASTE);
	}

	@Override
	public String getToolTipText() {
		return "Paste copied element(s) on current selected element";
	}
	
	protected AvroNode getTargetNode(AvroContext context, List<AvroNode> contextualNodes) {
		AvroNode targetNode = null;
		if (contextualNodes.isEmpty()) {
			// get the input node
			targetNode = context.getInputNode();
		} else if (contextualNodes.size() == 1) {
			targetNode = contextualNodes.get(0);
		}
		return targetNode;
	}

	@Override
	protected boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes) {		
		AvroNode targetNode = getTargetNode(context, contextualNodes);
		List<AvroNode> nodesToCopy = EditUtils.cleanContextualNodesForCopy(context.getCopyContext().getNodesToCopy());
		boolean canPaste = !nodesToCopy.isEmpty();
		if (targetNode != null && canPaste) {						
			for (AvroNode node : nodesToCopy) {
				if (!getController().canDnDElement(DragAndDropPolicy.Action.COPY, node, targetNode, TargetPosition.UPON)) {
					canPaste = false;
					break;
				}
			}
		}
		return canPaste;
	}

	@Override
	public void run() {
		AvroNode targetNode = getTargetNode(getContext(), getContextualNodes());
		List<AvroNode> nodesToCopy = getContext().getCopyContext().getNodesToCopy();
		if (targetNode != null && !nodesToCopy.isEmpty()) {
			List<AvroNode> nodes = EditUtils.cleanContextualNodesForCopy(nodesToCopy);
			IEditCommandFactory commandFactory = getCommandFactory();
			SchemaEditCompositeCommand mainCopyCmd = commandFactory.createCompositeCommand("Copy element(s)", getNotifications());
			for (AvroNode node : nodes) {	
				IEditCommand copyCmd = commandFactory.createDnDElementCommand(
						DragAndDropPolicy.Action.COPY, node, targetNode, TargetPosition.UPON, getNotifications());
				mainCopyCmd.addCommand(copyCmd);
			}
			execute(mainCopyCmd);			
		}
	}

	@Override
	protected void clean() {
		if (copyContextListener != null) {
			getContext().getCopyContext().removeListener(copyContextListener);
			copyContextListener = null;
		}
		super.clean();
	}
	
}
