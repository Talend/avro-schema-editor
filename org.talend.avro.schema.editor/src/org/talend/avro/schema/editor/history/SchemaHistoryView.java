package org.talend.avro.schema.editor.history;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.ICommandListener;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;

/**
 * Eclipse view part displaying the undo and redo command stacks of the avro schema editor.
 * <p>
 * 
 * @author timbault
 *
 */
public class SchemaHistoryView extends ViewPart {

	public static final String ID = "org.talend.avro.schema.editor.history.view"; //$NON-NLS-1$
	
	private enum Orientation {
		HORIZONTAL(SWT.HORIZONTAL), VERTICAL(SWT.VERTICAL);
		
		private int style;

		private Orientation(int style) {
			this.style = style;
		}

		public int getStyle() {
			return style;
		}
		
	}
	
	private static final boolean UNDO = true;
	
	private static final boolean REDO = false;
	
	private Orientation orientation = Orientation.HORIZONTAL;
	
	private Composite mainComposite;
	
	private SashForm sashForm;
	
	private static final int[] WEIGHTS = new int[] { 1, 1 };
	
	private ListViewer undoViewer;
	
	private ListViewer redoViewer;
	
	private IPartListener partListener;
	
	private ICommandListener commandListener;
	
	private AvroSchemaEditor editor;
	
	private ICommandExecutor commandExecutor;
	
	private Button undoButton;
	
	private Button redoButton;
	
	private void init() {
		commandListener = new ICommandListener() {
			
			@Override
			public void onUndoCommand(IEditCommand command, ICommandExecutor executor) {
				update();
			}
			
			@Override
			public void onRunCommand(IEditCommand command, ICommandExecutor executor) {
				update();
			}
			
			@Override
			public void onRedoCommand(IEditCommand command, ICommandExecutor executor) {
				update();
			}
		};
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		init();
		
		mainComposite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		mainComposite.setLayout(layout);
		
		Composite topCompo = new Composite(mainComposite, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		topCompo.setLayoutData(layoutData);
		topCompo.setLayout(new GridLayout(2, true));
		
		undoButton = new Button(topCompo, SWT.PUSH);
		undoButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		undoButton.setEnabled(false);
		undoButton.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.UNDO));
		undoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				undo();
			}
		});
		
		redoButton = new Button(topCompo, SWT.PUSH);
		redoButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		redoButton.setEnabled(false);
		redoButton.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.REDO));
		redoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				redo();
			}
		});
		
		sashForm = new SashForm(mainComposite, orientation.getStyle());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		undoViewer = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);		
		undoViewer.setContentProvider(getContentProvider(UNDO));		
		undoViewer.setLabelProvider(getLabelProvider());
		
		redoViewer = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);		
		redoViewer.setContentProvider(getContentProvider(REDO));		
		redoViewer.setLabelProvider(getLabelProvider());
		
		sashForm.setWeights(WEIGHTS);
		
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
		
		getSite().getPage().addPartListener(partListener);
		
	}
	
	protected void linkEditor(AvroSchemaEditor editor) {
		
		this.editor = editor;
		this.commandExecutor = this.editor.getServiceProvider().getService(ICommandExecutor.class);
		this.commandExecutor.addCommandListener(commandListener);
		
		undoViewer.setInput(commandExecutor);
		redoViewer.setInput(commandExecutor);
		
		update();
		
	}
	
	protected void unlinkEditor(boolean onDispose) {
		
		commandExecutor.removeCommandListener(commandListener);
		commandExecutor = null;
		editor = null;
		
		if (!onDispose) {			
			// clear the list viewer
			undoViewer.setInput(null);
			redoViewer.setInput(null);
			update();
		}
		
	}
	
	protected void update() {
		undoViewer.refresh();
		redoViewer.refresh();
		undoButton.setEnabled(commandExecutor != null && !commandExecutor.isUndoableCommandStackEmpty());
		redoButton.setEnabled(commandExecutor != null && !commandExecutor.isRedoableCommandStackEmpty());
	}
	
	protected void undo() {
		this.commandExecutor.undo();
		update();
	}
	
	protected void redo() {
		this.commandExecutor.redo();
		update();
	}
	
	protected IContentProvider getContentProvider(final boolean undo) {
		return new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				//
			}
			
			@Override
			public void dispose() {
				//
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				ICommandExecutor cmdExecutor = (ICommandExecutor) inputElement;
				if (undo) {
					return cmdExecutor.getUndoableCommandStack().toArray();
				} else {
					return cmdExecutor.getRedoableCommandStack().toArray();
				}
			}
			
		};
	}
	
	protected IBaseLabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IEditCommand) element).getLabel();
			} 
		};
	}
	
	@Override
	public void setFocus() {
		sashForm.setFocus();
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		if (editor != null) {
			unlinkEditor(true);
		}		
		super.dispose();
	}
	
}
