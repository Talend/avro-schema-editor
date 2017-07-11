package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.registry.NSNode;

public class RemoveNSNodeCommand extends AbstractSchemaEditCommand {

	private NSNode parentNode;
		
	private NSNode removedNode;
	
	private String name;
	
	public RemoveNSNodeCommand(AvroContext context, NSNode removedNode, int notifications) {
		super(context, notifications);
		this.removedNode = removedNode;
		this.parentNode = removedNode.getParent();
		this.name = removedNode.getName();
	}

	@Override
	public void run() {
		getContext().getSchemaRegistry().getNameSpaceRegistry().removeNameSpaceNode(removedNode);
		refresh();
	}

	protected void refresh() {
		getNotificationService().refresh();
//		// refresh only the schema registry view		
//		IView view = getNotificationService().getView(SchemaRegistryView.ID);
//		view.refresh();
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		removedNode = getContext().getSchemaRegistry().getNameSpaceRegistry().addNameSpaceNode(parentNode, name);
		refresh();
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void redo() {
		run();
	}

	@Override
	public String getLabel() {
		return "Add name space";
	}

	@Override
	public void dispose() {
		// 
	}


}
