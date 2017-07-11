package org.talend.avro.schema.editor.edit.services;

import org.eclipse.ui.menus.IMenuService;
import org.talend.avro.schema.editor.context.services.IContextualServiceProvider;

/**
 * Provides editor services. It gives also an access to some eclipse services (IMenuService). It is UI.
 * 
 * @author timbault
 *
 */
public interface IEditorServiceProvider extends IContextualServiceProvider {

	IMenuService getMenuService();
	
}
