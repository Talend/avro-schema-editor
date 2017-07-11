package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;

public class RemoveNameSpaceCommand extends AbstractChangeNameSpaceCommand {

	private NSNode removedNode;
	
	public RemoveNameSpaceCommand(AvroContext context, NSNode removedNode, int notifications) {
		super("Remove name space", context, notifications);
		this.removedNode = removedNode;
	}

	@Override
	protected String getNewNameSpace(NSNode changedNode, NSNode childNode) {
		NameSpaceRegistry nameSpaceRegistry = getContext().getSchemaRegistry().getNameSpaceRegistry();
		return nameSpaceRegistry.getNewNameSpace(removedNode, childNode);
	}

	@Override
	protected void postBuildCommands() {
		// finally remove the node
		RemoveNSNodeCommand cmd = new RemoveNSNodeCommand(getInternalContext(), removedNode, Notifications.REFRESH);
		addCommand(cmd);
	}

	@Override
	protected NSNode getImpactedNode() {
		return removedNode;
	}

}
