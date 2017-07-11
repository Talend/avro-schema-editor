package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AbstractContextualService;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

/**
 * Base implementation of an {@link IAttributeCommandFactory}.
 * 
 * @author timbault
 *
 */
public class AttributeCommandFactoryImpl extends AbstractContextualService implements IAttributeCommandFactory {

	@Override
	public <T> IEditCommand createChangeAttributeCommand(AvroAttribute<T> attribute, T newValue,
			int notifications) {		
		return new ChangeAttributeCommand<T>(getContext(), attribute, newValue, notifications);
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}	
	
}
