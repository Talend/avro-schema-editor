package org.talend.avro.schema.editor.viewer;

import java.util.StringTokenizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.menus.IMenuService;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.AddElementAction;
import org.talend.avro.schema.editor.edit.actions.ContextualAction;
import org.talend.avro.schema.editor.edit.actions.CopyElementAction;
import org.talend.avro.schema.editor.edit.actions.MoveInDirectionAction;
import org.talend.avro.schema.editor.edit.actions.PasteElementAction;
import org.talend.avro.schema.editor.edit.actions.RemoveElementAction;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.Metadata;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.cmd.Direction;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of a {@link ToolBarConfiguration} for the avro schema editor.
 * 
 * @author timbault
 *
 */
public class SchemaToolBarConfigurationImpl implements ToolBarConfiguration {

	/**
	 * Default toolbar identifier used by the eclipse command/handler extensions.
	 * 
	 */
	public static final String TOOLBAR_ID = "toolbar:org.talend.avro.schema.editor.viewer.tree.toolbar"; //$NON-NLS-1$
    
	/**
	 * 
	 * @author timbault
	 *
	 */
	public enum Kind {
		DISPLAY, NAVIGATION, EDITION
	}
	
	public static final String DOT = "."; //$NON-NLS-1$
	
	private IEditorServiceProvider serviceProvider;
	
    private AvroContext context;
        
    private boolean[] alignments;
    
	public SchemaToolBarConfigurationImpl(IEditorServiceProvider serviceProvider, AvroContext context, boolean[] alignments) {
		super();
		this.serviceProvider = serviceProvider;
		this.context = context;
		this.alignments = alignments;
	}
	
	protected AvroContext getContext() {
		return context;
	}
	
	protected IEditorServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	protected String getToolBarId(Kind toolBarKind) {
		return TOOLBAR_ID 
				+ "." + toolBarKind.toString().toLowerCase()
				+ "." + context.getKind().toString().toLowerCase();
	}
	
	protected Kind getToolBarKind(String toolBarId) {
		Kind kind = null;
		StringTokenizer tokenizer = new StringTokenizer(toolBarId, DOT);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			try {
				kind = Kind.valueOf(token.toUpperCase());
			} catch (IllegalArgumentException e) {
				continue;
			}
			if (kind != null) {
				return kind;
			}
		}
		return null;
	}
	
	@Override
	public void fillToolBar(ToolBarManager manager, String toolBarId) {
		Kind toolBarKind = getToolBarKind(toolBarId);
		if (toolBarKind != null) {
			fillToolBar(manager, toolBarKind);
		}
	}
	
	protected void fillToolBar(ToolBarManager manager, Kind toolBarKind) {
		switch (toolBarKind) {
		case EDITION:
			fillEditionToolBar(manager);
			break;
		default:
			populateToolBar(manager, getToolBarId(toolBarKind));
			break;
		}
	}

	protected void fillEditionToolBar(ToolBarManager manager) {
		// add element
		AddContextualAction(manager, 
				new AddElementAction("Add new element", IAction.AS_PUSH_BUTTON, Notifications.notifyRefreshReveal(getContext())));		
		// remove element
		AddContextualAction(manager, 
				new RemoveElementAction("Remove element(s)", IAction.AS_PUSH_BUTTON, Notifications.NOT_REF));		
		// move up
		AddContextualAction(manager, 
				new MoveInDirectionAction(MoveInDirectionAction.getLabel(Direction.UP), IAction.AS_PUSH_BUTTON, Direction.UP, Notifications.NOT_REF));
		// move down
		AddContextualAction(manager,
				new MoveInDirectionAction(MoveInDirectionAction.getLabel(Direction.DOWN), IAction.AS_PUSH_BUTTON, Direction.DOWN, Notifications.NOT_REF));		
		// copy
		AddContextualAction(manager,		
				new CopyElementAction("Copy element(s)", IAction.AS_PUSH_BUTTON, Notifications.NONE));
		// paste
		AddContextualAction(manager,
				new PasteElementAction("Paste element(s)", IAction.AS_PUSH_BUTTON, Notifications.NOT_REF));
	}
	
	protected void AddContextualAction(ToolBarManager manager, ContextualAction action) {
		action.init(getContext(), true);
		manager.add(action);
	}
	
	protected void populateToolBar(ToolBarManager manager, String toolBarId) {
		IMenuService service = serviceProvider.getMenuService();
        service.populateContributionManager(manager, toolBarId);
	}
	
	protected boolean isLeftAlignment(Kind kind) {
		if (alignments != null && alignments.length > kind.ordinal()) {
			return alignments[kind.ordinal()];
		}
		return true;
	}

	@Override
	public String[] getTopToolBarIds() {
		return new String[] { getToolBarId(Kind.DISPLAY), getToolBarId(Kind.NAVIGATION) };
	}

	@Override
	public String[] getBottomToolBarIds() {
		return new String[] { getToolBarId(Kind.EDITION) };
	}

	@Override
	public int getToolBarStyle(String toolBarId) {
		int style = SWT.HORIZONTAL | SWT.FLAT;// | SWT.BEGINNING)
		Kind toolBarKind = getToolBarKind(toolBarId);
		if (isLeftAlignment(toolBarKind)) {
			style = style | SWT.BEGINNING;
		}
		return style;
	}

	@Override
	public boolean hasTitle(String toolBarId) {
		return getToolBarKind(toolBarId) == Kind.NAVIGATION;
	}

	@Override
	public String getTitle(String toolBarId, AvroNode node) {
		if (node.getType().isRoot()) {
			RootNode rootNode = (RootNode) node;
			return rootNode.getMetadata(Metadata.SCHEMA_DESCRIPTION);
		}
		return null;
	}

	@Override
	public Image getImage(String toolBarId, AvroNode node) {
		if (node.getType().isRoot()) {
			return AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.SCHEMA_FILE);
		}
		return null;
	}	

}
