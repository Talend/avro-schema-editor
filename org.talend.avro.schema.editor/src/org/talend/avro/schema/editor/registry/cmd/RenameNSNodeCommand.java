package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.registry.NSNode;

public class RenameNSNodeCommand extends AbstractSchemaEditCommand {

	private NSNode node;
	
	private String newName;
	
	private String oldName;
	
	public RenameNSNodeCommand(AvroContext context, NSNode node, String newName, int notifications) {
		super(context, notifications);
		this.node = node;
		this.newName = newName;
		this.oldName = node.getName();
	}

	@Override
	public void run() {
		node.setName(newName);
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
		node.setName(oldName);
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
		return "Rename NS node";
	}

	@Override
	public void dispose() {
		// 
	}
	
}
