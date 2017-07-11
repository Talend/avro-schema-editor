package org.talend.avro.schema.editor.edit.services;

/**
 * Observer of the {@link NotificationService}.
 * 
 * @author timbault
 * @see NotificationService
 *
 */
public interface NotificationObserver {

	void notify(Object object);
	
	void refresh();
	
	void refresh(Object object);		
	
}
