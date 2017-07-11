package org.talend.avro.schema.editor.edit.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.context.services.ServicesObserver;

/**
 * Base implementation of a service provider for the avro schema editor.
 * 
 * @author timbault
 *
 */
public class AvroSchemaEditorServiceProvider implements IEditorServiceProvider {

	private Map<Class<? extends IContextualService>, IContextualService> services = new HashMap<>();
		
	private ListenerList observers = new ListenerList();
	
	public AvroSchemaEditorServiceProvider() {
		super();
	}

	@Override
	public void addServicesObserver(ServicesObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeServicesObserver(ServicesObserver observer) {
		observers.remove(observer);
	}
	
	protected void notifyObserversOnServiceRegistered(IContextualService service) {
		for (Object observer : observers.getListeners()) {
			((ServicesObserver) observer).onServiceRegistered(service);
		}
	}
	
	public <T extends IContextualService> void registerService(Class<T> serviceClass, T service) {
		services.put(serviceClass, service);
		notifyObserversOnServiceRegistered(service);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IContextualService> T getService(Class<T> serviceClass) {
		IContextualService service = services.get(serviceClass);
		if (service == null) {
			// try to get a registered service assignable from specified class
			for (Map.Entry<Class<? extends IContextualService>, IContextualService> entry : services.entrySet()) {
				Class<? extends IContextualService> registeredServiceClass = entry.getKey();
				if (serviceClass.isAssignableFrom(registeredServiceClass)) {
					service = entry.getValue();
					break;
				}
			}
		}
		return (T) service;
	}
	
	@Override
	public IMenuService getMenuService() {
		return (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
	}

	@Override
	public void dispose() {
		for (IContextualService service : services.values()) {
			service.dispose();
		}
		services.clear();
	}
	
}
