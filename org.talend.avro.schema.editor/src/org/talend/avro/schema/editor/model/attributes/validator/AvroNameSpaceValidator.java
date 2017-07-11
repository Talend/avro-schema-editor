package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NameService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class AvroNameSpaceValidator implements IInputValidator {

	private AvroContext context;
	
	private AvroNode node;
	
	public AvroNameSpaceValidator(AvroContext context, AvroNode node) {
		super();
		this.context = context;
		this.node = node;
	}

	@Override
	public String isValid(String newValue) {
		
		NodeType type = node.getType();		
		
		// first check the namespace syntax
		NameService nameService = context.getService(NameService.class);
		String msg = nameService.validateNameSpace(newValue, node);
		if (msg != null) {
			return msg;
		}
		
		if (!AttributeUtil.isNameWithNameSpace(node)) {
			
			String name = AttributeUtil.getNameFromAttribute(node);
			String newFullName = null;
			
			if (AttributeUtil.isStringDefined(newValue)) {
				
				// check that this new namespace is not already used by another complex type				
				newFullName = AttributeUtil.getFullName(newValue, name); 								
				
			} else {
				
				// empty name space
				// we have to check with the inherited name space
				String enclosingNameSpace = AttributeUtil.getEnclosingNameSpace(node, false);
				newFullName = AttributeUtil.getFullName(enclosingNameSpace, name);
				
			}
			
			if (newFullName != null) {
				SchemaRegistry schemaRegistry = context.getSchemaRegistry();		
				AvroNode registeredNode = schemaRegistry.getRegisteredNode(type, newFullName);
				if (registeredNode != null && registeredNode != node) {
					return "Invalid " + type.getDefaultLabel() + " namespace: it is already used in a registered " + type.getDefaultLabel();
				}
			}			
			
		}
		
		return null;
	}

	
	
}
