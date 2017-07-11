package org.talend.avro.schema.editor.registry.cmd;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.NameAttribute;
import org.talend.avro.schema.editor.model.attributes.NameSpaceAttribute;
import org.talend.avro.schema.editor.model.attributes.NameSpaceDefinition;
import org.talend.avro.schema.editor.model.attributes.cmd.ChangeAttributeCommand;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class ChangeNameSpaceCommand extends SchemaEditCompositeCommand {

	private AvroNode sourceNode;
	
	private NSNode targetNode;
	
	public ChangeNameSpaceCommand(AvroContext context,
			AvroNode sourceNode, NSNode targetNode, int notifications) {
		super("Change Name Space", context, notifications);
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		buildCommands();
	}

	private void buildCommands() {
		
		SchemaRegistry schemaRegistry = getContext().getSchemaRegistry();
		NameSpaceRegistry nameSpaceRegistry = schemaRegistry.getNameSpaceRegistry();				

		NameSpaceAttribute nameSpaceAttribute = sourceNode.getAttributes().getAttributeFromClass(AvroAttributes.NAME_SPACE, NameSpaceAttribute.class);
		String newNameSpace = nameSpaceRegistry.getNameSpace(targetNode);
		
		NameSpaceDefinition nameSpaceDefinition = AttributeUtil.getNameSpaceDefinition(sourceNode);
		switch (nameSpaceDefinition) {
		case EXPLICIT:	
		case INHERITED:
			// generate change name space attribute command			
			IEditCommand cmd = 
					new ChangeAttributeCommand<String>(getInternalContext(), nameSpaceAttribute, newNameSpace, Notifications.NOT_REF);
			addCommand(cmd);
			break;
		case IN_NAME:
			// generate change name attribute command
			NameAttribute nameAttribute = sourceNode.getAttributes().getAttributeFromClass(AvroAttributes.NAME, NameAttribute.class);
			String name = AttributeUtil.getTrueName(sourceNode);
			String newFullName = AttributeUtil.getFullName(newNameSpace, name);
			cmd = new ChangeAttributeCommand<String>(getInternalContext(), nameAttribute, newFullName, Notifications.NOT_REF);
			addCommand(cmd);
			break;
		}
		
	}
	
}
