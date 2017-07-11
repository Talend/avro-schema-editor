package org.talend.avro.schema.editor.commands;

import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.context.AvroContextListener;
import org.talend.avro.schema.editor.context.CopyContext;
import org.talend.avro.schema.editor.context.SearchNodeContext;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.context.services.ServicesObserver;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.services.NotificationObserver;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.registry.SchemaRegistry;

/**
 * Base implementation of a {@link CompositeCommand}.
 * <p>
 * This command handles an internal context which is passed to the encapsulated commands.
 * The aim of this internal context is to provide to the child commands a dummy notification service in order to avoid to many refreshes.
 * <p>
 * The parent composite command is responsible for final notification.
 * 
 * @author timbault
 *
 */
public class SchemaEditCompositeCommand extends CompositeCommand {

	private AvroContext context;
		
	private AvroContext internalContext;
	
	private int notifications;
	
	public SchemaEditCompositeCommand(String label, AvroContext context, int notifications) {
		super(label);
		this.context = context;
		this.internalContext = new CompositeCommandContext(context);
		this.notifications = notifications;
	}
		
	@Override
	public void addCommand(IEditCommand command) {
		super.addCommand(command);
		if (command instanceof AbstractSchemaEditCommand) {
			// change the context
			((AbstractSchemaEditCommand) command).setContext(internalContext);
		}
	}

	protected AvroContext getContext() {
		return context;
	}

	protected AvroContext getInternalContext() {
		return internalContext;
	}

	protected NotificationService getNotificationService() {
		return context.getService(NotificationService.class);
	}
	
	@Override
	public void run() {		
		super.run();
		doNotifications();
	}

	@Override
	public void undo() {
		super.undo();
		doNotifications();
	}

	@Override
	public void redo() {
		run();
	}
	
	protected void doNotifications(Object notifiedObject, Object revealedObject) {
		doNotify(notifiedObject);
		doRefresh(notifiedObject);
		doReveal(revealedObject);
		doSelect(revealedObject);
	}
	
	protected void doNotify(Object notifiedObject) {
		if ((notifications & Notifications.NOTIFY) != 0) {
			getNotificationService().notify(notifiedObject);
		}
	}
	
	protected void doRefresh(Object refreshedObject) {
		if ((notifications & Notifications.REFRESH) != 0) {
			getNotificationService().refresh(refreshedObject);
		}
	}
	
	protected void doReveal(Object revealedObject) {
		if (revealedObject != null && (notifications & Notifications.REVEAL) != 0) {
			Kind contextKind = Notifications.getContextKind(notifications);
			getNotificationService().reveal(revealedObject, contextKind);
		}
	}
	
	protected void doSelect(Object selectedObject) {
		if (selectedObject != null && (notifications & Notifications.SELECT) != 0) {
			Kind contextKind = Notifications.getContextKind(notifications);
			getNotificationService().select(selectedObject, contextKind);
		}
	}
	
	protected void doNotifications(Object notifiedObject) {
		doNotifications(notifiedObject, null);
	}
	
	protected void doNotifications() {
		doNotifications(null, null);
	}
	
	/**
	 * Internal context only used by the child commands.
	 * 
	 * @author timbault
	 *
	 */
	private class CompositeCommandContext implements AvroContext {

		private AvroContext encapsulatedContext;
		
		/**
		 * Dummy notification service only used by the child commands.
		 */
		private NotificationService internalNotificationService;
		
		public CompositeCommandContext(AvroContext encapsulatedContext) {
			super();
			this.encapsulatedContext = encapsulatedContext;
			this.internalNotificationService = new NotificationService() {
				
				@Override
				public void init(AvroContext context) {
					// 
				}
				
				@Override
				public void dispose() {
					// 
				}
				
				@Override
				public boolean setLock(boolean locked) {
					return false;
				}
				
				@Override
				public void select(Object object, Kind context) {
					
				}
				
				@Override
				public void reveal(Object object, Kind context) {
					
				}
				
				@Override
				public void refresh(Object object) {
					
				}
				
				@Override
				public void refresh() {
					
				}
				
				@Override
				public void notify(Object object) {
					
				}

				@Override
				public void addObserver(NotificationObserver observer) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void removeObserver(NotificationObserver observer) {
					throw new UnsupportedOperationException();
				}
				
			};
		}

		@Override
		public <T extends IContextualService> T getService(Class<T> serviceClass) {
			if (NotificationService.class.isAssignableFrom(serviceClass)) {
				return (T) internalNotificationService;
			}
			return encapsulatedContext.getService(serviceClass);
		}

		@Override
		public void addServicesObserver(ServicesObserver observer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeServicesObserver(ServicesObserver observer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Kind getKind() {
			return encapsulatedContext.getKind();
		}

		@Override
		public String getId() {
			return "org.talend.avro.schema.editor.commands.CompositeCommandContext";//$NON-NLS-1$
		}

		@Override
		public AvroContext getMaster() {
			return encapsulatedContext.getMaster();
		}

		@Override
		public AvroContext getSlave() {
			return encapsulatedContext.getSlave();
		}

		@Override
		public boolean isMaster() {
			return encapsulatedContext.isMaster();
		}

		@Override
		public boolean isSlave() {
			return encapsulatedContext.isSlave();
		}

		@Override
		public RootNode getRootNode() {
			return encapsulatedContext.getRootNode();
		}

		@Override
		public AvroNode getInputNode() {
			return encapsulatedContext.getInputNode();
		}

		@Override
		public List<AvroNode> getContextualNodes() {
			return encapsulatedContext.getContextualNodes();
		}
		
		@Override
		public List<SchemaNode> getSchemaNodes() {
			return encapsulatedContext.getSchemaNodes();
		}

		@Override
		public String getAvailableName(NodeType nodeType) {
			return encapsulatedContext.getAvailableName(nodeType);
		}

		@Override
		public String getDefaultNameSpace() {
			return encapsulatedContext.getDefaultNameSpace();
		}

		@Override
		public String getEnclosingNameSpace() {
			return encapsulatedContext.getEnclosingNameSpace();
		}

		@Override
		public PrimitiveType getDefaultPrimitiveType() {
			return encapsulatedContext.getDefaultPrimitiveType();
		}

		@Override
		public SchemaRegistry getSchemaRegistry() {
			return encapsulatedContext.getSchemaRegistry();
		}
		
		@Override
		public SchemaNodeRegistry getSchemaNodeRegistry() {
			return encapsulatedContext.getSchemaNodeRegistry();
		}

		@Override
		public CopyContext getCopyContext() {
			return encapsulatedContext.getCopyContext();
		}

		@Override
		public SearchNodeContext getSearchNodeContext() {
			return encapsulatedContext.getSearchNodeContext();
		}

		@Override
		public CustomAttributeConfiguration getCustomAttributeConfiguration() {
			return encapsulatedContext.getCustomAttributeConfiguration();
		}

		@Override
		public void addContextListener(AvroContextListener listener) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeContextListener(AvroContextListener listener) {
			throw new UnsupportedOperationException();
		}	
		
		@Override
		public void dispose() {
			// nothing to do
		}

	}
		
}
