package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;

public class SwitchArrayMapAttributeCommand extends AbstractSchemaEditCommand {

	private AvroNode arrayOrMapNode;
	
	private AvroNode newArrayOrMapNode;
	
	public SwitchArrayMapAttributeCommand(AvroContext context, AvroNode arrayOrMapNode, int notifications) {
		super(context, notifications);		
		this.arrayOrMapNode = arrayOrMapNode;
	}

	@Override
	public void run() {
		newArrayOrMapNode = getController().switchArrayMap(arrayOrMapNode);
		FieldNode fieldNode = ModelUtil.getFirstParentOfType(newArrayOrMapNode, false, FieldNode.class);
		doNotifications(fieldNode, newArrayOrMapNode);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		arrayOrMapNode = getController().switchArrayMap(newArrayOrMapNode);
		FieldNode fieldNode = ModelUtil.getFirstParentOfType(arrayOrMapNode, false, FieldNode.class);
		doNotifications(fieldNode, arrayOrMapNode);
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
		return arrayOrMapNode.getType() == NodeType.ARRAY ? "Switch array to map" : "Switch map to array";
	}

	@Override
	public void dispose() {
		// 
	}
	
}
