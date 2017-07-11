package org.talend.avro.schema.editor.context.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContextualServiceRegistry {

	private Map<Class<? extends IContextualService>, Class<? extends IContextualService>> serviceClasses = new HashMap<>();
	
	public ContextualServiceRegistry() {
		super();
	}

	public void register(Class<? extends IContextualService> serviceClass, Class<? extends IContextualService> serviceImplClass) {
		serviceClasses.put(serviceClass, serviceImplClass);
	}
	
	public  Collection<Class<? extends IContextualService>> getProvidedServices() {
		return serviceClasses.keySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends IContextualService> T createService(Class<T> serviceClass) {
		T serviceImpl = null;		
		Class<? extends IContextualService> serviceImplClass = serviceClasses.get(serviceClass);
		if (serviceImplClass == null) {
			throw new IllegalArgumentException("Cannot create service " + serviceClass.toString());
		}
		try {
			serviceImpl = (T) serviceImplClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return serviceImpl;
	}

}
