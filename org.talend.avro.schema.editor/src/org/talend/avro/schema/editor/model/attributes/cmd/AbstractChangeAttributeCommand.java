package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public abstract class AbstractChangeAttributeCommand<T> extends AbstractSchemaEditCommand {

	private AvroAttribute<T> attribute;
	
	private T oldValue;
	
	private T newValue;
	
	protected AbstractChangeAttributeCommand(
			AvroContext context, AvroAttribute<T> attribute, T newValue, int notifications) {
		super(context, notifications);
		this.attribute = attribute;
		this.oldValue = getValue(attribute);
		this.newValue = newValue;
	}
	
	protected AvroAttribute<T> getAttribute() {
		return attribute;
	}

	protected T getOldValue() {
		return oldValue;
	}

	protected T getNewValue() {
		return newValue;
	}

	protected abstract T getValue(AvroAttribute<T> attribute);
	
	protected abstract void applyValue(AvroAttribute<T> attribute, T value);
	
	@Override
	public void run() {		
		applyValue(attribute, newValue);
		doNotifications(attribute.getHolder());
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		applyValue(attribute, oldValue);
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
		return "Change attribute " + attribute.getName();
	}

	@Override
	public void dispose() {
		// 
	}

}
