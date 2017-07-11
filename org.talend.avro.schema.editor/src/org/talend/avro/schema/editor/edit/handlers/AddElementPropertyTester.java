package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.EditUtils;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

public class AddElementPropertyTester extends SchemaEditorPropertyTester {

	public static final String ID = "org.talend.avro.schema.editor.edit.AddElementPropertyTester"; //$NON-NLS-1$
	
	public static final String PROPERTY = "CanAddElement"; //$NON-NLS-1$
	
	@Override
	protected boolean test(Object receiver, String property, Object[] args, Object expectedValue, AvroSchemaEditor editor) {
		if (PROPERTY.equals(property)) {
			
			if (receiver instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) receiver;
				
				if (selection.size() == 1) {
					
					AvroNode node = (AvroNode) selection.getFirstElement();
					
					NodeType[] types = EditUtils.getAddableNodeTypes(node, editor.getServiceProvider().getService(AvroSchemaController.class));
					
					return types.length > 0;
					
				}
				
			}
			
		}
		return false;
	}

}
