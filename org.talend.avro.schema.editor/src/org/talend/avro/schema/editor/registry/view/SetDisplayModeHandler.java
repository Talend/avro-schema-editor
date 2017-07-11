package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.registry.view.SchemaRegistryView.DisplayMode;

public class SetDisplayModeHandler extends AbstractRegistryViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.view.setDisplayMode"; //$NON-NLS-1$
	
	public static final String PARAMETER = "org.talend.avro.schema.editor.registry.view.displayMode"; //$NON-NLS-1$
	
	@Override
	protected Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event) {

		String displayModeStr = event.getParameter(PARAMETER);
		DisplayMode displayMode = DisplayMode.valueOf(displayModeStr.toUpperCase());
		
		registryView.setDisplayMode(displayMode);
		
		return null;
	}

}
