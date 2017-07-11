package org.talend.avro.schema.editor.edit.services;

import org.eclipse.core.runtime.ListenerList;
import org.talend.avro.schema.editor.context.AbstractContextualService;

public abstract class NotificationServiceImpl extends AbstractContextualService implements NotificationService {

	private ListenerList observers = new ListenerList();

	@Override
	public void addObserver(NotificationObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(NotificationObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void notify(Object object) {
		for (Object observer : observers.getListeners()) {
			((NotificationObserver) observer).notify(object);
		}
	}

	@Override
	public void refresh() {
		for (Object observer : observers.getListeners()) {
			((NotificationObserver) observer).refresh();
		}
	}

	@Override
	public void refresh(Object object) {
		for (Object observer : observers.getListeners()) {
			((NotificationObserver) observer).refresh(object);
		}
	}	

	@Override
	public boolean setLock(boolean locked) {
		return false;
	}
	
	@Override
	public void dispose() {
		observers.clear();
	}

}
