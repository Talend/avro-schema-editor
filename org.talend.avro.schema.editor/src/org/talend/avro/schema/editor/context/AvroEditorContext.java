package org.talend.avro.schema.editor.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.context.services.ServicesObserver;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.edit.services.NameService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.model.SchemaNodeRegistryImpl;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.SchemaViewerNodeConverter;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class AvroEditorContext implements AvroContext, ISelectionChangedListener  {
	
	private String id;
	
	private Kind kind;
	
	private SchemaRegistry schemaRegistry;
	
	private SchemaNodeRegistry schemaNodeRegistry;
	
	private SchemaViewerNodeConverter nodeConverter;
	
	private RootNode rootNode;
	
	private AvroNode inputNode;
	
	private String defaultNameSpace = "";
	
	private PrimitiveType defaultPrimitiveType = PrimitiveType.STRING;
	
	private List<AvroNode> contextualNodes = new ArrayList<>();	
	
	private List<SchemaNode> schemaNodes = new ArrayList<>();
	
	private SearchNodeContext searchNodeContext;
	
	private CopyContext copyContext;
	
	/**
	 * MASTER or SLAVE, it depends
	 */
	private AvroContext linkedContext;
	
	private CustomAttributeConfiguration customAttributeConfiguration;
	
	private IEditorServiceProvider serviceProvider;
	
	private ListenerList listeners = new ListenerList();
	
	public AvroEditorContext(String id, IEditorServiceProvider serviceProvider) {
		super();
		this.id = id;
		this.serviceProvider = serviceProvider;
		init(null);
	}

	public AvroEditorContext(String id, AvroContext masterContext) {
		super();
		this.id = id;
		init(masterContext);
	}
	
	@Override
	public String getId() {
		return id;
	}

	private void init(AvroContext masterContext) {
		if (masterContext == null) {
			// this is the master context
			this.kind = Kind.MASTER;
			this.schemaRegistry = new SchemaRegistry(serviceProvider);	
			this.schemaNodeRegistry = new SchemaNodeRegistryImpl(schemaRegistry);
			this.nodeConverter = new SchemaViewerNodeConverter(schemaNodeRegistry);
			this.searchNodeContext = new SearchNodeContext(schemaRegistry);
			this.copyContext = new CopyContext();
		} else {
			// this is the slave context
			this.kind = Kind.SLAVE;
			this.schemaRegistry = masterContext.getSchemaRegistry();
			this.schemaNodeRegistry = new SchemaNodeRegistryImpl(schemaRegistry);
			this.nodeConverter = new SchemaViewerNodeConverter(schemaNodeRegistry);
			this.searchNodeContext = new SearchNodeContext(schemaRegistry);
			setLinkedContext(masterContext);
			((AvroEditorContext) masterContext).setLinkedContext(this);
		}
	}
	
	@Override
	public <T extends IContextualService> T getService(Class<T> serviceClass) {
		if (serviceProvider != null) {
			return serviceProvider.getService(serviceClass);
		} else {
			return linkedContext.getService(serviceClass);
		}
	}
	
	@Override
	public void addServicesObserver(ServicesObserver observer) {
		if (serviceProvider != null) {
			serviceProvider.addServicesObserver(observer);
		} else {
			linkedContext.addServicesObserver(observer);
		}
	}

	@Override
	public void removeServicesObserver(ServicesObserver observer) {
		if (serviceProvider != null) {
			serviceProvider.removeServicesObserver(observer);
		} else {
			linkedContext.removeServicesObserver(observer);
		}
	}

	public CustomAttributeConfiguration getCustomAttributeConfiguration() {
		return customAttributeConfiguration;
	}

	public void setCustomAttributeConfiguration(CustomAttributeConfiguration customAttributeConfiguration) {
		this.customAttributeConfiguration = customAttributeConfiguration;
	}

	public void setLinkedContext(AvroContext context) {
		this.linkedContext = context;
	}
	
	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public AvroContext getMaster() {
		if (kind == Kind.MASTER) {
			return this;
		} else {
			return linkedContext;
		}
	}

	@Override
	public AvroContext getSlave() {
		if (kind == Kind.SLAVE) {
			return this;
		} else {
			return linkedContext;
		}
	}

	@Override
	public boolean isMaster() {
		return kind == Kind.MASTER;
	}

	@Override
	public boolean isSlave() {
		return kind == Kind.SLAVE;
	}

	@Override
	public SearchNodeContext getSearchNodeContext() {
		return searchNodeContext;
	}
	
	@Override
	public CopyContext getCopyContext() {
		if (copyContext != null) {
			return copyContext;
		} else {
			return linkedContext.getCopyContext();
		}
	}

	public void setRootNode(RootNode rootNode) {
		this.rootNode = rootNode;
		notifyListenersOnRootNodeChanged(rootNode);
	}

	public void setInputNode(AvroNode inputNode) {
		this.inputNode = inputNode;
		notifyListenersOnInputNodeChanged(inputNode);
	}

	@Override
	public RootNode getRootNode() {
		return rootNode;
	}

	@Override
	public AvroNode getInputNode() {
		return inputNode;
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();				
		contextualNodes.clear();
		contextualNodes.addAll(nodeConverter.convertToAvroNodes(selection));
		schemaNodes.clear();
		schemaNodes.addAll(nodeConverter.convertToSchemaNodes(selection));
		notifyListenersOnContextualNodesChanged(getContextualNodes());
		notifyListenersOnSchemaNodesChanged(getSchemaNodes());
	}

	@Override
	public String getDefaultNameSpace() {
		return defaultNameSpace;
	}

	@Override
	public String getEnclosingNameSpace() {
		String enclosingNameSpace = defaultNameSpace;
		if (!contextualNodes.isEmpty()) {
			String nameSpace = AttributeUtil.getEnclosingNameSpace(contextualNodes.get(0), true);
			if (nameSpace != null) {
				enclosingNameSpace = nameSpace;
			}
		}
		return enclosingNameSpace;
	}	
		
	@Override
	public PrimitiveType getDefaultPrimitiveType() {
		return defaultPrimitiveType;
	}

	@Override
	public String getAvailableName(NodeType nodeType) {
		AvroNode enclosingNode = inputNode;
		if (!contextualNodes.isEmpty()) {
			enclosingNode = contextualNodes.get(0);
		}
		if (enclosingNode != null) {
			NameService nameService = serviceProvider.getService(NameService.class);
			return nameService.getAvailableName(nodeType, enclosingNode);		
		}
		return "";
	}	
	
	@Override
	public List<AvroNode> getContextualNodes() {
		return Collections.unmodifiableList(contextualNodes);
	}

	@Override
	public List<SchemaNode> getSchemaNodes() {
		return Collections.unmodifiableList(schemaNodes);
	}

	@Override
	public SchemaRegistry getSchemaRegistry() {
		return schemaRegistry;
	}
	
	@Override
	public SchemaNodeRegistry getSchemaNodeRegistry() {
		return schemaNodeRegistry;
	}

	@Override
	public void addContextListener(AvroContextListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeContextListener(AvroContextListener listener) {
		listeners.remove(listener);
	}

	protected void notifyListenersOnRootNodeChanged(RootNode rootNode) {
		for (Object listener : listeners.getListeners()) {
			((AvroContextListener) listener).onRootNodeChanged(this, rootNode);
		}
	}
	
	protected void notifyListenersOnInputNodeChanged(AvroNode inputNode) {
		for (Object listener : listeners.getListeners()) {
			((AvroContextListener) listener).onInputNodeChanged(this, inputNode);
		}
	}
	
	protected void notifyListenersOnContextualNodesChanged(List<AvroNode> contextualNodes) {
		for (Object listener : listeners.getListeners()) {
			((AvroContextListener) listener).onContextualNodesChanged(this, contextualNodes);
		}
	}
	
	protected void notifyListenersOnSchemaNodesChanged(List<SchemaNode> schemaNodes) {
		for (Object listener : listeners.getListeners()) {
			((AvroContextListener) listener).onSchemaNodesChanged(this, schemaNodes);
		}
	}
	
	protected void notifyListenersOnContextDispose() {
		for (Object listener : listeners.getListeners()) {
			((AvroContextListener) listener).onContextDispose(this);
		}
	}
	
	@Override
	public void dispose() {
		notifyListenersOnContextDispose();
		schemaRegistry.dispose();
		schemaNodeRegistry.dispose();		
		searchNodeContext.dispose();
		contextualNodes.clear();
		schemaNodes.clear();
	}
	
}
