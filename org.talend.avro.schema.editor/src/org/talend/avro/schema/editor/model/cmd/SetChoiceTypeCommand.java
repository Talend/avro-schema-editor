package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.UnionNode;

/**
 * This command allows to change the 'choice nature' of an union node.
 * 
 * @author timbault
 *
 */
public class SetChoiceTypeCommand extends AbstractSchemaEditCommand {

	private UnionNode unionNode;
	
	private boolean choice;
		
	public SetChoiceTypeCommand(AvroContext context, UnionNode unionNode, boolean choice, int notifications) {
		super(context, notifications);
		this.unionNode = unionNode;
		this.choice = choice;
	}
	
	@Override
	public void run() {
		getController().setChoice(unionNode, choice);
		doNotifications(unionNode.getParent());
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		getController().setChoice(unionNode, !choice);	
		doNotifications(unionNode.getParent());
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
		return "Change choice type";
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

}
