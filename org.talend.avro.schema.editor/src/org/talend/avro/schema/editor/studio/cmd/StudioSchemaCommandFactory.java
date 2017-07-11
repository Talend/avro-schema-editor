package org.talend.avro.schema.editor.studio.cmd;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.cmd.AvroSchemaEditCommandFactory;
import org.talend.avro.schema.editor.model.AvroNode;

public class StudioSchemaCommandFactory extends AvroSchemaEditCommandFactory {

	@Override
	public IEditCommand createRemoveElementCommand(AvroNode node, int notifications) {
		int notif = notifications | Notifications.SELECT;
		return new StudioRemoveElementCommand(getContext(), node, notif);
	}
	
}
