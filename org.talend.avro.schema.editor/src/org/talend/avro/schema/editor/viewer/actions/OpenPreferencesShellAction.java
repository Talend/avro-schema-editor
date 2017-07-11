package org.talend.avro.schema.editor.viewer.actions;

import java.awt.MouseInfo;
import java.awt.Point;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.viewer.handlers.SchemaViewerPreferencesDialog;

public class OpenPreferencesShellAction extends Action {
	
	private IEditorServiceProvider serviceProvider;
	
	public OpenPreferencesShellAction(IEditorServiceProvider serviceProvider) {
		super("Preferences", IAction.AS_PUSH_BUTTON);
		this.serviceProvider = serviceProvider;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.CONFIGURE);
	}

	@Override
	public String getToolTipText() {
		return "Set display preferences";
	}

	@Override
	public void run() {		
		
		Point location = MouseInfo.getPointerInfo().getLocation();
		Rectangle bounds = new Rectangle(location.x, location.y, 250, 250);
		
		Shell shell = Display.getDefault().getActiveShell();
		SchemaViewerPreferencesDialog dialog = new SchemaViewerPreferencesDialog(shell, bounds, serviceProvider);		
		dialog.open();				
	}
	
}
