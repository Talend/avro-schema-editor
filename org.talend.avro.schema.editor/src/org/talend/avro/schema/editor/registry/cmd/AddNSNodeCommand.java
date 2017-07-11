package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.registry.NSNode;

public class AddNSNodeCommand extends AbstractSchemaEditCommand {

	private NSNode parentNode;
		
	private NSNode addedNode;
	
	private String name;
	
	public AddNSNodeCommand(AvroContext context, NSNode parentNode, String name, int notifications) {
		super(context, notifications);
		this.parentNode = parentNode;
		this.name = name;		
	}

	@Override
	public void run() {
		addedNode = getContext().getSchemaRegistry().getNameSpaceRegistry().addNameSpaceNode(parentNode, name);
		refresh();
	}

	protected void refresh() {		
		getNotificationService().refresh();
//		 refresh only the schema registry view
//		IView view = getNotificationService().getView(SchemaRegistryView.ID);
//		view.refresh();
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		getContext().getSchemaRegistry().getNameSpaceRegistry().removeNameSpaceNode(addedNode);
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
