package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public class SetEnabledAttributeCommand extends AbstractSchemaEditCommand {

	private AvroAttribute<?> attribute;
	
	private boolean enableStatus;
	
	public SetEnabledAttributeCommand(AvroContext context,
			AvroAttribute<?> attribute, boolean enableStatus, int notifications) {
		super(context, notifications);
		this.attribute = attribute;
		this.enableStatus = enableStatus;
	}

	@Override
	public void run() {
		attribute.setEnabled(enableStatus);
		doNotifications(attribute.getHolder());
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		attribute.setEnabled(!enableStatus);
		doNotifications(attribute.getHolder());
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
		return "Change enable status";
	}

	@Override
	public void dispose() {
		
	}

}
