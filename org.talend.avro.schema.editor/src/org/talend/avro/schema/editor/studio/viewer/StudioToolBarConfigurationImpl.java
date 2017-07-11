package org.talend.avro.schema.editor.studio.viewer;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.viewer.SchemaToolBarConfigurationImpl;
import org.talend.avro.schema.editor.viewer.ToolBarConfiguration;
import org.talend.avro.schema.editor.viewer.actions.OpenPreferencesShellAction;

/**
 * Implementation of a {@link ToolBarConfiguration} for the studio schema editor.
 * 
 * @author timbault
 *
 */
public class StudioToolBarConfigurationImpl extends SchemaToolBarConfigurationImpl {

	public static final String TOP_TOOLBAR_ID = "org.talend.avro.schema.editor.studio.viewer.toolbar.top"; //$NON-NLS-1$
	
	public StudioToolBarConfigurationImpl(IEditorServiceProvider serviceProvider, AvroContext context) {
		super(serviceProvider, context, new boolean[0]);
	}

	@Override
	public String[] getTopToolBarIds() {
		return new String[] { TOP_TOOLBAR_ID };
	}

	@Override
	public void fillToolBar(ToolBarManager manager, String toolBarId) {
		if (TOP_TOOLBAR_ID.equals(toolBarId)) {
			fillTopToolBar(manager);
		} else if (toolBarId.equals(getToolBarId(Kind.EDITION))) {
			fillEditionToolBar(manager);
		}
	}	
	
	protected void fillTopToolBar(ToolBarManager manager) {
		manager.add(new OpenPreferencesShellAction(getServiceProvider()));
	}

	@Override
	public boolean hasTitle(String toolBarId) {
		return false;
	}

	@Override
	public int getToolBarStyle(String toolBarId) {
		if (TOP_TOOLBAR_ID.equals(toolBarId)) {
			return SWT.HORIZONTAL | SWT.FLAT;
		}
		return SWT.HORIZONTAL | SWT.FLAT | SWT.BEGINNING;
	}
	
}
