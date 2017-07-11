package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.action.IMenuManager;

public interface PopupMenuConfiguration {

	void fillPopupMenu(IMenuManager manager, SchemaViewer viewer);
	
}
