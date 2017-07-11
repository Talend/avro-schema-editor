package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

/**
 * This factory creates commands in order to change avro node attributes.
 * 
 * @author timbault
 *
 */
public interface IAttributeCommandFactory extends IContextualService {

	/**
	 * Create a command to set new value to the specified attribute. 
	 * 
	 * @param attribute
	 * @param newValue
	 * @param notifications
	 * @return
	 */
	<T> IEditCommand createChangeAttributeCommand(AvroAttribute<T> attribute, T newValue, int notifications);
	
}
