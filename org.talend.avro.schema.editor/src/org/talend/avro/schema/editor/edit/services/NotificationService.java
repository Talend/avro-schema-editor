package org.talend.avro.schema.editor.edit.services;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.services.IContextualService;

/**
 * Notification, Refresh, Reveal, Select services
 * 
 * @author timbault
 *
 */
public interface NotificationService extends IContextualService {
	
	void addObserver(NotificationObserver observer);
	
	void removeObserver(NotificationObserver observer);
	
	void notify(Object object);
	
	// TODO notify on attribute change
	// use a new method notify(object, info) where info provides nature of the modifications applied on the given object 
	
	void refresh();
	
	void refresh(Object object);	
	
	void reveal(Object object, AvroContext.Kind context);
	
	void select(Object object, AvroContext.Kind context);
		
	boolean setLock(boolean locked);
	
}
