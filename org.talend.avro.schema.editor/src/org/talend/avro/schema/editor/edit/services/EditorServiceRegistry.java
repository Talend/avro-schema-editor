package org.talend.avro.schema.editor.edit.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EditorServiceRegistry {

	private Map<InitializationPhase, Map<Class<? extends IEditorService>, Class<? extends IEditorService>>> serviceClasses = new HashMap<>();
	
	public EditorServiceRegistry() {
		super();
	}

	public void register(Class<? extends IEditorService> serviceClass, Class<? extends IEditorService> serviceImplClass, InitializationPhase phase) {
		Map<Class<? extends IEditorService>, Class<? extends IEditorService>> phaseServices = serviceClasses.get(phase);
		if (phaseServices == null) {
			phaseServices = new HashMap<>();
			serviceClasses.put(phase, phaseServices);
		}
		phaseServices.put(serviceClass, serviceImplClass);
	}
	
	public  Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase) {
		Map<Class<? extends IEditorService>, Class<? extends IEditorService>> phaseServices = serviceClasses.get(phase);
		if (phaseServices != null) {
			return phaseServices.keySet();
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public <T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase) {
		T serviceImpl = null;
		Map<Class<? extends IEditorService>, Class<? extends IEditorService>> phaseServices = serviceClasses.get(phase);
		if (phaseServices == null) {
			throw new IllegalArgumentException("Invalid phase");
		}
		Class<? extends IEditorService> serviceImplClass = phaseServices.get(serviceClass);
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
