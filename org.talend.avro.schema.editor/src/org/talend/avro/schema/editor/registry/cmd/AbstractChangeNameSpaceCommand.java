package org.talend.avro.schema.editor.registry.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.NameSpaceAttribute;
import org.talend.avro.schema.editor.model.attributes.NameSpaceDefinition;
import org.talend.avro.schema.editor.model.attributes.cmd.ChangeAttributeCommand;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public abstract class AbstractChangeNameSpaceCommand extends SchemaEditCompositeCommand {
	
	protected AbstractChangeNameSpaceCommand(String label, AvroContext context, int notifications) {
		super(label, context, notifications);		
	}

	public final void init() {
		buildCommands(getImpactedNode());
	}
	
	protected abstract NSNode getImpactedNode();
	
	protected void buildCommands(NSNode nsNode) {
		
		SchemaRegistry schemaRegistry = getContext().getSchemaRegistry();
		NameSpaceRegistry nameSpaceRegistry = schemaRegistry.getNameSpaceRegistry();
		
		// first get all impacted registered name spaces
		// then get all impacted avro nodes
		Map<NSNode, List<AvroNode>> ns2nodes = new HashMap<>();
		
		populateNameSpaces(schemaRegistry, nameSpaceRegistry, ns2nodes, nsNode);
				
		// generate a change name space command for each impacted avro node
		for (Map.Entry<NSNode, List<AvroNode>> entry : ns2nodes.entrySet()) {
			NSNode key = entry.getKey();
			List<AvroNode> nodes = entry.getValue();
			for (AvroNode node : nodes) {
				NameSpaceDefinition nameSpaceDefinition = AttributeUtil.getNameSpaceDefinition(node);
				switch (nameSpaceDefinition) {
				case EXPLICIT:
					// name space is defined in the namespace attribute, change it					
					String newNameSpace = getNewNameSpace(nsNode, key);
					NameSpaceAttribute nameSpaceAttribute = node.getAttributes().getAttributeFromClass(AvroAttributes.NAME_SPACE, NameSpaceAttribute.class);
					IEditCommand cmd = 
							new ChangeAttributeCommand<String>(getInternalContext(), nameSpaceAttribute, newNameSpace, Notifications.NOT_REF);
					addCommand(cmd);
					break;
				case IN_NAME:
					// name space is defined in the name attribute, change it
					// TODO
					break;
				case INHERITED:
					// no need to generate a command, the name space will be updated automatically
					break;
				}				
			}
		}
		
		postBuildCommands();
		
	}
	
	protected abstract String getNewNameSpace(NSNode changedNode, NSNode childNode);
	
	protected abstract void postBuildCommands();
	
	private void populateNameSpaces(
			SchemaRegistry schemaRegistry, NameSpaceRegistry nameSpaceRegistry, Map<NSNode, List<AvroNode>> ns2nodes, NSNode nsNode) {
		String namespace = nameSpaceRegistry.getNameSpace(nsNode);
		List<AvroNode> nodesFromNameSpace = schemaRegistry.getNodesFromNameSpace(namespace);
		ns2nodes.put(nsNode, nodesFromNameSpace);
		for (NSNode child : nsNode.getChildren()) {
			populateNameSpaces(schemaRegistry, nameSpaceRegistry, ns2nodes, child);
		}
	}

}
