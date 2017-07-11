package org.talend.avro.schema.editor.studio.viewer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.AddElementAction;
import org.talend.avro.schema.editor.edit.actions.ContextualAction;
import org.talend.avro.schema.editor.edit.actions.CopyElementAction;
import org.talend.avro.schema.editor.edit.actions.PasteElementAction;
import org.talend.avro.schema.editor.edit.actions.RemoveElementAction;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.viewer.SchemaPopupMenuConfigurationImpl;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class StudioPopupMenuConfigurationImpl extends SchemaPopupMenuConfigurationImpl {

	public StudioPopupMenuConfigurationImpl(IEditorServiceProvider serviceProvider, AvroContext context) {
		super(serviceProvider, context);
	}

	@Override
	public void fillPopupMenu(IMenuManager manager, SchemaViewer viewer) {
		// add element
		ContextualAction action = new AddElementAction("Add", IAction.AS_PUSH_BUTTON, Notifications.notifyRefreshReveal(getContext()));
		action.init(getContext(), true);
		manager.add(action);
		// remove element
		action = new RemoveElementAction("Remove", IAction.AS_PUSH_BUTTON, Notifications.NOT_REF);
		action.init(getContext(), true);
		manager.add(action);		
		// copy
		action = new CopyElementAction("Copy", IAction.AS_PUSH_BUTTON, Notifications.NONE);
		action.init(getContext(), true);
		manager.add(action);
		// paste
		action = new PasteElementAction("Paste", IAction.AS_PUSH_BUTTON, Notifications.NOT_REF);
		action.init(getContext(), true);
		manager.add(action);
	}	
	
}
