package org.talend.avro.schema.editor.edit.services;


import java.util.List;

import org.talend.avro.schema.editor.context.AbstractContextualService;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of the name service.
 * 
 * @author timbault
 *
 */
public class AvroNameService extends AbstractContextualService implements NameService {

	private static final String DEFAULT_FIELD_NAME = "Field";
	
	private static final String DEFAULT_RECORD_NAME = "Record";
	
	private static final String DEFAULT_ENUM_NAME = "Enum";
	
	private static final String DEFAULT_FIXED_NAME = "Fixed";
		
	private static final String NAME_PATTERN = "[A-Za-z_][A-Za-z0-9_]*"; //$NON-NLS-1$
	
	private static final String NAME_SPACE_PATTERN = NAME_PATTERN + "[\\." + NAME_PATTERN + "]*"; //$NON-NLS-1$ //$NON-NLS-2$	

	private SchemaRegistry schemaRegistry;
	
	@Override
	public void init(AvroContext context) {
		super.init(context);
		this.schemaRegistry = context.getSchemaRegistry();
	}

	@Override
	public String validateName(String name, AvroNode node) {
		if (name != null && !name.trim().isEmpty()) {
			if (!name.matches(NAME_PATTERN) && !name.matches(NAME_SPACE_PATTERN)) {
				return "Invalid name: a valid name starts with [A-Za-z_] and subsequently contains only [A-Za-z0-9_]";
			}
			return null;
		}
		return "Name cannot be empty";
	}

	@Override
	public String validateNameSpace(String nameSpace, AvroNode node) {
		if (nameSpace != null && !nameSpace.trim().isEmpty()) {
			if (!nameSpace.matches(NAME_SPACE_PATTERN)) {
				return "Invalid namespace: a valid namespace is a dot-separated sequence of valid names";
			}
		}
		return null;
	}

	@Override
	public String getAvailableName(NodeType type, AvroNode contextualNode) {
				
		switch (type) {
		case FIELD:
			if (contextualNode.getType() == NodeType.FIELD) {
				contextualNode = contextualNode.getParent();
				// should be a record node
			}
			int size = ModelUtil.getChildrenCount(contextualNode);
			return DEFAULT_FIELD_NAME + "_" + ++size;
		case RECORD:			
		case ENUM:
		case FIXED:
			String nameSpace = AttributeUtil.getEnclosingNameSpace(contextualNode, true);
			return getAvailableNameSpacedNodeName(type, nameSpace);
		default:
			return null;
		}						
		
	}
	
	protected String getDefaultName(NodeType type) {
		switch (type) {
		case RECORD:
			return DEFAULT_RECORD_NAME;
		case ENUM:
			return DEFAULT_ENUM_NAME;
		case FIXED:
			return DEFAULT_FIXED_NAME;
		case FIELD:
			return DEFAULT_FIELD_NAME;
		default:
			return "";
		}
	}
	
	protected String getAvailableNameSpacedNodeName(NodeType nsType, String nameSpace) {
		SchemaRegistry schemaRegistry = getContext().getSchemaRegistry();
		List<AvroNode> nodes = schemaRegistry.getNodesFromNameSpace(nsType, nameSpace);
		String defaultName = getDefaultName(nsType);
		boolean used = isNameUsed(defaultName, nodes);		
		if (!used) {
			return defaultName;
		}
		int index = 0;
		String name = null;
		while (used) {
			index++;
			name = defaultName + "_" + index;
			used = isNameUsed(name, nodes);
		}		
		return name;
	}

	protected boolean isNameUsed(String name, List<AvroNode> nodes) {
		for (AvroNode node : nodes) {
			if (AttributeUtil.getNameFromAttribute(node).equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getValidNameCopy(AvroNode sourceNode, AvroNode copyNode, AvroNode targetNode) {	
		String originName = AttributeUtil.getNameFromAttribute(sourceNode);
		NodeType type = sourceNode.getType();
		switch (type) {
		case FIELD:
			String name = originName;
			int counter = 0;
			boolean nameAvailable = isFieldNameAvailable(targetNode, name);
			while (!nameAvailable) {
				counter++;
				name = originName + "_" + counter;
				nameAvailable = isFieldNameAvailable(targetNode, name);
			}
			return name;
		case RECORD:
		case ENUM:
		case FIXED:
			counter = 1;
			name = originName + "_" + counter; 
			String nameSpace = AttributeUtil.getNameSpaceFromAttribute(copyNode);
			nameAvailable = isSchemaNameAvailable(type, name, nameSpace);
			while (!nameAvailable) {
				counter++;
				name = originName + "_" + counter;
				nameAvailable = isSchemaNameAvailable(type, name, nameSpace);
			}
			return name;
		default:
			return originName;
		}
	}

	protected boolean isSchemaNameAvailable(NodeType type, String name, String nameSpace) {
		return !schemaRegistry.isSchemaNameAlreadyRegistered(type, name, nameSpace);
	}
	
	protected boolean isFieldNameAvailable(AvroNode targetNode, String name) {
		AvroNode recordNode = null;
		NodeType targetType = targetNode.getType();
		if (targetType == NodeType.RECORD) {
			recordNode = targetNode;
		} else if (targetType == NodeType.FIELD) {
			recordNode = targetNode.getParent();
		}
		if (recordNode != null) {
			for (AvroNode child : recordNode.getChildren()) {
				String childName = AttributeUtil.getNameFromAttribute(child);
				if (childName.trim().equals(name.trim())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}
	
}
