package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NameService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.NameSpaceDefinition;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class AvroNameValidator implements IInputValidator {

	private AvroContext context;
	
	private AvroNode node;
	
	public AvroNameValidator(AvroContext context, AvroNode node) {
		super();
		this.context = context;
		this.node = node;
	}	
	
	@Override
	public String isValid(String newValue) {
		
		NodeType type = node.getType();		
		
		// first check the name syntax
		NameService nameService = context.getService(NameService.class);
		String msg = nameService.validateName(newValue, node);
		if (msg != null) {
			return msg;
		}		
		
		String newFullName = null;
		
		boolean wasNameWithNameSpace = AttributeUtil.isNameWithNameSpace(node);
		boolean isNewNameWithNameSpace = AttributeUtil.isNameWithNameSpace(newValue);
		
		if (isNewNameWithNameSpace) {
			
			String newNameSpace = AttributeUtil.getNameSpaceFromFullName(newValue);
			String name = AttributeUtil.getTrueNameFromFullName(newValue);
			newFullName = AttributeUtil.getFullName(newNameSpace, name);
			
		} else if (wasNameWithNameSpace) {
			
			String nameSpace = AttributeUtil.tryToGetTrueNameSpace(node, NameSpaceDefinition.EXPLICIT, NameSpaceDefinition.INHERITED);
			newFullName = AttributeUtil.getFullName(nameSpace, newValue);			
			
		} else {
			
			String nameSpace = AttributeUtil.getTrueNameSpace(node);
			newFullName = AttributeUtil.getFullName(nameSpace, newValue);
			
		}
		
		if (newFullName != null) {
			
			SchemaRegistry schemaRegistry = context.getSchemaRegistry();		
			AvroNode registeredNode = schemaRegistry.getRegisteredNode(type, newFullName);
			if (registeredNode != null && registeredNode != node) {
				return "Invalid " + type.getDefaultLabel() + " name: it is already registered";
			}
			
		}
		
		return null;
	}

}
