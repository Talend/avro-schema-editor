package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.cmd.AddNameSpaceAction;

public class AddNameSpaceHandler extends AbstractRegistryViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.view.addNameSpace"; //$NON-NLS-1$
	
	@Override
	protected Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event) {
		
		NSNode nsNode = getSelectedNSNode(registryView);
		
		if (nsNode != null) {

			AddNameSpaceAction action = new AddNameSpaceAction();
			action.setContext(editor.getContext());
			action.setNSNode(nsNode);
			action.run();
			
		}

		return null;
	}

}
