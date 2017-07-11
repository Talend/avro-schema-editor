package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class ChangePrimitiveTypeAttributeCommand extends AbstractSchemaEditCommand {

	private AvroNode node;
	
	private PrimitiveType newType;
	
	private PrimitiveType oldType;
	
	public ChangePrimitiveTypeAttributeCommand(AvroContext context, AvroNode node, PrimitiveType type, int notifications) {
		super(context, notifications);
		this.node = node;
		this.newType = type;
		this.oldType = AttributeUtil.getPrimitiveType(node);
	}

	@Override
	public void run() {
		getController().setPrimitiveType(node, newType);
		doNotifications(node);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		getController().setPrimitiveType(node, oldType);
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
		return "Set " + newType;
	}

	@Override
	public void dispose() {
		//
	}

}
