package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;

public class RenameNameSpaceCommand extends AbstractChangeNameSpaceCommand {

	private NSNode nsNode;
		
	private String newName;
	
	public RenameNameSpaceCommand(AvroContext context, NSNode nsNode, String newName, int notifications) {
		super("Rename name space", context, notifications);
		this.nsNode = nsNode;
		this.newName = newName;
	}

	@Override
	protected String getNewNameSpace(NSNode changedNode, NSNode childNode) {
		NameSpaceRegistry nameSpaceRegistry = getContext().getSchemaRegistry().getNameSpaceRegistry();
		return nameSpaceRegistry.getNewNameSpace(nsNode, newName, childNode);
	}

	@Override
	protected void postBuildCommands() {
		if (isEmpty()) {
			// at least rename the NSNode! Otherwise the node is not renamed at all!
			RenameNSNodeCommand cmd = new RenameNSNodeCommand(getInternalContext(), nsNode, newName, Notifications.REFRESH);
			addCommand(cmd);
		} else {
			// remove the previous NSNode. It must be deleted, we have rename it!
			RemoveNSNodeCommand cmd = new RemoveNSNodeCommand(getInternalContext(), nsNode, Notifications.REFRESH);
			addCommand(cmd);
		}
	}

	@Override
	protected NSNode getImpactedNode() {
		return nsNode;
	}
		
}
