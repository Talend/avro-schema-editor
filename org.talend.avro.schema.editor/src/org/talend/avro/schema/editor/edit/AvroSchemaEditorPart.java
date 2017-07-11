package org.talend.avro.schema.editor.edit;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.services.IEvaluationService;
import org.talend.avro.schema.editor.attributes.AttributesConfiguration;
import org.talend.avro.schema.editor.attributes.AvroSchemaEditorAttributesConfiguration;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.ICommandListener;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.edit.services.AvroSchemaEditorConfiguration;
import org.talend.avro.schema.editor.handlers.UndoRedoPropertyTester;
import org.talend.avro.schema.editor.model.attributes.custom.AttributesConfigurationLoader;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfiguration;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfigurationImpl;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfigurationLoader;

/**
 * <b>The avro schema editor part.</b>
 * <p>
 * It creates and configures the main component {@link AvroSchemaEditor} in an eclipse editor.
 * <p>
 * It loads the configurations from several eclipse extension points.
 * <p>
 * @author timbault
 * @see AvroSchemaEditor
 *
 */
public class AvroSchemaEditorPart extends EditorPart implements IDirtyListener, IWithAvroSchemaEditor {

	public static final String ID = "org.talend.avro.schema.editor.edit.AvroSchemaEditor"; //$NON-NLS-1$
	
	public static final String CONTEXT_ID = ID + ".context"; //$NON-NLS-1$
	
	private AvroSchemaEditor editor;
	
	private AvroSchema avroSchema;
	
	protected String getContextId() {
		return ID;
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		setSite(site);
        setInput(input);
        setTitleImage(input.getImageDescriptor().createImage());
        setPartName(input.getName());
        
        // initialize the context bindings for this editor. Useful for shortcuts
        IContextService service = (IContextService) site.getService(IContextService.class);
        service.activateContext(CONTEXT_ID);
        
        // get the input
        avroSchema = getAvroSchema();
        
        // load the configurations
        IEditorConfiguration editorConfiguration = loadEditorConfiguration();
        SchemaViewerConfiguration schemaViewerConfiguration = loadSchemaViewerConfiguration();
        AttributesConfiguration attributesConfiguration = loadAttributesConfiguration();
        
        // create and configure the main component
        editor = new AvroSchemaEditor(input.getName(), getContextId(), this);        
        editor.setEditorConfiguration(editorConfiguration);
        editor.setSchemaViewerConfiguration(schemaViewerConfiguration);
        editor.setAttributesConfiguration(attributesConfiguration);
        editor.setInput(avroSchema);
        
        editor.addDirtyListener(this);
        
	}
	
	protected void refreshCommands(String... properties) {
		IEvaluationService evalService = (IEvaluationService) getEditorSite().getService(IEvaluationService.class);
		if (evalService != null) {
			for (String property: properties) {
				evalService.requestEvaluation(property);
			}
		}        
    }
	
	protected IEditorConfiguration loadEditorConfiguration() {
		// configure services
		EditorConfigurationLoader editorConfigurationLoader = new EditorConfigurationLoader();
		IEditorConfiguration editorConfiguration = editorConfigurationLoader.getEditorConfiguration(getContextId());
		if (editorConfiguration == null) {
			editorConfiguration = new AvroSchemaEditorConfiguration();
		}
		return editorConfiguration;
	}
	
	protected SchemaViewerConfiguration loadSchemaViewerConfiguration() {
		SchemaViewerConfigurationLoader schemaViewerConfigurationLoader = new SchemaViewerConfigurationLoader();
		SchemaViewerConfiguration schemaViewerConfiguration = schemaViewerConfigurationLoader.getSchemaViewerConfiguration(getContextId());
		if (schemaViewerConfiguration == null) {
			schemaViewerConfiguration = new SchemaViewerConfigurationImpl();
		}
		return schemaViewerConfiguration;
	}
	
	protected AttributesConfiguration loadAttributesConfiguration() {
		AttributesConfigurationLoader attributesConfigurationLoader = new AttributesConfigurationLoader();
		AttributesConfiguration attributesConfiguration = attributesConfigurationLoader.getAttributesConfiguration(getContextId());
		if (attributesConfiguration == null) {
			attributesConfiguration = new AvroSchemaEditorAttributesConfiguration();
		}
		return attributesConfiguration;
	}
	
	public AvroSchemaEditor getEditor() {
		return editor;
	}

	protected AvroSchema getAvroSchema() {
		IPathEditorInput pathInput = (IPathEditorInput) getEditorInput();
		IPath path = pathInput.getPath();		
		File file = path.toFile();
		return new AvroSchemaFile(file);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		editor.save(avroSchema);
	}

	@Override
	public void doSaveAs() {
		// not yet implemented
	}

	@Override
	public boolean isDirty() {
		return editor.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		editor.createPartControl(parent);
		getSite().setSelectionProvider(editor.getContentPart());
		ICommandExecutor cmdExecutor = editor.getServiceProvider().getService(ICommandExecutor.class);
		cmdExecutor.addCommandListener(new ICommandListener() {
			
			@Override
			public void onUndoCommand(IEditCommand command, ICommandExecutor executor) {
				refreshCommands(UndoRedoPropertyTester.PROPERTIES);
			}
			
			@Override
			public void onRunCommand(IEditCommand command, ICommandExecutor executor) {
				refreshCommands(UndoRedoPropertyTester.PROPERTIES);
			}
			
			@Override
			public void onRedoCommand(IEditCommand command, ICommandExecutor executor) {
				refreshCommands(UndoRedoPropertyTester.PROPERTIES);
			}
			
		});
	}
	
	@Override
	public void setFocus() {
		editor.setFocus();
	}

	@Override
	public void onDirtyStatusChanged(IDirtyable dirtyable, boolean dirty) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void onDirtyStatusChanged(IDirtyable dirtyable, Object object, boolean dirty) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void dispose() {
		editor.removeDirtyListener(this);
		editor.dispose();
		super.dispose();
	}
	
}
