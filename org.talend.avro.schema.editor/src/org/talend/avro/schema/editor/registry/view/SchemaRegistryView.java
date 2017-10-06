package org.talend.avro.schema.editor.registry.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContextAdapter;
import org.talend.avro.schema.editor.context.AvroContextListener;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.services.NotificationObserver;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.NameSpaceRegistry;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.registry.cmd.AddNameSpaceAction;
import org.talend.avro.schema.editor.registry.cmd.NameSpaceAction;
import org.talend.avro.schema.editor.registry.cmd.RemoveNameSpaceAction;
import org.talend.avro.schema.editor.utils.TextCellEditorWithValidation;
import org.talend.avro.schema.editor.utils.UIUtils;
import org.talend.avro.schema.editor.viewer.SchemaTreeLabelProviderImpl;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class SchemaRegistryView extends ViewPart implements ISelectionProvider {

	public static final String ID = "org.talend.avro.schema.editor.registry.view"; //$NON-NLS-1$

	public static final String POPUP_MENU_ID = "popup:org.talend.avro.schema.editor.registry.view.tree"; //$NON-NLS-1$
	
	public enum DisplayMode {
		FLAT, HIERARCHICAL
	}
	
	private DisplayMode displayMode = DisplayMode.HIERARCHICAL;	
	
	private TreeViewer treeViewer;
	
	private Label nsLabel;
	
	private Button[] filterButtons;
	
	private boolean[] filters;
	
	private boolean showNameSpaceLabel = true;
	
	private boolean showUnusedNS = true;
	
	private Text searchText;
	
	private NotificationObserver notificationObserver;
	
	private AvroContextListener contextListener;
	
	private IPartListener partListener;
	
	private AvroSchemaEditor editor;
	
	private SchemaRegistry schemaRegistry;	
	
	private ListenerList selectionChangedListeners = new ListenerList();
	
	private ISelectionChangedListener internalSelectionListener;
	
	private SchemaTreeLabelProviderImpl labelProvider = null;
	
	private ViewerFilter patternFilter;
	
	private ViewerFilter typeFilter;
	
	private ViewerFilter unusedNSFilter;
	
	private String searchPattern = null;
	
	private ToolBarManager toolbarMgr;
	
	private NameSpaceAction[] actions;
	
	private void init() {
		internalSelectionListener = new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (editor != null) {
					updateToolbarOnSelectionChanged(event.getSelection());
					fireSelectionChangedEvent(event.getSelection(), SchemaRegistryView.this);
				}				
			}
		};
	}
	
	protected void fireSelectionChangedEvent(ISelection selection, ISelectionProvider provider) {
        SelectionChangedEvent event = new SelectionChangedEvent(provider, selection);
        for (Object listener : selectionChangedListeners.getListeners()) {
            ((ISelectionChangedListener) listener).selectionChanged(event);
        }
    }
	
	public void setDisplayMode(DisplayMode displayMode) {
		if (this.displayMode != displayMode) {
			this.displayMode = displayMode;
			updateDisplayMode(displayMode);
		}
	}
	
	protected void addSeparator(Composite compo) {
		Label separator = new Label(compo, SWT.SEPARATOR | SWT.VERTICAL);
		GridData layoutData = new GridData();
		layoutData.heightHint = 20;
		separator.setLayoutData(layoutData);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		init();
		
		int nsTypeSize = NodeType.NAMESPACED_NODE_TYPES.length;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		Composite topCompo = new Composite(composite, SWT.NONE);		
		GridLayout layout = new GridLayout(12 + nsTypeSize, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topCompo.setLayout(layout);
		topCompo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		nsLabel = new Label(topCompo, SWT.NONE);
		nsLabel.setText("   0 namespace(s)");
		nsLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		addSeparator(topCompo);
		
		toolbarMgr = new ToolBarManager(SWT.HORIZONTAL | SWT.FLAT | SWT.BEGINNING);
				
		actions = new NameSpaceAction[2];
		actions[0] = new AddNameSpaceAction();
		actions[1] = new RemoveNameSpaceAction();
		
		for (NameSpaceAction action : actions) {
			action.setContext(null);
			action.setNSNode(null);
			toolbarMgr.add(action);
		}
		
        ToolBar toolbar = toolbarMgr.createControl(topCompo);
        GridData toolbarLayoutdata = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        // toolbarLayoutdata.heightHint = 25;
        toolbar.setLayoutData(toolbarLayoutdata);             
		
        addSeparator(topCompo);
        
		Label filterLabel = new Label(topCompo, SWT.NONE);
		filterLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		filterLabel.setText("Filters:");
		
		filters = new boolean[nsTypeSize];		
		
		filterButtons = new Button[nsTypeSize];
		
		GridData layoutData = null;
		
		for (int i = 0; i < nsTypeSize; i++) {
			final int index = i;
			NodeType type = NodeType.NAMESPACED_NODE_TYPES[i];
			filters[i] = true;
			filterButtons[i] = new Button(topCompo, SWT.CHECK);
			filterButtons[i].setSelection(true);
			filterButtons[i].setText(type.getDefaultLabel());
			layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			layoutData.widthHint = 100;
			filterButtons[i].setLayoutData(layoutData);
			filterButtons[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					filters[index] = filterButtons[index].getSelection();
					UIUtils.refresh(treeViewer, ID);
				}
				
			});
		}
		
		updateButtonLabels();
		
		addSeparator(topCompo);
		
		final Button showNSLabelButton = new Button(topCompo, SWT.CHECK);
		showNSLabelButton.setText("Show namespace label");
		showNSLabelButton.setSelection(showNameSpaceLabel);
		showNSLabelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		showNSLabelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showNameSpaceLabel = showNSLabelButton.getSelection();
				UIUtils.refresh(treeViewer, ID);
			} 
			
		});
		
		final Button showUnusedNSButton = new Button(topCompo, SWT.CHECK);
		showUnusedNSButton.setText("Show unused NS");
		showUnusedNSButton.setSelection(showUnusedNS);
		showUnusedNSButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		showUnusedNSButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showUnusedNS = showUnusedNSButton.getSelection();
				UIUtils.refresh(treeViewer, ID);
			} 
			
		});
		
		addSeparator(topCompo);
		
		Label searchLabel = new Label(topCompo, SWT.NONE);
		layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		searchLabel.setText("Search:");
		
		searchText = new Text(topCompo, SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        searchText.setLayoutData(layoutData);
        
        searchText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent modifyEvent) {            	                      
            	searchPattern = searchText.getText();
            	if (editor != null) {
            		UIUtils.refresh(treeViewer, ID);
            	}
            }
            
        });
		
        Button clearButton = new Button(topCompo, SWT.PUSH);
        clearButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        clearButton.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.CLEAR));
        clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				searchPattern = null;
				searchText.setText("");
				if (editor != null) {
					treeViewer.refresh();
				}
			}
        	
		});
        
		treeViewer = new TreeViewer(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
                treeViewer) {
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL)
                        || (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
                        || ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) && (event.keyCode == SWT.CR))
                        || (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC);
            }
        };

        TreeViewerEditor.create(treeViewer, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		TreeViewerColumn column = createTreeViewerColumn("Name", 300, SWT.LEFT);
		column.setLabelProvider(new StyledCellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();
				if (element instanceof AvroNode) {
					AvroNode node = (AvroNode) element;
					cell.setText(getLabel(node));
					Image image = getImage(node);
					if (image != null) {
						cell.setImage(image);
					}
					cell.setStyleRanges(getStyleRanges(node));
				} else if (element instanceof NSNode) {
					NSNode nsNode = (NSNode) element;
					cell.setText(nsNode.getName());
					cell.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.NAME_SPACE));
					cell.setStyleRanges(getStyleRanges(nsNode));
				}
				super.update(cell);
			}
			
		});
		column.setEditingSupport(new EditingSupport(treeViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				NSNode nsNode = (NSNode) element;
				String newName = (String) value;
				if (!nsNode.getName().trim().equals(newName.trim())) {
					IEditCommandFactory commandFactory = editor.getServiceProvider().getService(IEditCommandFactory.class);
					IEditCommand cmd = commandFactory.createRenameNameSpaceCommand(nsNode, newName, Notifications.NOT_REF);
					editor.getServiceProvider().getService(ICommandExecutor.class).execute(cmd);
				}
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof NSNode) {
					return ((NSNode) element).getName();
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element instanceof NSNode) {
					final NSNode node = (NSNode) element;
					TextCellEditor textCellEditor = new TextCellEditorWithValidation(
	                        (Composite) treeViewer.getControl());
					textCellEditor.setValidator(new ICellEditorValidator() {
	                    @Override
	                    public String isValid(Object value) {	                    	
	                        String name = (String) value;
	                        return editor.getContext().getSchemaRegistry().getNameSpaceRegistry().validateNameSpace(node, name);
	                    }
	                });
					return textCellEditor;
				}
				return null;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof NSNode && editor != null;
			}
			
		});
		
		column = createTreeViewerColumn("Nbr of references", 150, SWT.CENTER);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();
				if (element instanceof AvroNode) {
					AvroNode node = (AvroNode) element;
					SchemaRegistry schemaRegistry = editor.getContext().getSchemaRegistry();
					cell.setText(Integer.toString(schemaRegistry.getNbrOfReferences(node) + 1));
				} else {
					cell.setText("");
				}
			}
			
		});
		
		
		column = createTreeViewerColumn("Doc", 600, SWT.CENTER);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();
				if (element instanceof AvroNode) {
					AvroNode node = (AvroNode) element;
					if (AttributeUtil.hasDocAttribute(node)) {
						cell.setText(AttributeUtil.getDoc(node));
					} else {
						cell.setText("");
					}
				} else {
					cell.setText("");
				}
			}
			
		});
		
		updateDisplayMode(displayMode);
		
		treeViewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof AvroNode && e2 instanceof AvroNode) {
					AvroNode node1 = (AvroNode) e1;
					AvroNode node2 = (AvroNode) e2;
					String fullName1 = AttributeUtil.getFullName(node1);
					String fullName2 = AttributeUtil.getFullName(node2);
					return super.compare(viewer, fullName1, fullName2);
				}
				return super.compare(viewer, e1, e2);
			}

			@Override
			public int category(Object element) {
				if (element instanceof NSNode) {
					return 2;
				} else if (element instanceof AvroNode) {
					return 1;
				}
				return super.category(element);
			} 			
			
		});
		
		patternFilter = new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof NSNode) {
					return true;
				}
				if (searchPattern != null && !searchPattern.trim().isEmpty()) {
					AvroNode node = (AvroNode) element;
					return isTextMatchingSearchPattern(AttributeUtil.getNameFromAttribute(node))
							|| (AttributeUtil.hasDocAttribute(node) && isTextMatchingSearchPattern(AttributeUtil.getDoc(node)));
				}
				return true;
			}
			
		};
		
		typeFilter = new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof NSNode) {
					return true;
				}
				AvroNode node = (AvroNode) element;
				NodeType type = node.getType();		
				int index = NodeType.indexOf(NodeType.NAMESPACED_NODE_TYPES, type);
				if (index != -1) {
					return filters[index];
				}
				return true;
			}
			
		};
		
		unusedNSFilter = new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof AvroNode) {
					return true;
				}
				NSNode nsNode = (NSNode) element;
				return showUnusedNS || schemaRegistry.getNameSpaceRegistry().isUsed(nsNode);				
			}
			
		}; 
		
		treeViewer.addFilter(patternFilter);
		treeViewer.addFilter(typeFilter);
		treeViewer.addFilter(unusedNSFilter);
		
		treeViewer.setInput(null);
		
		treeViewer.addSelectionChangedListener(internalSelectionListener);
		
		notificationObserver = new NotificationObserver() {
			
			@Override
			public void refresh(Object object) {
				update();
			}
			
			@Override
			public void refresh() {				
				update();
			}			

			@Override
			public void notify(Object object) {
				// nothing to do
			}

		};
		
		contextListener = new AvroContextAdapter() {

			@Override
			public void onRootNodeChanged(AvroContext context, RootNode rootNode) {
				update();
			}
			
		};
		
		partListener = new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part instanceof IWithAvroSchemaEditor) {
					AvroSchemaEditor closedEditor = ((IWithAvroSchemaEditor) part).getEditor();
					if (closedEditor == editor) {
						unlinkEditor(false);					
					}
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof IWithAvroSchemaEditor) {
					AvroSchemaEditor activatedEditor = ((IWithAvroSchemaEditor) part).getEditor();
					if (editor != null && editor != activatedEditor) {
						unlinkEditor(false);						
					}
					if (editor == null) {
						linkEditor(activatedEditor);
					}
				}
			}
		};
		
		addDragSupport(treeViewer);
		addDropSupport(treeViewer);
		
		configurePopupMenu(treeViewer);
		
		getSite().getPage().addPartListener(partListener);
		
	}
	
	protected String getLabel(AvroNode node) {
		return showNameSpaceLabel ? AttributeUtil.getFullName(node) : AttributeUtil.getTrueName(node); 
	}
	
	protected StyleRange[] getStyleRanges(AvroNode node) {
		String label = getLabel(node);
		Color foregroundColor = Display.getDefault().getSystemColor(SWT.DEFAULT);
		Color backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		StyleRange nodeStyle = new StyleRange(0, label.length(), foregroundColor, backgroundColor);
		return new StyleRange[] { nodeStyle };
	}
	
	protected StyleRange[] getStyleRanges(NSNode nsNode) {
		String name = nsNode.getName();
		Color foregroundColor = Display.getDefault().getSystemColor(SWT.DEFAULT);
		Color backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		Color grayColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);		
		boolean used = schemaRegistry.getNameSpaceRegistry().isUsed(nsNode);
		StyleRange nsNodeStyle = null;
		if (used) {
			nsNodeStyle = new StyleRange(0, name.length(), foregroundColor, backgroundColor);			
		} else {
			nsNodeStyle = new StyleRange(0, name.length(), grayColor, backgroundColor);
			nsNodeStyle.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
		}		
		return new StyleRange[] { nsNodeStyle };
	}
	
	protected void updateDisplayMode(final DisplayMode displayMode) {
		UIUtils.run(treeViewer, new Runnable() {			
			@Override
			public void run() {
				treeViewer.setContentProvider(getTreeContentProvider(displayMode));
			}
		}, ID);		
	}
	
	protected ITreeContentProvider getTreeContentProvider(DisplayMode displayMode) {
		switch (displayMode) {
		case HIERARCHICAL:
			return new ITreeContentProvider() {
				
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					
				}
				
				@Override
				public void dispose() {
					
				}
				
				@Override
				public boolean hasChildren(Object element) {				
					if (element instanceof NSNode) {
						return true;
					}
					return false;
				}
				
				@Override
				public Object getParent(Object element) {
					if (element instanceof AvroNode) {
						AvroNode node = (AvroNode) element;
						String nameSpace = AttributeUtil.getTrueNameSpace(node);
						if (nameSpace != null && !nameSpace.trim().isEmpty()) {
							return schemaRegistry.getNameSpaceRegistry().getNameSpaceNode(nameSpace);
						} else {
							// the root is the parent
							return schemaRegistry.getNameSpaceTree();
						}
					} else if (element instanceof NSNode) {
						return ((NSNode) element).getParent();
					}
					return null;
				}
				
				@Override
				public Object[] getElements(Object inputElement) {				
					return getChildren(inputElement);
				}
				
				@Override
				public Object[] getChildren(Object parentElement) {				
					if (parentElement instanceof NSNode) {
						NSNode nsNode = (NSNode) parentElement;
						List<Object> children = new ArrayList<>();
						children.addAll(nsNode.getChildren());									
						String nameSpace = schemaRegistry.getNameSpaceRegistry().getNameSpace(nsNode);
						children.addAll(schemaRegistry.getNodesFromNameSpace(nameSpace));
						return children.toArray();
					}
					return new Object[0];
				}
			};
		case FLAT:
			return new ITreeContentProvider() {

				@Override
				public void dispose() {
					
				}

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					
				}

				@Override
				public Object[] getElements(Object inputElement) {
					return schemaRegistry.getAllRegisteredNodes().toArray();
				}

				@Override
				public Object[] getChildren(Object parentElement) {
					return new Object[0];
				}

				@Override
				public Object getParent(Object element) {
					return null;
				}

				@Override
				public boolean hasChildren(Object element) {
					return false;
				}
				
			};
		}
		return null;
	}
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	protected void fillPopupMenu(IMenuManager manager) {
		IMenuService service = (IMenuService) editor.getServiceProvider().getMenuService();
        service.populateContributionManager((ContributionManager) manager, POPUP_MENU_ID);
	}
	
	protected void configurePopupMenu(TreeViewer treeViewer) {
		MenuManager mgr = new MenuManager();
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fillPopupMenu(manager);
			}
		});           
        Menu menu = mgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
	}
	
	protected void addDragSupport(final TreeViewer treeViewer) {
		DragSourceListener dragListener = new DragSourceListener() {

            @Override
            public void dragFinished(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(null);
            }

            @Override
            public void dragSetData(DragSourceEvent event) {
                if (treeViewer.getSelection() instanceof IStructuredSelection) {
                    LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
                    IStructuredSelection ss = (IStructuredSelection) treeViewer.getSelection();
                    Object[] objects = ss.toArray();
                    transfer.setSelection(new StructuredSelection(objects));
                    event.data = transfer.getSelection();
                }
            }

            @Override
            public void dragStart(DragSourceEvent event) {
                if (treeViewer.getSelection() instanceof IStructuredSelection) {
                    event.doit = !treeViewer.getSelection().isEmpty();
                    dragSetData(event);
                }
            }
        };
        Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        treeViewer.addDragSupport(operations, transferTypes, dragListener);
	}
		
	protected void addDropSupport(final TreeViewer treeViewer) {		
		
		DropTargetListener dropListener = new DropTargetListener() {
			
			@Override
			public void dropAccept(DropTargetEvent event) {
				
			}
			
			@Override
			public void drop(DropTargetEvent event) {
				
				IStructuredSelection structuredSelection = (IStructuredSelection) LocalSelectionTransfer
		                .getTransfer().getSelection();
				
				TreeItem item = (TreeItem) event.item;
				
				if (item != null && !structuredSelection.isEmpty()) {
					
					NSNode targetNode = (NSNode) item.getData();
				
					IEditCommandFactory commandFactory = getEditor().getServiceProvider().getService(IEditCommandFactory.class);
					ICommandExecutor cmdExecutor = getEditor().getServiceProvider().getService(ICommandExecutor.class);
					
					if (structuredSelection.size() == 1) {

						AvroNode sourceNode = (AvroNode) structuredSelection.getFirstElement();
						
						IEditCommand cmd = commandFactory.createChangeNameSpaceCommand(sourceNode, targetNode, Notifications.NOT_REF);						
						cmdExecutor.execute(cmd);										
						
					} else {
						
						@SuppressWarnings("unchecked")
						List<Object> selectedElements = structuredSelection.toList();
						SchemaEditCompositeCommand compositeCommand =
								commandFactory.createCompositeCommand("Multi change name space", Notifications.NOT_REF);
						
						for (Object element : selectedElements) {
							
							if (element instanceof AvroNode) {
								AvroNode node = (AvroNode) element;
								IEditCommand cmd = commandFactory.createChangeNameSpaceCommand(node, targetNode, Notifications.NONE);
								compositeCommand.addCommand(cmd);								
							}
							
						}
						
						if (!compositeCommand.isEmpty()) {
							cmdExecutor.execute(compositeCommand);
						}
						
					}
					
				}								
				
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		        
				IStructuredSelection structuredSelection = (IStructuredSelection) LocalSelectionTransfer
		                .getTransfer().getSelection();
				
				if (structuredSelection.size() >= 1) {
					
					Object element = structuredSelection.getFirstElement();	
					
					if (element instanceof AvroNode) {
						
						AvroNode sourceNode = (AvroNode) element;
						
						TreeItem item = (TreeItem) event.item;
						
						if (item != null) {
							
							Object data = item.getData();
							
							if (data instanceof NSNode) {
							
								NSNode targetNSNode = (NSNode) data;
								
								String sourceNameSpace = AttributeUtil.getTrueNameSpace(sourceNode);
								NameSpaceRegistry nameSpaceRegistry = getEditor().getContext().getSchemaRegistry().getNameSpaceRegistry();
								String targetNameSpace = nameSpaceRegistry.getNameSpace(targetNSNode);
								
								if (!targetNameSpace.equals(sourceNameSpace)) {
								
									Point pt = treeViewer.getTree().toControl(event.x, event.y);
									Rectangle bounds = item.getBounds();

									if (pt.y >= bounds.y && pt.y <=  bounds.y + bounds.height) {									
										event.feedback |= DND.FEEDBACK_SELECT;
										event.detail = DND.DROP_MOVE;
									}
									
								} else {
									event.detail = DND.DROP_NONE;
								}
								
							} else {
								event.detail = DND.DROP_NONE;
							}
						}
					} else {
						event.detail = DND.DROP_NONE;
					}
		            
				}
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
				
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {
				
			}
		};		
		
		Transfer[] transferTypes =  new Transfer[] { LocalSelectionTransfer.getTransfer() };
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        
        DropTarget target = new DropTarget(treeViewer.getControl(), operations);
        target.setTransfer(transferTypes);
        
        target.addDropListener(dropListener);
	}
	
	protected boolean isTextMatchingSearchPattern(String text) {
        SearchPattern matcher = new SearchPattern(SearchPattern.RULE_PATTERN_MATCH
                | SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_PREFIX_MATCH
                | SearchPattern.RULE_BLANK_MATCH);
        matcher.setPattern("*" + searchPattern);
        return matcher.matches(text);
    }
	
	protected Image getImage(AvroNode node) {
		if (labelProvider != null) {
			return labelProvider.getImage(node);
		}
		return null;
	}
		
	protected void linkEditor(AvroSchemaEditor editor) {
		
		this.editor = editor;
		this.schemaRegistry = editor.getContext().getSchemaRegistry();
		
		labelProvider = new SchemaTreeLabelProviderImpl(editor.getServiceProvider(), org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode.WITHOUT_COLUMNS);
		
		NotificationService notificationService = editor.getServiceProvider().getService(NotificationService.class);
		notificationService.addObserver(notificationObserver);
		
		editor.getContext().addContextListener(contextListener);
		
		// get namespace tree
		NSNode nameSpaceTree = this.schemaRegistry.getNameSpaceTree();
		treeViewer.setInput(nameSpaceTree);
		
		updateToolbarOnContextChanged();
		
		update();
		
	}
	
	protected void updateToolbarOnContextChanged() {
		AvroContext context = null;
		if (editor != null) {
			context = editor.getContext();		
		}
		for (NameSpaceAction action : actions) {
			action.setContext(context);				
		}
		toolbarMgr.update(true);
	}
	
	protected void updateToolbarOnSelectionChanged(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		NSNode selectedNode = null;
		if (sel.isEmpty()) {
			selectedNode = schemaRegistry.getNameSpaceTree();
		} else {
			Object element = sel.getFirstElement();
			if (element instanceof NSNode) {
				selectedNode = (NSNode) element;
			}
		}		
		for (NameSpaceAction action : actions) {
			action.setNSNode(selectedNode);				
		}
		toolbarMgr.update(true);
	}
	
	protected void unlinkEditor(boolean onDispose) {
		
		NotificationService notificationService = editor.getServiceProvider().getService(NotificationService.class);
		notificationService.removeObserver(notificationObserver);
		
		editor.getContext().removeContextListener(contextListener);
		
		labelProvider.dispose();
		labelProvider = null;
		
		editor = null;
		schemaRegistry = null;
		
		if (!onDispose) {
			// clear the selection
			treeViewer.setSelection(new StructuredSelection());
			// then clear the tree
			treeViewer.setInput(null);
			updateToolbarOnContextChanged();
			update();
		}
		
	}
	
	protected List<AvroNode> getRegisteredNodes(NodeType type) {
		if (editor != null) {
			return editor.getContext().getSchemaRegistry().getAllRegisteredNodes(type);
		}
		return Collections.emptyList();
	}
	
	protected int getRegisteredNodeCount(NodeType type) {
		if (editor != null) {
			return editor.getContext().getSchemaRegistry().getRegisteredNodeCount(type);
		}
		return 0;
	}	
	
	protected void update() {
		treeViewer.refresh();		
		updateButtonLabels();
		updateNSLabel();
	}
	
	protected void updateButtonLabels() {
		for (int i = 0; i < filterButtons.length; i++) {
			NodeType type = NodeType.NAMESPACED_NODE_TYPES[i];
			int count = getRegisteredNodeCount(type);
			filterButtons[i].setText(type.getDefaultLabel() + " [" + count + "]");
		}
	}
	
	protected void updateNSLabel() {
		if (schemaRegistry != null) {
			int nameSpaceCount = schemaRegistry.getNameSpaceRegistry().getNameSpaceCount();
			nsLabel.setText(nameSpaceCount + " namespace(s)");
		} else {
			nsLabel.setText("0 namespace(s)");
		}
	}
	
	protected int getSelectedNodeTypeIndex(NodeType type) {
		for (int i = 0; i < NodeType.REGISTERED_NODE_TYPES.length; i++) {
			if (NodeType.REGISTERED_NODE_TYPES[i] == type) {
				return i;
			}
		}
		return -1;
	}
	
	private TreeViewerColumn createTreeViewerColumn(String name, int width, int style) {
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, style);
		column.getColumn().setText(name);			
		column.getColumn().setResizable(true);
		column.getColumn().setWidth(width);
		return column;
	}
	
	public AvroSchemaEditor getEditor() {
		return editor;
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
	
	@Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    @Override
    public ISelection getSelection() {
        return treeViewer.getSelection();
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        treeViewer.setSelection(selection);
    }
	
	@Override
	public void dispose() {
		treeViewer.removeSelectionChangedListener(internalSelectionListener);
		getSite().getPage().removePartListener(partListener);
		if (editor != null) {
			unlinkEditor(true);
		}		
		super.dispose();
	}
	
}
