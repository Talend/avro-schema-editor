package org.talend.avro.schema.editor.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroEditorContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.dnd.DnDSourceListener;
import org.talend.avro.schema.editor.edit.dnd.DnDTargetListener;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.utils.UIUtils;
import org.talend.avro.schema.editor.view.IContextualView;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.column.SchemaViewerColumnEditingSupport;

/**
 * <b>Main component of the {@link AvroSchemaEditor}.</b>
 * <p>
 * It is composed of:
 * <ul>
 * <li> one tree viewer (to display the schema as a tree)
 * <li> several toolbars at top or bottom of the tree.
 * </ul>
 * This component is configurable via {@link SchemaViewerConfiguration}.
 * <p>
 * @author timbault
 * @see AvroSchemaEditor 
 * @see SchemaViewerConfiguration
 */
public class SchemaViewer implements IPostSelectionProvider, IContextualView {
	
	public static final String ID = "org.talend.avro.schema.editor.viewer.SchemaViewer"; //$NON-NLS-1$
	
	public enum DisplayMode {
		WITHOUT_COLUMNS, WITH_COLUMNS;
		
		public DisplayMode switchMode() {
			if (this == WITH_COLUMNS) {
				return DisplayMode.WITHOUT_COLUMNS;
			} else {
				return DisplayMode.WITH_COLUMNS;
			}
		}
	}
	
	private AvroSchemaEditor editor;
	
	private AvroEditorContext context;
	
	private Composite composite;
	
	private TreeViewer treeViewer;
	
	private ToolBarConfiguration toolBarConfiguration;
	
	private Map<String, ToolBarManager> toolbarManagers = new HashMap<>();
	
	private ListenerList selectionChangedListeners = new ListenerList();
	
	private ISelectionChangedListener internalSelectionListener;
	
	private ListenerList postSelectionChangedListeners = new ListenerList();
	
	private ISelectionChangedListener internalPostSelectionListener;
	
	private ISelectionChangedListener postSelectionListenerForContext;
	
	private SchemaViewerConfiguration schemaViewerConfiguration;
	
	private SchemaViewerContentProvider contentProvider;	
	
	private Map<String, Label> toolBarTitles = new HashMap<>();
	
	private DisplayMode displayMode = DisplayMode.WITHOUT_COLUMNS;	
	
	private AttributeColumnConfiguration attributeColumnConfiguration;
	
	private Map<String, TreeViewerColumn> columns = new HashMap<>();
	
	private boolean isRootContent = false;;
	
	private static final int CHARACTER_HEIGHT = 20;
	
	private SchemaNodeRegistry schemaNodeRegistry;
	
	private SchemaViewerNodeConverter nodeConverter;
	
	public SchemaViewer(AvroSchemaEditor editor, AvroEditorContext context) {
		super();
		this.editor = editor;
		this.context = context;
		init();
	}
	
	private void init() {
		schemaNodeRegistry = context.getSchemaNodeRegistry();
		nodeConverter = new SchemaViewerNodeConverter(schemaNodeRegistry);
		internalSelectionListener = new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection outgoingSelection = nodeConverter.convertOutgoingSelection((IStructuredSelection) event.getSelection());
				fireSelectionChangedEvent(outgoingSelection, SchemaViewer.this);
			}
		};
		internalPostSelectionListener = new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection outgoingSelection = nodeConverter.convertOutgoingSelection((IStructuredSelection) event.getSelection());
				firePostSelectionChangedEvent(outgoingSelection, SchemaViewer.this);
			}
		};
		postSelectionListenerForContext = new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				//IStructuredSelection convertedSelection = nodeConverter.convertOutgoingSelection((IStructuredSelection) event.getSelection());
				context.selectionChanged(new SelectionChangedEvent(SchemaViewer.this, event.getSelection()));
			}
		};
	}
	
	public static String getId(AvroContext.Kind kind) {
		return ID + "." + kind.toString().toLowerCase();
	}
	
	@Override
	public String getId() {
		return ID + "." + context.getKind().toString().toLowerCase();
	}

	protected boolean isControlCreated() {
		return treeViewer != null && !treeViewer.getTree().isDisposed();
	}
	
	public void setSchemaViewerConfiguration(SchemaViewerConfiguration schemaViewerConfiguration) {
		this.schemaViewerConfiguration = schemaViewerConfiguration;
		if (!isControlCreated()) {
			this.displayMode = schemaViewerConfiguration.getInitialDisplayMode(editor, context);
		}
		this.toolBarConfiguration = schemaViewerConfiguration.getToolBarConfiguration(editor, context, displayMode);
		// TODO reconfigure the tree viewer if it is already created
	}
	
	public void setAttributeColumnConfiguration(AttributeColumnConfiguration attributeColumnConfiguration) {
		this.attributeColumnConfiguration = attributeColumnConfiguration;
	}
	
	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(final DisplayMode displayMode) {
		if (this.displayMode != displayMode) {
			this.displayMode = displayMode;
			if (isControlCreated()) {
				// update tree viewer
				UIUtils.run(treeViewer, new Runnable() {					
					@Override
					public void run() {
						updateDisplayMode(treeViewer, displayMode);
					}
				}, "SetDisplayMode(" + displayMode.toString().toLowerCase() + ")");				
			}
		}
	}	
	
	protected void fireSelectionChangedEvent(ISelection selection, ISelectionProvider provider) {
        SelectionChangedEvent event = new SelectionChangedEvent(provider, selection);
        for (Object listener : selectionChangedListeners.getListeners()) {
            ((ISelectionChangedListener) listener).selectionChanged(event);
        }
    }
	
	protected void firePostSelectionChangedEvent(ISelection selection, ISelectionProvider provider) {
        SelectionChangedEvent event = new SelectionChangedEvent(provider, selection);
        for (Object listener : postSelectionChangedListeners.getListeners()) {
            ((ISelectionChangedListener) listener).selectionChanged(event);
        }
    }

	public AvroEditorContext getContext() {
		return context;
	}
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public void createControl(Composite parent) {
		
		if (schemaViewerConfiguration == null) {
			throw new IllegalStateException("No schema viewer configuration provided");
		}
		
		if (displayMode == DisplayMode.WITH_COLUMNS && attributeColumnConfiguration == null) {
			throw new IllegalStateException("Attribute Column Configuration is missing, you cannot use the WITH_COLUMNS display mode");
		}
		
		composite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		// top toolbars
		if (toolBarConfiguration != null) {
			
			String[] topToolBarIds = toolBarConfiguration.getTopToolBarIds();
			for (String toolBarId : topToolBarIds) {
				boolean hasTitle = toolBarConfiguration.hasTitle(toolBarId);
				if (hasTitle) {
					createToolBarManagerWithTitle(composite, toolBarConfiguration, toolBarId);
				} else {
					createToolBarManager(composite, toolBarConfiguration, toolBarId);
				}
				addSeparator();
			}
			
		}
		
		treeViewer = createTreeViewer(composite);
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// bottom toolbars
		if (toolBarConfiguration != null) {
			String[] bottomToolBarIds = toolBarConfiguration.getBottomToolBarIds();
			for (String toolBarId : bottomToolBarIds) {
				addSeparator();
				boolean hasTitle = toolBarConfiguration.hasTitle(toolBarId);
				if (hasTitle) {
					createToolBarManagerWithTitle(composite, toolBarConfiguration, toolBarId);
				} else {
					createToolBarManager(composite, toolBarConfiguration, toolBarId);
				}
			}
		}
		
		treeViewer.addPostSelectionChangedListener(postSelectionListenerForContext);		
		
		treeViewer.addSelectionChangedListener(internalSelectionListener);
		treeViewer.addPostSelectionChangedListener(internalPostSelectionListener);
		
		// configure Popup Menu
		PopupMenuConfiguration popupMenuConfiguration = schemaViewerConfiguration.getPopupMenuConfiguration(editor, context, displayMode);
        if (popupMenuConfiguration != null) {
            configurePopupMenu(popupMenuConfiguration);
        }
		
        // configure drag and drop
        DragAndDropConfiguration dragAndDropConfiguration = schemaViewerConfiguration.getDragAndDropConfiguration(editor, context, displayMode);
        if (dragAndDropConfiguration != null) {
        	configureDragAndDrop(dragAndDropConfiguration, treeViewer);
        }
        
        // add filters
        ViewerFilter[] viewerFilters = schemaViewerConfiguration.getViewerFilters(editor, context, displayMode);
        for (ViewerFilter filter : viewerFilters) {
        	treeViewer.addFilter(new SchemaViewerFilter(filter, nodeConverter));
        }
        
        // set comparator
        ViewerComparator comparator = schemaViewerConfiguration.getViewerComparator(editor, context, displayMode);
        if (comparator != null) {
        	treeViewer.setComparator(new SchemaViewerComparator(comparator, nodeConverter));
        }             
        
        updateDisplayMode(treeViewer, displayMode);
        
	}
	
	protected void updateDisplayMode(TreeViewer treeViewer, DisplayMode displayMode) {
		
		treeViewer.getTree().setLinesVisible(displayMode == DisplayMode.WITH_COLUMNS);
		treeViewer.getTree().setHeaderVisible(displayMode == DisplayMode.WITH_COLUMNS);
		
		if (displayMode == DisplayMode.WITH_COLUMNS) {
			
			if (attributeColumnConfiguration == null) {
				throw new IllegalStateException("No attribute column configuration provided");
			}
						
			String[] columnAttributeNames = attributeColumnConfiguration.getColumnAttributeNames();
			
			for (String attrName : columnAttributeNames) {
					
				final String attributeName = attrName;
				
				int width = attributeColumnConfiguration.getColumnWidth(attributeName);
				boolean resizable = attributeColumnConfiguration.isResizable(attributeName);				
				String title = attributeColumnConfiguration.getColumnTitle(attributeName);
				int style = attributeColumnConfiguration.getColumnStyle(attributeName);
				
				TreeViewerColumn viewerColumn = createTreeViewerColumn(treeViewer, title, width, resizable, style);						

				CellLabelProvider labelProvider = null;
				if (attributeColumnConfiguration.isMainColumn(attributeName)) {					
					labelProvider = new SchemaViewerStyledCellLabelProvider(schemaViewerConfiguration.getLabelProvider(editor, context, displayMode), nodeConverter);
				} else {
					final CellLabelProvider internalCellLabelProvider = 
							new SchemaViewerStyledCellLabelProvider(attributeColumnConfiguration.getColumnLabelProvider(attributeName), nodeConverter);
					labelProvider = new CellLabelProvider() {					
						@Override
						public void update(ViewerCell cell) {
							AvroNode node = nodeConverter.convertToAvroNode(cell.getElement());
							if (node.getAttributes().isDefined(attributeName)) {
								internalCellLabelProvider.update(cell);
							} else {
								cell.setText("");
								cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
							}
						}
					};
				}
				
				viewerColumn.setLabelProvider(labelProvider);
						
				SchemaViewerColumnEditingSupport editingSupport = attributeColumnConfiguration.getColumnEditingSupport(attributeName);
				if (editingSupport != null) {
					viewerColumn.setEditingSupport(new InternalEditingSupport(viewerColumn.getViewer(), editingSupport, nodeConverter));
				}
				
				columns.put(attributeName, viewerColumn);
			}
						
		} else {
			
			for (Map.Entry<String, TreeViewerColumn> entry : columns.entrySet()) {
				entry.getValue().getColumn().dispose();				
			}
			columns.clear();			
			
		}
		
		treeViewer.refresh();
		
	}
	
	protected TreeViewerColumn createTreeViewerColumn(TreeViewer treeViewer, String title, int width, boolean resizable, int style) {
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, style);
		column.getColumn().setText(title);			
		column.getColumn().setResizable(resizable);
		column.getColumn().setWidth(width);
		return column;
	} 
	
	protected void configurePopupMenu(final PopupMenuConfiguration popupMenuConfiguration) {
		MenuManager mgr = new MenuManager();
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				popupMenuConfiguration.fillPopupMenu(manager, SchemaViewer.this);
			}
		});           
        Menu menu = mgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
	}
	
	protected void configureDragAndDrop(DragAndDropConfiguration dragAndDropConfiguration, TreeViewer treeViewer) {
		
        // drop
        Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
        int operations = dragAndDropConfiguration.getSupportedDropOperations(this);
        
        DropTarget target = new DropTarget(treeViewer.getControl(), operations);
        target.setTransfer(transferTypes);

        // Drop listeners pour le drop des elements sur l'arbre
        SchemaViewerDropPolicy dropPolicy = dragAndDropConfiguration.getDropPolicy(this);
        DnDTargetListener targetListener = new DnDTargetListener(this, nodeConverter, dropPolicy);
        target.addDropListener(targetListener);

        // Drag listener pour le drag des elements de l'arbre
        DragSourceListener sourceListener = new DnDSourceListener(this);
        operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
        treeViewer.addDragSupport(operations, transferTypes, sourceListener);
        
    }

	protected void updateToolBarTitles(final AvroNode node) {
		for (Map.Entry<String, Label> entry : toolBarTitles.entrySet()) {
			Label titleLabel = entry.getValue();
			if (!titleLabel.isDisposed()) {
				String toolBarId = entry.getKey();
				updateToolBarTitle(node, toolBarId, titleLabel);
			}
		}
	}
	
	protected void updateToolBarTitle(final AvroNode node, final String toolBarId, final Label titleLabel) {
		UIUtils.runSyncOrAsync(new Runnable() {
			@Override
			public void run() {
				String title = toolBarConfiguration.getTitle(toolBarId, node);
				titleLabel.setText(title);
	            GC gc = new GC(titleLabel);
	            int width = getTextWidth(gc, title);
	            gc.dispose();

	            Point size = titleLabel.getSize();
	            if (size.x < width) {
	            	titleLabel.setSize(width, CHARACTER_HEIGHT);
	            	toolbarManagers.get(toolBarId).update(true);
	            }
			}
		});
	}

	protected final int getTextWidth(GC gc, String str) {
        int size = 0;
        char[] charArrayOfString = str.toCharArray();
        for (int i = 0; i < charArrayOfString.length; i++) {
            size += gc.getAdvanceWidth(charArrayOfString[i]);
        }
        return size;
    }
	
	protected void addSeparator() {
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	protected void createToolBarManager(Composite parent, ToolBarConfiguration toolBarConfiguration, String toolBarId) {
		ToolBarManager toolbarManager = createToolBar(parent, toolBarConfiguration.getToolBarStyle(toolBarId));
		toolBarConfiguration.fillToolBar(toolbarManager, toolBarId);
		toolbarManager.update(true);
		toolbarManagers.put(toolBarId, toolbarManager);
	}
	
	protected void createToolBarManagerWithTitle(Composite parent, ToolBarConfiguration toolBarConfiguration, String toolBarId) {
		
		Composite compo = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		compo.setLayoutData(new  GridData(GridData.FILL_HORIZONTAL));
		
		Label titleLabel = new Label(compo, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolBarTitles.put(toolBarId, titleLabel);
		
		createToolBarManager(compo, toolBarConfiguration, toolBarId);
	}
	
	protected boolean isBeginning(int style) {
		return (style & SWT.BEGINNING) != 0;
	}
	
	protected ToolBarManager createToolBar(Composite parent, int style) {
		ToolBarManager toolbarManager = new ToolBarManager(style);
		
		int gridDataStyle = isBeginning(style) ? GridData.HORIZONTAL_ALIGN_BEGINNING : GridData.HORIZONTAL_ALIGN_END;
		
        ToolBar toolbar = toolbarManager.createControl(parent);
        GridData toolbarLayoutdata = new GridData(gridDataStyle);
        toolbarLayoutdata.heightHint = 25;
        toolbar.setLayoutData(toolbarLayoutdata);
        
		return toolbarManager;
	}
	
	protected TreeViewer createTreeViewer(Composite parent) {
		
		TreeViewer viewer = new TreeViewer(parent, schemaViewerConfiguration.getTreeViewerStyle(editor, context, displayMode));		
		
		contentProvider = schemaViewerConfiguration.getContentProvider(editor, context, displayMode);
		schemaNodeRegistry.setSchemaContentProvider(contentProvider);
		viewer.setContentProvider(new SchemaViewerTreeContentProviderImpl(nodeConverter));
		SchemaViewerStyledCellLabelProvider labelProvider = 
				new SchemaViewerStyledCellLabelProvider(schemaViewerConfiguration.getLabelProvider(editor, context, displayMode), nodeConverter);
		viewer.setLabelProvider(labelProvider);
		
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		
		return viewer;
		
	}
	
	public void setContent(AvroNode inputNode) {
		isRootContent = ModelUtil.isRoot(inputNode);
		treeViewer.setInput(inputNode);
		if (isRootContent) {
			context.setRootNode((RootNode) inputNode);
		}
		context.setInputNode(inputNode);
		updateToolBarTitles(inputNode);
	}	
	
	public AvroNode getContent() {
		return (AvroNode) treeViewer.getInput();
	}	
	
	public void refresh() {
		treeViewer.getTree().setRedraw(false);
		try {
			treeViewer.refresh();
		} finally {
			treeViewer.getTree().setRedraw(true);
		}
	}
	
	public void reveal(Object object) {
		treeViewer.reveal(nodeConverter.convertToSchemaNode(object));
	}	
	
	@Override
	public void refresh(Object object) {	
		refresh();
		/*
		treeViewer.getTree().setRedraw(false);
		try {
			if (object == null || !isRootContent) {
				treeViewer.refresh();
			} else {
				List<AvroNode> nodesToRefresh = contentProvider.getNodesToRefresh(nodeConverter.convertToAvroNode(object));
				if (nodesToRefresh.isEmpty()) {
					// refresh all
					treeViewer.refresh();
				} else {
					for (AvroNode node : nodesToRefresh) {
						treeViewer.refresh(nodeConverter.convertToSchemaNode(node));
					}
				}
			}
		} finally {
			treeViewer.getTree().setRedraw(true);
		}
		*/
	}

	protected List<SchemaNode> getSchemaNodesToRefresh(AvroNode node) {
		List<SchemaNode> schemaNodes = new ArrayList<>();
		FieldNode fieldNode = ModelUtil.getFirstParentOfType(node, true, FieldNode.class);
		if (fieldNode != null) {
			
		}
		return schemaNodes;
	}
	
	@Override
	public void reveal(Object object, org.talend.avro.schema.editor.context.AvroContext.Kind context) {
		if (context == null || this.context.getKind() == context) {
			reveal(object);
		}
	}

	@Override
	public void select(Object object, org.talend.avro.schema.editor.context.AvroContext.Kind context) {
		if (this.context.getKind() == context) {
			setSelection(new StructuredSelection(object));
		}
	}
	
	@Override
	public void notify(Object object) {
		// nothing to do
	}

	@Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    @Override
	public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
    	postSelectionChangedListeners.add(listener);
	}

	@Override
    public ISelection getSelection() {
        return nodeConverter.convertOutgoingSelection((IStructuredSelection) treeViewer.getSelection());
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }
    
    @Override
	public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
    	postSelectionChangedListeners.remove(listener);
	}

	@Override
    public void setSelection(ISelection selection) {
        treeViewer.setSelection(nodeConverter.convertIncomingSelection((IStructuredSelection) selection));
    }
	
	public void setSelection(ISelection selection, boolean reveal) {
		treeViewer.setSelection(nodeConverter.convertIncomingSelection((IStructuredSelection) selection), reveal);
	}
	
    public Control getControl() {
    	return composite;
    }
    
	public void setFocus() {
		composite.setFocus();
	}
	
	public void dispose() {
		treeViewer.removePostSelectionChangedListener(postSelectionListenerForContext);				
		treeViewer.removeSelectionChangedListener(internalSelectionListener);
		treeViewer.removePostSelectionChangedListener(internalPostSelectionListener);
	}

}
