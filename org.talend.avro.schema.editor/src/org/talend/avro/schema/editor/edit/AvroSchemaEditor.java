package org.talend.avro.schema.editor.edit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.talend.avro.schema.editor.attributes.AttributesConfiguration;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.context.AvroEditorContext;
import org.talend.avro.schema.editor.context.services.ContextualServiceFactory;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.context.services.IContextualServiceFactory;
import org.talend.avro.schema.editor.edit.services.AvroSchemaEditorServiceProvider;
import org.talend.avro.schema.editor.edit.services.EditorServiceFactory;
import org.talend.avro.schema.editor.edit.services.IEditorService;
import org.talend.avro.schema.editor.edit.services.IEditorServiceFactory;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.edit.services.InitializationPhase;
import org.talend.avro.schema.editor.io.AvroSchemaGenerator;
import org.talend.avro.schema.editor.io.AvroSchemaParser;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfiguration;

/**
 * <b>This is the main component to edit an avro schema.</b>
 *   
 * 
 * @author timbault
 *
 */
public class AvroSchemaEditor implements IDirtyable, IWithServiceProvider {
	
	/**
	 * Enclosing workbench part (editor or view). Could be null.
	 */
	private IWorkbenchPart enclosingPart;	
	
	private SchemaEditorContentPart contentPart;
	
	private AvroEditorContext masterContext;
	
	private AvroEditorContext slaveContext;
		
	private AvroSchemaEditorServiceProvider serviceProvider;
	
	private boolean dirty = false;
		
	private String name;
	
	private String contextId;
	
	private ListenerList dirtyListeners = new ListenerList();
	
	private IEditorConfiguration editorConfiguration;
	
	private AttributesConfiguration attributesConfiguration;
	
	private SchemaViewerConfiguration schemaViewerConfiguration;
	
	private boolean initialized = false;
	
	public AvroSchemaEditor(String name, String contextId) {
		this(name, contextId, null);
	}
	
	public AvroSchemaEditor(String name, String contextId, IWorkbenchPart enclosingPart) {
		super();
		this.name = name;
		this.contextId = contextId;
		this.enclosingPart = enclosingPart;
	}
	
	public String getName() {
		return name;
	}

	public IWorkbenchPart getEnclosingPart() {
		return enclosingPart;
	}
	
	protected void checkInitializedStatus() {
		if (initialized) {
			throw new IllegalStateException("Avro Schema Editor content part already initialized");
		}
	}

	public void setSchemaViewerConfiguration(SchemaViewerConfiguration schemaViewerConfiguration) {
		checkInitializedStatus();
		if (this.schemaViewerConfiguration == null) {
			this.schemaViewerConfiguration = schemaViewerConfiguration;
			if (canBeInitialized()) {
				init();
			}
		} else {
			throw new IllegalStateException("Avro Schema viewer configuration already set");
		}
	}

	public void setEditorConfiguration(IEditorConfiguration editorConfiguration) {
		checkInitializedStatus();
		if (this.editorConfiguration == null) {
			this.editorConfiguration = editorConfiguration;
			if (canBeInitialized()) {
				init();
			}
		} else {
			throw new IllegalStateException("Editor configuration already set");
		}
	}

	public void setAttributesConfiguration(AttributesConfiguration attributesConfiguration) {
		checkInitializedStatus();
		if (this.attributesConfiguration == null) {
			this.attributesConfiguration = attributesConfiguration;
			if (canBeInitialized()) {
				init();
			}
		} else {
			throw new IllegalStateException("Attributes configuration already set");
		}
	}
	
	@Override
	public void addDirtyListener(IDirtyListener listener) {
		dirtyListeners.add(listener);
	}

	@Override
	public void removeDirtyListener(IDirtyListener listener) {
		dirtyListeners.remove(listener);
	}

	protected void notifyOnDirtyStatusChanged(boolean dirty) {
		for (Object listener : dirtyListeners.getListeners()) {
			((IDirtyListener) listener).onDirtyStatusChanged(this, dirty);
		}
	}

	protected void notifyOnDirtyStatusChanged(Object object, boolean dirty) {
		for (Object listener : dirtyListeners.getListeners()) {
			((IDirtyListener) listener).onDirtyStatusChanged(this, object, dirty);
		}
	}
	
	protected String getContextId() {
		return contextId;
	}
	
	public void setInput(AvroSchema avroSchema) {
		
		// parsing
		AvroSchemaParser parser = editorConfiguration.getParser(masterContext);
		RootNode rootNode = parser.parse(avroSchema.getContent(), avroSchema.getName());
		
		// update the master and slave contexts
		updateContexts(rootNode);
		
		if (contentPart != null) {
			
			masterContext.getSchemaRegistry().clear();
			
			contentPart.setContent(rootNode);
						
			setDirty(true);
			
			// clear the command stacks
			serviceProvider.getService(ICommandExecutor.class).clearUndoAndRedoStacks();
		}
		
	}
	
	private boolean canBeInitialized() {
		return editorConfiguration != null && attributesConfiguration != null && schemaViewerConfiguration != null;
	}
	
	private void init() {
		
		if (editorConfiguration == null) {
			throw new IllegalStateException("Avro Schema Editor cannot be initialized, editor configuration is missing");
		}
		if (attributesConfiguration == null) {
			throw new IllegalStateException("Avro Schema Editor cannot be initialized, attributes configuration is missing");
		}
		if (schemaViewerConfiguration == null) {
			throw new IllegalStateException("Avro Schema Editor cannot be initialized, schema viewer configuration is missing");
		}		          
        
        // create service provider
        serviceProvider = new AvroSchemaEditorServiceProvider();		
        
        // create master context
        masterContext = new AvroEditorContext(contextId, serviceProvider);
        CustomAttributeConfiguration customAttributeConfiguration = attributesConfiguration.getCustomAttributeConfiguration(masterContext);
        masterContext.setCustomAttributeConfiguration(customAttributeConfiguration);
        
        // create slave context
        slaveContext = new AvroEditorContext(contextId, masterContext);        
        slaveContext.setCustomAttributeConfiguration(customAttributeConfiguration);
                
        // configure contextual services
        createAndConfigureContextualServices(masterContext, serviceProvider, editorConfiguration);
        
        // configure Pre UI editor services
        createAndConfigureEditorServices(masterContext, serviceProvider, editorConfiguration, InitializationPhase.PRE_UI);	
		
        initialized = true;
        
	}
	
	protected void updateContexts(RootNode rootNode) {
		masterContext.setRootNode(rootNode);
		masterContext.setInputNode(rootNode);		
		slaveContext.setRootNode(rootNode);
	}
	
	public void save(AvroSchema avroSchema) {
		
		RootNode rootNode = masterContext.getRootNode();		
		
		// write the new content
		AvroSchemaGenerator generator = editorConfiguration.getGenerator(masterContext);		
		Schema schema = generator.generate(rootNode);
		
		if (schema == null) {
			avroSchema.setContent("");
		} else {
			avroSchema.setContent(schema.toString(true));			
		}
		
		setDirty(false);
				
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            notifyOnDirtyStatusChanged(dirty);
        }
    }
	
	@Override
	public void setDirty(Object object, boolean dirty) {
		setDirty(dirty);
	}

	public void createPartControl(Composite parent) {		
		
		if (!initialized) {
			throw new IllegalStateException("Cannot create avro schema editor content part, configurations are missing");
		}
				
		contentPart = new SchemaEditorContentPart(this, masterContext, slaveContext);
		contentPart.setSchemaViewerConfiguration(schemaViewerConfiguration);
		contentPart.setAttributesConfiguration(attributesConfiguration);
		
		contentPart.createContentPart(parent);
		
		contentPart.setContent(masterContext.getInputNode());
		
        // configure Post UI services
        createAndConfigureEditorServices(masterContext, serviceProvider, editorConfiguration, InitializationPhase.POST_UI);
		
        finalizeEditorConfiguration(editorConfiguration);
		
	}	
	
	public IEditorConfiguration getEditorConfiguration() {
		return editorConfiguration;
	}
	
	protected void finalizeEditorConfiguration(IEditorConfiguration editorConfiguration) {
		editorConfiguration.configureContentPart(contentPart);
	}	
	
	protected IContextualServiceFactory getDefaultContextualServiceFactory() {
		return new ContextualServiceFactory(); 
	}
	
	protected IEditorServiceFactory getDefaultEditorServiceFactory() {
		return new EditorServiceFactory();
	}
	
	protected void createAndConfigureContextualServices(
			AvroContext context, AvroSchemaEditorServiceProvider serviceProvider, IEditorConfiguration editorConfiguration) {
		
		InternalContextualServiceFactory contextualServiceFactory = 
				new InternalContextualServiceFactory(getDefaultContextualServiceFactory(), editorConfiguration);
		
		Collection<Class<? extends IContextualService>> providedServices = contextualServiceFactory.getProvidedServices();
		
		for (Class<? extends IContextualService> providedService : providedServices) {
			createContextualService(context, serviceProvider, providedService, contextualServiceFactory);
		}
		
		if (editorConfiguration != null) {
			editorConfiguration.configureContextualServices(context, serviceProvider);
		}
		
	}
			
	protected <T extends IContextualService> void createContextualService(AvroContext context, AvroSchemaEditorServiceProvider serviceProvider, 
			Class<T> serviceClass, IContextualServiceFactory serviceFactory) {
		T serviceImpl = serviceFactory.createService(serviceClass);
		serviceImpl.init(context);
		serviceProvider.registerService(serviceClass, serviceImpl);
	}
	
	protected void createAndConfigureEditorServices(
			AvroContext context, AvroSchemaEditorServiceProvider serviceProvider, IEditorConfiguration editorConfiguration, InitializationPhase phase) {		
		
		InternalEditorServiceFactory editorServiceFactory = 
				new InternalEditorServiceFactory(getDefaultEditorServiceFactory(), editorConfiguration);
		
		Collection<Class<? extends IEditorService>> providedServices = editorServiceFactory.getProvidedServices(phase);
		
		for (Class<? extends IEditorService> providedService : providedServices) {
			createEditorService(serviceProvider, providedService, editorServiceFactory, phase);
		}
		
		if (editorConfiguration != null) {
			editorConfiguration.configureEditorServices(context, serviceProvider, phase);
		}
		
	}

	protected <T extends IEditorService> void createEditorService(AvroSchemaEditorServiceProvider serviceProvider, 
			Class<T> serviceClass, IEditorServiceFactory serviceFactory, InitializationPhase phase) {
		T serviceImpl = serviceFactory.createService(serviceClass, phase);
		serviceImpl.init(this);
		serviceProvider.registerService(serviceClass, serviceImpl);
	}
	
	public SchemaEditorContentPart getContentPart() {
		return contentPart;
	}

	@Override
	public IEditorServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setFocus() {
		contentPart.getSchemaViewer(AvroContext.Kind.MASTER).setFocus();
	}

	public AvroContext getContext() {
		return masterContext;
	}
	
	public AvroContext getActiveContext() {
		return contentPart.getActiveSchemaViewer().getContext();
	}
	
	public AvroContext getContext(AvroContext.Kind kind) {
		if (kind == Kind.MASTER) {
			return masterContext;
		} else {
			return slaveContext;
		}
	}
	
	public void dispose() {
		masterContext.dispose();
		slaveContext.dispose();
		serviceProvider.dispose();
	}

	private static class InternalContextualServiceFactory implements IContextualServiceFactory {
		
		private IContextualServiceFactory defaultContextualServiceFactory;
		
		private IContextualServiceFactory customContextualServiceFactory;
		
		public InternalContextualServiceFactory(IContextualServiceFactory defaultContextualServiceFactory,
				IContextualServiceFactory customContextualServiceFactory) {
			super();
			this.defaultContextualServiceFactory = defaultContextualServiceFactory;
			this.customContextualServiceFactory = customContextualServiceFactory;
		}

		@Override
		public Collection<Class<? extends IContextualService>> getProvidedServices() {
			Set<Class<? extends IContextualService>> providedServiceClasses = new HashSet<>();
			Collection<Class<? extends IContextualService>> providedServices = defaultContextualServiceFactory.getProvidedServices();
			for (Class<? extends IContextualService> providedServiceClass : providedServices) {
				providedServiceClasses.add(providedServiceClass);
			}
			if (customContextualServiceFactory != null) {
				providedServices = customContextualServiceFactory.getProvidedServices();
				for (Class<? extends IContextualService> providedServiceClass : providedServices) {
					providedServiceClasses.add(providedServiceClass);
				}
			}
			return providedServiceClasses;
		}

		@Override
		public <T extends IContextualService> T createService(Class<T> serviceClass) {
			if (customContextualServiceFactory != null) {
				Collection<Class<? extends IContextualService>> providedServices = customContextualServiceFactory.getProvidedServices();
				if (providedServices.contains(serviceClass)) {
					return customContextualServiceFactory.createService(serviceClass);
				}
			}
			return defaultContextualServiceFactory.createService(serviceClass);
		}
		
	}
	
	private static class InternalEditorServiceFactory implements IEditorServiceFactory {
				
		private IEditorServiceFactory defaultEditorServiceFactory;
		
		private IEditorServiceFactory customEditorServiceFactory;

		public InternalEditorServiceFactory(IEditorServiceFactory defaultEditorServiceFactory,
				IEditorServiceFactory customEditorServiceFactory) {
			super();
			this.defaultEditorServiceFactory = defaultEditorServiceFactory;
			this.customEditorServiceFactory = customEditorServiceFactory;
		}

		@Override
		public Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase) {
			Set<Class<? extends IEditorService>> providedServiceClasses = new HashSet<>();
			Collection<Class<? extends IEditorService>> providedServices = defaultEditorServiceFactory.getProvidedServices(phase);
			for (Class<? extends IEditorService> providedServiceClass : providedServices) {
				providedServiceClasses.add(providedServiceClass);
			}
			if (customEditorServiceFactory != null) {
				providedServices = customEditorServiceFactory.getProvidedServices(phase);
				for (Class<? extends IEditorService> providedServiceClass : providedServices) {
					providedServiceClasses.add(providedServiceClass);
				}
			}
			return providedServiceClasses;
		}

		@Override
		public <T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase) {
			if (customEditorServiceFactory != null) {
				Collection<Class<? extends IEditorService>> providedServices = customEditorServiceFactory.getProvidedServices(phase);
				if (providedServices.contains(serviceClass)) {
					return customEditorServiceFactory.createService(serviceClass, phase);
				}
			}
			return defaultEditorServiceFactory.createService(serviceClass, phase);
		}
		
	}

}
