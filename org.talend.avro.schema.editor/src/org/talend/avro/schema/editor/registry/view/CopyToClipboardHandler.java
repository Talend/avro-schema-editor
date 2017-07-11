package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class CopyToClipboardHandler extends AbstractRegistryViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.copy"; //$NON-NLS-1$
	
	@Override
	protected Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event) {
		
		IStructuredSelection selection = (IStructuredSelection) registryView.getSelection();
		
		if (!selection.isEmpty()) {
			
			AvroNode node = (AvroNode) selection.getFirstElement();
						
			String fullName = AttributeUtil.getFullName(node);
			
			Clipboard clipboard = new Clipboard(Display.getDefault());
			
			Transfer[] transfers = null;
			
			try {
	            TextTransfer textTransfer = TextTransfer.getInstance();
	            transfers = new Transfer[] { textTransfer };
	            clipboard.setContents(new Object[] { fullName }, transfers);
	        }
	        finally {
	            clipboard.dispose();
	        }
			
		}
		
		return null;
	}

}
