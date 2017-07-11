package org.talend.avro.schema.editor.debug;


import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.attributes.AttributesConfiguration;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContextAdapter;
import org.talend.avro.schema.editor.context.AvroContextListener;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.EditorLayout;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.edit.services.NotificationObserver;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeContentProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControlProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;

/**
 * Model debug view.
 * 
 * @author timbault
 *
 */
public class AvroSchemaEditorDebugView extends ViewPart implements ISelectionProvider {

	public static final String ID = "org.talend.avro.schema.editor.debug.AvroSchemaEditorDebugView"; //$NON-NLS-1$
	
	private AvroSchemaEditor editor;
	
	private TreeViewer treeViewer;
	
	private NotificationObserver notificationObserver;
	
	private AvroContextListener contextListener;
	
	private IPartListener partListener;
	
	private AttributeViewer attributeViewer;
	
	private ISelectionChangedListener schema2AattributeSelectionListener;
	
	private ListenerList selectionChangedListeners = new ListenerList();
	
	private Button displayModeButton;
	
	private Combo editorLayoutCombo;
	
	@Override
	public void createPartControl(Composite parent) {
		
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		
		// 1) model part
		
		Composite modelTreeComposite = new Composite(sashForm, SWT.NONE);
		modelTreeComposite.setLayout(new FillLayout());
		
		treeViewer = new TreeViewer(modelTreeComposite, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeViewerColumn column = createTreeViewerColumn("Node", 600);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				AvroNode node = (AvroNode) cell.getElement();
				cell.setText(AttributeUtil.getNameFromAttribute(node));
			}
		});
		
		column = createTreeViewerColumn("Type", 100);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				AvroNode node = (AvroNode) cell.getElement();
				cell.setText(node.getType().toString());
			}
		});
		
		treeViewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public boolean hasChildren(Object element) {				
				return ((AvroNode)element).hasChildren();
			}
			
			@Override
			public Object getParent(Object element) {
				return ((AvroNode)element).getParent();
			}
			
			@Override
			public Object[] getElements(Object inputElement) {				
				return getChildren(inputElement);
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {				
				return ((AvroNode)parentElement).getChildren().toArray();
			}
		});
		
		// 2) attributes part
		
		Composite attributeComposite = new Composite(sashForm, SWT.NONE);
		attributeComposite.setLayout(new FillLayout());
		
		attributeViewer = new AttributeViewer();	
		attributeViewer.setAttributeControlProvider(new DummyAttributeControlProvider());
		attributeViewer.setContentProvider(new DummyAttributeContentProvider());
		
		attributeViewer.createControl(attributeComposite);
		
		// 3) Parameters part
		
		Composite parametersComposite = new Composite(sashForm, SWT.BORDER);
		parametersComposite.setLayout(new GridLayout());
		
		displayModeButton = new Button(parametersComposite, SWT.CHECK);		
		displayModeButton.setText("Display attributes in columns (only in master tree viewer)");
		displayModeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		displayModeButton.setEnabled(false);
		displayModeButton.setSelection(false);
		displayModeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (editor != null) {
					SchemaViewer schemaViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
					schemaViewer.setDisplayMode(displayModeButton.getSelection() ? DisplayMode.WITH_COLUMNS : DisplayMode.WITHOUT_COLUMNS);
				}
			}		
		});		
		
		editorLayoutCombo = new Combo(parametersComposite, SWT.READ_ONLY);
		editorLayoutCombo.setItems(EditorLayout.getValuesAsString());
		editorLayoutCombo.setEnabled(false);
		editorLayoutCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editorLayoutCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (editor != null) {
					editor.getContentPart().setEditorLayout(EditorLayout.getEditorLayout(editorLayoutCombo.getSelectionIndex()));
				}
			}		
		});
		
		sashForm.setWeights(new int[] { 5, 2, 2 });
		
		notificationObserver = new NotificationObserver() {						
			
			@Override
			public void refresh(Object object) {
				treeViewer.refresh();
				attributeViewer.update();
			}
			
			@Override
			public void refresh() {				
				treeViewer.refresh();
				attributeViewer.update();
			}

			@Override
			public void notify(Object object) {
				// nothing to do
			}
				
		};
		
		contextListener = new AvroContextAdapter() {

			@Override
			public void onRootNodeChanged(AvroContext context, RootNode rootNode) {
				treeViewer.setInput(rootNode);
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
						unlinkEditor();					
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
						unlinkEditor();						
					}
					if (editor == null) {
						linkEditor(activatedEditor);
					}
				}
			}
		};
		
		getSite().getPage().addPartListener(partListener);
		
	}
	
	public TreeViewer getViewer() {
		return treeViewer;
	}
	
	public void globalRefresh() {
		if (editor != null) {
			NotificationService notificationService = editor.getServiceProvider().getService(NotificationService.class);
			notificationService.refresh();
		}
	}
	
	public void synchronizeSelection() {
		if (editor != null) {
			SchemaViewer masterViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
			IStructuredSelection selection = (IStructuredSelection) masterViewer.getSelection();
			if (!selection.isEmpty()) {
				AvroNode node = (AvroNode) selection.getFirstElement();
				treeViewer.setSelection(new StructuredSelection(node), true);				
			}			
		}
	}

	private TreeViewerColumn createTreeViewerColumn(String name, int width) {
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.CENTER);
		column.getColumn().setText(name);			
		column.getColumn().setResizable(true);
		column.getColumn().setWidth(width);
		return column;
	}
	
	protected void linkEditor(AvroSchemaEditor editor) {
		
		// parameters
		final SchemaViewer schemaViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
		displayModeButton.setSelection(schemaViewer.getDisplayMode() == DisplayMode.WITH_COLUMNS);
		displayModeButton.setEnabled(true);
		
		EditorLayout editorLayout = editor.getContentPart().getEditorLayout();
		editorLayoutCombo.select(editorLayout.getIndex());
		editorLayoutCombo.setEnabled(true);
		
		// link editor
		this.editor = editor;
		
		// configure
		NotificationService notificationService = editor.getServiceProvider().getService(NotificationService.class);
		notificationService.addObserver(notificationObserver);
		
		AvroContext context = editor.getContext();
		treeViewer.setInput(context.getRootNode());
		context.addContextListener(contextListener);
		
		AttributesConfiguration attributesConfiguration = editor.getContentPart().getAttributesConfiguration();
		AttributeViewerConfiguration attributeViewerConfiguration = attributesConfiguration.getAttributeViewerConfiguration(context);
		
		attributeViewer.setContentProvider(attributeViewerConfiguration.getAttributeContentProvider());
		attributeViewer.setAttributeControlProvider(new AttributeControlProviderProxy(attributeViewerConfiguration.getAttributeControlProvider()));
		attributeViewer.setComparator(attributeViewerConfiguration.getAttributeComparator());
		
		linkAttributeViewerToSchemaViewer(attributeViewer, treeViewer);
		
	}
	
	protected void linkAttributeViewerToSchemaViewer(final AttributeViewer attributeViewer, TreeViewer treeViewer) {
		if (schema2AattributeSelectionListener == null) {
			schema2AattributeSelectionListener = new ISelectionChangedListener() {			
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					ISelection selection = event.getSelection();
					Object selectedNode = null;
					if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
						selectedNode = ((IStructuredSelection)selection).getFirstElement();
					}					
					attributeViewer.setInput(selectedNode, editor.getContext());
				}
			};
		}
		treeViewer.addPostSelectionChangedListener(schema2AattributeSelectionListener);	
	}
	
	protected void unlinkAttributeViewerToSchemaViewer(final AttributeViewer attributeViewer, TreeViewer treeViewer) {
		treeViewer.removeSelectionChangedListener(schema2AattributeSelectionListener);
	}
	
	protected void unlinkEditor() {
		
		NotificationService notificationService = editor.getServiceProvider().getService(NotificationService.class);
		notificationService.removeObserver(notificationObserver);
		
		editor.getContext().removeContextListener(contextListener);
		
		unlinkAttributeViewerToSchemaViewer(attributeViewer, treeViewer);
		
		if (!treeViewer.getControl().isDisposed()) {
			treeViewer.setInput(null);
		}
				
		editor = null;
		
		// parameters
		
		if (!displayModeButton.isDisposed()) {
			displayModeButton.setSelection(false);
			displayModeButton.setEnabled(false);
		}
		
		if (!editorLayoutCombo.isDisposed()) {
			editorLayoutCombo.setEnabled(false);
		}
		
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
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		if (editor != null) {
			unlinkEditor();
		}		
		super.dispose();
	}

	private static class AttributeControlProviderProxy implements AttributeControlProvider {
		
		private AttributeControlProvider attrUIProvider;

		public AttributeControlProviderProxy(AttributeControlProvider attrUIProvider) {
			super();
			this.attrUIProvider = attrUIProvider;
		}

		@Override
		public Layout getMainLayout() {
			return attrUIProvider.getMainLayout();
		}

		@Override
		public AttributeControl<?> getAttributeControl(AvroAttribute<?> attribute) {
			return attrUIProvider.getAttributeControl(attribute);
		}

		@Override
		public Object getLayoutData(AvroAttribute<?> attribute) {
			return attrUIProvider.getLayoutData(attribute);
		}

		@Override
		public AttributeControlConfiguration getAttributeControlConfiguration(AvroAttribute<?> attribute) {
			return attrUIProvider.getAttributeControlConfiguration(attribute);
		}

		@Override
		public boolean isVisible(AvroAttribute<?> attribute) {
			return true;
		}				
		
	}
	
	private static class DummyAttributeControlProvider implements AttributeControlProvider {

		@Override
		public Layout getMainLayout() {
			return new FillLayout();
		}

		@Override
		public AttributeControl<?> getAttributeControl(AvroAttribute<?> attribute) {
			return null;
		}

		@Override
		public Object getLayoutData(AvroAttribute<?> attribute) {
			return null;
		}

		@Override
		public AttributeControlConfiguration getAttributeControlConfiguration(AvroAttribute<?> attribute) {
			return null;
		}

		@Override
		public boolean isVisible(AvroAttribute<?> attribute) {
			return false;
		}		
		
	}

	private static class DummyAttributeContentProvider implements AttributeContentProvider {

		@Override
		public AvroAttribute<?>[] getAttributes(Object inputElement) {
			return new AvroAttribute<?>[0];
		}
		
	}
	
}
