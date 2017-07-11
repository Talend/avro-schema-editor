package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;

public class ChangeOptionalFieldAttributeCommand extends AbstractSchemaEditCommand {	
	
	private AvroNode node;
	
	private boolean optionalField;
	
	public ChangeOptionalFieldAttributeCommand(AvroContext context, AvroNode node,
			boolean optionalField, int notifications) {
		super(context, notifications);
		this.node = node;
		this.optionalField = optionalField;
	}
	
	@Override
	public void run() {
		getController().setOptional(node, optionalField);
		doNotifications(node);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		getController().setOptional(node, !optionalField);
		doNotifications(node);
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
		return optionalField ? "Set Optional Field" : "Unset Optional Field";
	}

	@Override
	public void dispose() {
		// 
	}
	
}
