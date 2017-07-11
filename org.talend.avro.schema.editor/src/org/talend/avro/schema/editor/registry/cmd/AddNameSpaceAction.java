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
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;
import org.talend.avro.schema.editor.registry.view.AddNameSpaceHandler;

public class AddNameSpaceAction extends Action implements NameSpaceAction {

	private AvroContext context;
		
	private NSNode nsNode;
	
	public AddNameSpaceAction() {
		super(AddNameSpaceHandler.CMD_ID);
	}
	
	public void setContext(AvroContext context) {
		this.context = context;
		setEnabled(context != null && nsNode != null);
	}

	public void setNSNode(NSNode nsNode) {
		this.nsNode = nsNode;
		setEnabled(context != null && nsNode != null);
	}

	@Override
	public String getId() {
		return AddNameSpaceHandler.CMD_ID;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.ADD_ELEMENT);
	}

	@Override
	public String getToolTipText() {
		return "Add Name Space";
	}

	@Override
	public void run() {
		if (context == null || nsNode == null) {
			throw new IllegalStateException("Cannot add name space without valid context and valid name space target node");
		}
		NameSpaceRegistry nameSpaceRegistry = context.getSchemaRegistry().getNameSpaceRegistry();
		String name = nameSpaceRegistry.getAvailableNameSpace(nsNode);
		IEditCommandFactory commandFactory = context.getService(IEditCommandFactory.class);
		IEditCommand cmd = commandFactory.createAddNameSpaceCommand(nsNode, name, Notifications.NOT_REF);		
		context.getService(ICommandExecutor.class).execute(cmd);
	}
	
}
