package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;


public class NodeTypePropertyTester extends PropertyTester {

	public static final String ID = "org.talend.avro.schema.editor.edit.NodeTypePropertyTester"; //$NON-NLS-1$
	
	public static final String PROPERTY = "NodeType"; //$NON-NLS-1$
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY.equals(property)) {
			
			if (receiver instanceof AvroNode) {
				AvroNode node = (AvroNode) receiver;
				return checkNodeType(node.getType(), args);
			}
			
		}
		return false;
	}

	protected boolean checkNodeType(NodeType type, Object[] args) {
		for (Object arg : args) {
			String nodeTypeStr = (String) arg;
			NodeType nodeType = NodeType.valueOf(nodeTypeStr.toUpperCase());
			if (nodeType == type) {
				return true;
			}
		}
		return false;
	}
	
}
