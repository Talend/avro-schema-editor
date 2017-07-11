package org.talend.avro.schema.editor.log;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.utils.UIUtils;

public class DisplayNameSpaceTreeHandler extends AbstractLogViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.log.nameSpaceTree"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		AvroSchemaEditor editor = UIUtils.pickAvroSchemaEditorFromEditorParts();
		
		if (editor != null) {
			
			NSNode nameSpaceTree = editor.getContext().getSchemaRegistry().getNameSpaceTree();
			AvroSchemaLogger.logNameSpaceTree(nameSpaceTree, false);
			
		}
		
		return null;
	}	

}
