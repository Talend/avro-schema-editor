package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.menus.IMenuService;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;

public class SchemaPopupMenuConfigurationImpl implements PopupMenuConfiguration {

	public static final String POPUP_MENU_ID = "popup:org.talend.avro.schema.editor.viewer.tree"; //$NON-NLS-1$
	
	public static final String NEW_SECTION = "New Section"; //$NON-NLS-1$

	public static final String OPEN_SECTION = "Open Section"; //$NON-NLS-1$
	
	public static final String EXPAND_SECTION = "Expand Section"; //$NON-NLS-1$
	
	private static final String[] ORDERED_SECTIONS = new String[] { NEW_SECTION, OPEN_SECTION, EXPAND_SECTION };
	
	private IEditorServiceProvider serviceProvider;
	
	private AvroContext context;
	
	public SchemaPopupMenuConfigurationImpl(IEditorServiceProvider serviceProvider, AvroContext context) {
		super();
		this.serviceProvider = serviceProvider;
		this.context = context;
	}
	
	protected IEditorServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	protected AvroContext getContext() {
		return context;
	}

	protected String getPopupMenuId() {
		return POPUP_MENU_ID + "." + context.getKind().toString().toLowerCase();
	}
	
	@Override
	public void fillPopupMenu(IMenuManager manager, SchemaViewer viewer) {
		for (String section : ORDERED_SECTIONS) {
            manager.add(new Separator(section));
        }
		IMenuService service = serviceProvider.getMenuService();
        service.populateContributionManager((ContributionManager) manager, getPopupMenuId());
	}
	
}
