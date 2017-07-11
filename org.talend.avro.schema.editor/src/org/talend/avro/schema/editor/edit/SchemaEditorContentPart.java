package org.talend.avro.schema.editor.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.avro.schema.editor.attributes.AttributesConfiguration;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroEditorContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.view.AttributeViewService;

/**
 * This class is responsible for creation of the GUI of the {@link AvroSchemaEditor}.
 * <p>
 * @author timbault
 * @see AvroSchemaEditor
 *
 */
public class SchemaEditorContentPart implements ISelectionProvider {

	private AvroSchemaEditor editor;
	
	private AvroEditorContext masterContext;
	
	private AvroEditorContext slaveContext;
		
	private Composite mainComposite;
	
	private SashForm sashForm;
	
	private static final int[] ONE_PART_WEIGHTS = new int[] { 1 };
	
	private static final int[] TWO_PARTS_WEIGHTS = new int[] { 1, 1 };
	
	private static final int[] THREE_PARTS_WEIGHTS = new int[] { 1, 1, 1 };
	
	private Composite leftComposite;
	
	private Composite middleComposite;
	
	private Composite rightComposite;	
	
	private SchemaViewer masterViewer;
	
	private SchemaViewer slaveViewer;

	private AttributeViewer attributeViewer;
	
	private SchemaViewerConfiguration schemaViewerConfiguration;
	
	private AttributesConfiguration attributesConfiguration;
	
	private ListenerList selectionChangedListeners = new ListenerList();
	
	private ISelectionChangedListener internalSelectionListener;
	
	private EditorLayout editorLayout = EditorLayout.TREE_AND_ATTRIBUTES;
	
	private ISelectionChangedListener master2slaveSelectionListener;
	
	private ISelectionChangedListener schema2AattributeSelectionListener;
	
	private ISelectionChangedListener activeSchemaViewerListener;
	
	private AvroContext.Kind activeContext = Kind.MASTER;
	
	private boolean slavePinned = false;
	
	public SchemaEditorContentPart(AvroSchemaEditor editor, AvroEditorContext masterContext, AvroEditorContext slaveContext) {
		super();
		this.editor = editor;		
		this.masterContext = masterContext;
		this.slaveContext = slaveContext;
		init();
	}	
	
	private void init() {
		internalSelectionListener = new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fireSelectionChangedEvent(getSelection(), SchemaEditorContentPart.this);
			}
		};
	}	
	
	public EditorLayout getEditorLayout() {
		return editorLayout;
	}

	public void setEditorLayout(EditorLayout editorLayout) {
		if (this.editorLayout != editorLayout) {
			this.editorLayout = editorLayout;
			if (sashForm != null && !sashForm.isDisposed()) {
				updateEditorLayout(editorLayout);
			}
		}
	}	
	
	public void createContentPart(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());

		sashForm = new SashForm(mainComposite, SWT.HORIZONTAL);

		leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new FillLayout());

		masterViewer = new SchemaViewer(editor, masterContext);
		masterViewer.setSchemaViewerConfiguration(schemaViewerConfiguration);
		masterViewer.setAttributeColumnConfiguration(attributesConfiguration.getAttributeColumnConfiguration(masterContext));
		masterViewer.createControl(leftComposite);
		masterViewer.addSelectionChangedListener(internalSelectionListener);		

		middleComposite = new Composite(sashForm, SWT.NONE);
		middleComposite.setLayout(new FillLayout());

		attributeViewer = new AttributeViewer();

		AttributeViewerConfiguration attributeViewerConfiguration = attributesConfiguration.getAttributeViewerConfiguration(masterContext);
		attributeViewer.setContentProvider(attributeViewerConfiguration.getAttributeContentProvider());
		attributeViewer.setAttributeControlProvider(attributeViewerConfiguration.getAttributeControlProvider());
		attributeViewer.setComparator(attributeViewerConfiguration.getAttributeComparator());

		attributeViewer.createControl(middleComposite);

		linkAttributeViewerToSchemaViewer(attributeViewer, masterViewer);
		registerActiveSchemaViewerListener(masterViewer);

		sashForm.setWeights(TWO_PARTS_WEIGHTS);
		
	}
	
	protected void disposeMiddleComposite() {
		middleComposite.dispose();
		middleComposite = null;
	}
	
	protected void disposeRightComposite() {
		rightComposite.dispose();
		rightComposite = null;
	}
	
	protected void createMiddleComposite() {
		middleComposite = new Composite(sashForm, SWT.NONE);
		middleComposite.setLayout(new FillLayout());
	}
	
	protected void createRightComposite() {
		rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new FillLayout());
	}
	
	protected void updateEditorLayout(EditorLayout layout) {
		AttributeViewService attributeViewService = editor.getServiceProvider().getService(AttributeViewService.class);
		switch (layout) {
		case TREE:
			if (attributeViewService != null && !attributeViewService.isAttached()) {
				attributeViewService.attachToView();
			}
			if (slaveViewer != null) {
				disposeSlaveViewer();
			}
			if (rightComposite != null) {
				disposeRightComposite();
			}
			if (middleComposite != null) {
				disposeMiddleComposite();
			}
			sashForm.setWeights(ONE_PART_WEIGHTS);
			break;
		case TWO_TREES:
			if (attributeViewService != null && !attributeViewService.isAttached()) {
				attributeViewService.attachToView();
			}
			if (rightComposite != null) {
				disposeRightComposite();
			}
			if (middleComposite == null) {
				createMiddleComposite();
			}
			if (slaveViewer == null) {
				createSlaveViewer(middleComposite);
			} else {
				attachSlaveViewer(middleComposite);
			}
			sashForm.setWeights(TWO_PARTS_WEIGHTS);
			break;
		case TREE_AND_ATTRIBUTES:
			if (slaveViewer != null) {
				disposeSlaveViewer();
			}
			if (attributeViewService != null && attributeViewService.isAttached()) {
				attributeViewService.detachFromView();				
			}
			if (middleComposite == null) {
				createMiddleComposite();
			}
			attachAttributeViewer(middleComposite);
			if (rightComposite != null) {
				disposeRightComposite();
			}
			sashForm.setWeights(TWO_PARTS_WEIGHTS);
			break;
		case TWO_TREES_AND_ATTRIBUTES:
			if (attributeViewService != null && attributeViewService.isAttached()) {
				attributeViewService.detachFromView();				
			}
			if (middleComposite == null) {
				createMiddleComposite();
			}
			if (slaveViewer == null) {
				createSlaveViewer(middleComposite);
			} else {
				attachSlaveViewer(middleComposite);
			}
			if (rightComposite == null) {
				createRightComposite();
			}
			attachAttributeViewer(rightComposite);
			sashForm.setWeights(THREE_PARTS_WEIGHTS);
			break;
		}
		layoutAll();
	}
		
	protected void attachSlaveViewer(Composite composite) {
		slaveViewer.getControl().setParent(composite);
	}
	
	protected void attachAttributeViewer(Composite composite) {
		attributeViewer.getControl().setParent(composite);
	}
	
	protected void createSlaveViewer(Composite composite) {
		if (slaveViewer == null) {
			// create it!
			slavePinned = false;
			slaveViewer = new SchemaViewer(editor, slaveContext);
			slaveViewer.setSchemaViewerConfiguration(schemaViewerConfiguration);
			slaveViewer.setAttributeColumnConfiguration(attributesConfiguration.getAttributeColumnConfiguration(slaveContext));
			slaveViewer.createControl(composite);
			slaveViewer.addSelectionChangedListener(internalSelectionListener);
			
			linkAttributeViewerToSchemaViewer(attributeViewer, slaveViewer);
			registerActiveSchemaViewerListener(slaveViewer);
			
			if (master2slaveSelectionListener == null) {
				master2slaveSelectionListener = new ISelectionChangedListener() {					
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						showInSlaveViewer(event.getSelection());
					}
				};
			}
			
			showInSlaveViewer(masterViewer.getSelection());
			
			masterViewer.addSelectionChangedListener(master2slaveSelectionListener);			
			
		} else {
			throw new IllegalStateException("Slave Viewer should be null!");
		}
	}
	
	public boolean isSlavePinned() { 
		return slavePinned;
	}
	
	public void setSlavePinned(boolean pinned) {
		slavePinned = pinned;
		if (!slavePinned) {
			// update slave viewer
			showInSlaveViewer(masterViewer.getSelection());
		}
	}
	
	protected void showInSlaveViewer(ISelection selection) {
		if (!slavePinned) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structSel = (IStructuredSelection) selection;
				if (!structSel.isEmpty()) {
					Object element = structSel.getFirstElement();
					if (element instanceof AvroNode && isShowableInSlaveViewer((AvroNode)element)) {
						showInSlaveViewer((AvroNode) element);
					}
				}
			}
		}
	}
	
	protected boolean isShowableInSlaveViewer(AvroNode node) {
		return node.hasChildren() || 
				( node.getType().isRef() && ((RefNode) node).getReferencedNode().hasChildren());
	}
	
	protected void showInSlaveViewer(AvroNode node) {
		AvroNode slaveContent = node;
		if (node.getType().isRef()) {
			slaveContent = ((RefNode) node).getReferencedNode();
		}
		if (slaveViewer.getContent() != slaveContent) {
			slaveViewer.setContent(slaveContent);		
			//	slaveViewer.getTreeViewer().expandToLevel(2);
		}
	}
	
	protected void disposeSlaveViewer() {
		if (slaveViewer == null) {
			throw new IllegalStateException("Cannot dispose slave viewer since it is null!");
		}
		unlinkAttributeViewerToSchemaViewer(attributeViewer, slaveViewer);
		unregisterActiveSchemaViewerListener(slaveViewer);
		this.activeContext = Kind.MASTER;
		masterViewer.removeSelectionChangedListener(master2slaveSelectionListener);
		slaveViewer.getControl().dispose();
		slaveViewer.dispose();
		slaveViewer = null;
	}
	
	protected void layoutAll() {
		sashForm.getParent().layout(true, true);
	}
	
	@Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    @SuppressWarnings("unchecked")
	@Override
    public ISelection getSelection() {
        IStructuredSelection masterSelection = (IStructuredSelection) masterViewer.getSelection();                
        List<?> selection = new ArrayList<>();
        selection.addAll(masterSelection.toList());
        if (slaveViewer != null) {
        	IStructuredSelection slaveSelection = (IStructuredSelection) slaveViewer.getSelection();
        	selection.addAll(slaveSelection.toList());
        }
        return new StructuredSelection(selection);
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        // TODO
    }
	
	protected void fireSelectionChangedEvent(ISelection selection, ISelectionProvider provider) {
        SelectionChangedEvent event = new SelectionChangedEvent(provider, selection);
        for (Object listener : selectionChangedListeners.getListeners()) {
            ((ISelectionChangedListener) listener).selectionChanged(event);
        }
    }	
	
	protected void linkAttributeViewerToSchemaViewer(final AttributeViewer attributeViewer, SchemaViewer schemaViewer) {
		if (schema2AattributeSelectionListener == null) {
			schema2AattributeSelectionListener = new ISelectionChangedListener() {			
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					SchemaViewer selectionProvider = (SchemaViewer) event.getSelectionProvider();
					ISelection selection = event.getSelection();
					Object selectedNode = null;
					if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
						selectedNode = ((IStructuredSelection) selection).getFirstElement();
					}
					attributeViewer.setInput(selectedNode, selectionProvider.getContext());
				}
			};
		}
		schemaViewer.addPostSelectionChangedListener(schema2AattributeSelectionListener);	
	}
	
	protected void unlinkAttributeViewerToSchemaViewer(final AttributeViewer attributeViewer, SchemaViewer schemaViewer) {
		schemaViewer.removeSelectionChangedListener(schema2AattributeSelectionListener);
	}
	
	protected void registerActiveSchemaViewerListener(SchemaViewer schemaViewer) {
		if (activeSchemaViewerListener == null) {
			activeSchemaViewerListener = new ISelectionChangedListener() {				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					SchemaViewer activeSchemaViewer = (SchemaViewer) event.getSelectionProvider();
					SchemaEditorContentPart.this.activeContext = activeSchemaViewer.getContext().getKind();
				}
			};
		}
		schemaViewer.addSelectionChangedListener(activeSchemaViewerListener);
	}
	
	protected void unregisterActiveSchemaViewerListener(SchemaViewer schemaViewer) {
		schemaViewer.removeSelectionChangedListener(activeSchemaViewerListener);
	}
	
	public void setSchemaViewerConfiguration(SchemaViewerConfiguration schemaViewerConfiguration) {
		this.schemaViewerConfiguration = schemaViewerConfiguration;
	}
	
	public void setAttributesConfiguration(AttributesConfiguration attributesConfiguration) {
		this.attributesConfiguration = attributesConfiguration;
	}
	
	public AttributesConfiguration getAttributesConfiguration() {
		return attributesConfiguration;
	}

	public void setContent(AvroNode inputNode) {
		masterViewer.setContent(inputNode);
	}
	
	public SchemaViewer getSchemaViewer(AvroContext.Kind kind) {
		if (kind == Kind.MASTER) {
			return masterViewer;
		} else if (slaveViewer != null) {
			return slaveViewer;
		}
		return null;
	}
	public SchemaViewer getActiveSchemaViewer() {
		return getSchemaViewer(activeContext);
	}
	
	public AttributeViewer getAttributeViewer() {
		return attributeViewer;
	}
		
	public void dispose() {
		//
	}
	
}
