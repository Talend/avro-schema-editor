package org.talend.avro.schema.editor.view;

import org.talend.avro.schema.editor.edit.services.IEditorService;
import org.talend.avro.schema.editor.edit.services.NotificationService;

public interface IViewService extends NotificationService, IEditorService {

	/**
	 * Register a new view.
	 * 
	 * @param viewId define the insertion position. If null add the view at the end. If a view already exists, it is moved after the new one 
	 * @param view the new view to be added.
	 */
	void registerView(String viewId, IView view);
		
	/**
	 * Remove an existing view.
	 * 
	 * @param viewId the identifier of the view 
	 * @return the position of the removed view.
	 * If you want to replace a view implementation, you have to do: 
	 * 		String pos = unregisterView(viewId);
	 * 		IView view = new View(viewId);
	 * 		registerView(pos, view); 
	 */
	String unregisterView(String viewId);
	
	String[] getViewIds();
	
	IView getView(String viewId);
	
}
