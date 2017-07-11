package org.talend.avro.schema.editor.edit.actions;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.context.services.ServicesObserver;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.services.NotificationObserver;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;

/**
 * Base abstract implementation of a {@link ContextualAction}.
 * 
 * @author timbault
 *
 */
public abstract class ContextualActionImpl extends Action implements ContextualAction, NotificationObserver {

	private AvroContext context;
	
	private int notifications;
	
	private ServicesObserver servicesObserver;
	
	protected ContextualActionImpl(String text, int notifications) {
		this(text, IAction.AS_UNSPECIFIED, notifications);
	}
	
	protected ContextualActionImpl(String text, int style, int notifications) {
		super(text, style);		
		this.notifications = notifications;
	}
		
	public void init(final AvroContext context, boolean link) {
		this.context = context;
		if (context != null) {				
			updateEnableState(context);
			if (link) {
				this.context.addContextListener(this);	
				NotificationService notificationService = this.context.getService(NotificationService.class);
				if (notificationService == null) {
					servicesObserver = new ServicesObserver() {						
						@Override
						public void onServiceRegistered(IContextualService service) {
							if (NotificationService.class.isAssignableFrom(service.getClass())) {
								NotificationService notificationService = (NotificationService) service;
								notificationService.addObserver(ContextualActionImpl.this);
								context.removeServicesObserver(servicesObserver);
								servicesObserver = null;
							}
						}
					};
					context.addServicesObserver(servicesObserver);
				} else {
					notificationService.addObserver(this);
				}
			}
		}
	}
	
	protected void updateEnableState(AvroContext context) {
		updateEnableState(context, context.getContextualNodes());
	}
	
	protected void updateEnableState(AvroContext context, List<AvroNode> contextualNodes) {
		setEnabled(isEnabled(context, contextualNodes));
	}
	
	protected AvroContext getContext() {
		return context;
	}
	
	protected abstract boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes);
	
	@Override
	public void onRootNodeChanged(AvroContext context, RootNode rootNode) {
		updateEnableState(context);
	}

	@Override
	public void onInputNodeChanged(AvroContext context, AvroNode inputNode) {
		updateEnableState(context);
	}

	@Override
	public void onContextualNodesChanged(AvroContext context, List<AvroNode> contextualNodes) {
		updateEnableState(context, contextualNodes);
	}
	
	@Override
	public void onSchemaNodesChanged(AvroContext context, List<SchemaNode> schemaNodes) {
		// nothing to do
	}

	protected List<AvroNode> getContextualNodes() {
		if (context != null) {
			return context.getContextualNodes();
		}
		return Collections.emptyList();
	}
	
	protected IEditCommandFactory getCommandFactory() {
		if (context == null) {
			throw new IllegalStateException("Cannot execute action " + getText());
		}		
		return context.getService(IEditCommandFactory.class);
	}
	
	protected AvroSchemaController getController() {
		if (context == null) {
			throw new IllegalStateException("Cannot execute action " + getText());
		}		
		return context.getService(AvroSchemaController.class);
	}
	
	protected void execute(IEditCommand command) {
		if (context == null) {
			throw new UnsupportedOperationException("Cannot exeucte action " + getText());
		}
		context.getService(ICommandExecutor.class).execute(command);		
	}
	
	protected int getNotifications() {
		return notifications;
	}
	
	@Override
	public void onContextDispose(AvroContext context) {
		if (this.context == context) {
			clean();
		}
	}

	@Override
	public void notify(Object object) {
		// nothing to do
	}

	@Override
	public void refresh() {
		updateEnableState(context);
	}

	@Override
	public void refresh(Object object) {
		updateEnableState(context);
	}
	
	protected void clean() {
		this.context.removeContextListener(this);
		if (servicesObserver != null) {
			this.context.removeServicesObserver(servicesObserver);
			servicesObserver = null;
		}
		NotificationService notificationService = this.context.getService(NotificationService.class);
		if (notificationService != null) {
			notificationService.removeObserver(this);
		}
		this.context = null;
	}
	
	public void dispose() {
		if (this.context != null) {
			clean();
		}
	}

}
