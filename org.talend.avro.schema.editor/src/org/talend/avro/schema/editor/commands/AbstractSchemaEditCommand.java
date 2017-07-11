package org.talend.avro.schema.editor.commands;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.services.NotificationService;

/**
 * Base abstract implementation of an {@link IEditCommand}. It implements some convenient methods for the notifications.
 * 
 * @author timbault
 *
 */
public abstract class AbstractSchemaEditCommand implements IEditCommand {	

	private AvroContext context;
		
	private int notifications;
	
	protected AbstractSchemaEditCommand(AvroContext context, int notifications) {
		super();
		this.context = context;
		this.notifications = notifications;
	}
	
	void setContext(AvroContext context) {
		this.context = context;
	}
	
	protected AvroContext getContext() {
		return context;
	}

	protected AvroSchemaController getController() {
		return context.getService(AvroSchemaController.class);
	}
	
	protected NotificationService getNotificationService() {
		return context.getService(NotificationService.class);
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
	
}
