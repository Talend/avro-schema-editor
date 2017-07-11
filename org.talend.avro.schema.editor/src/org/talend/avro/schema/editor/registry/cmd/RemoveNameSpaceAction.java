package org.talend.avro.schema.editor.registry.cmd;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.registry.NSNode;

public class RemoveNameSpaceAction extends Action implements NameSpaceAction {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.view.removeNameSpace"; //$NON-NLS-1$
	
	private AvroContext context;
		
	private NSNode nsNode;
	
	public RemoveNameSpaceAction() {
		super(CMD_ID);
	}
	
	public void setContext(AvroContext context) {
		this.context = context;
		setEnabled(context != null && nsNode != null);
	}

	public void setNSNode(NSNode nsNode) {
		this.nsNode = nsNode;
		setEnabled(context != null && nsNode != null && nsNode != context.getSchemaRegistry().getNameSpaceTree());
	}

	@Override
	public String getId() {
		return CMD_ID;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.REMOVE_ELEMENT);
	}

	@Override
	public String getToolTipText() {
		return "Remove Name Space";
	}

	@Override
	public void run() {
		if (context == null || nsNode == null) {
			throw new IllegalStateException("Cannot remove name space without valid context and valid name space target node");
		}
		IEditCommandFactory commandFactory = context.getService(IEditCommandFactory.class);
		IEditCommand cmd = commandFactory.createRemoveNameSpaceCommand(nsNode, Notifications.NOT_REF);		
		context.getService(ICommandExecutor.class).execute(cmd);
	}

}
