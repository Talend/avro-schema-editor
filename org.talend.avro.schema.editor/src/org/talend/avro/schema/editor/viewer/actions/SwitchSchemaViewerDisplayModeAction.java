package org.talend.avro.schema.editor.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;

public class SwitchSchemaViewerDisplayModeAction extends Action {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.switchSchemaViewerDisplayMode"; //$NON-NLS-1$
	
	private AvroSchemaEditor editor;

	private AvroContext.Kind kind;
	
	public SwitchSchemaViewerDisplayModeAction(AvroSchemaEditor editor, AvroContext.Kind kind) {
		super(CMD_ID, IAction.AS_PUSH_BUTTON);
		this.editor = editor;
		this.kind = kind;
	}

	protected DisplayMode getDisplayMode() {
		return editor.getContentPart().getSchemaViewer(kind).getDisplayMode();
	}
	
	@Override
	public void run() {
		SchemaViewer schemaViewer = editor.getContentPart().getSchemaViewer(kind);
		DisplayMode displayMode = schemaViewer.getDisplayMode();
		schemaViewer.setDisplayMode(displayMode.switchMode());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (getDisplayMode() == DisplayMode.WITH_COLUMNS) {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.WITH_COLUMNS_DISPLAY_MODE);
		} else {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.WITHOUT_COLUMNS_DISPLAY_MODE);
		}
	}

	@Override
	public String getToolTipText() {
		if (getDisplayMode() == DisplayMode.WITH_COLUMNS) {
			return "Remove attribute columns";
		} else {
			return "Show attribute columns";
		}
	}
	
}
