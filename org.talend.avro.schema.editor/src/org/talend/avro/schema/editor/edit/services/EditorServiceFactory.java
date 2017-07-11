package org.talend.avro.schema.editor.edit.services;

import java.util.Collection;

import org.talend.avro.schema.editor.preferences.AvroSchemaEditorPreferencesService;
import org.talend.avro.schema.editor.preferences.IEditPreferencesService;
import org.talend.avro.schema.editor.view.IViewService;
import org.talend.avro.schema.editor.view.ViewServiceImpl;
import org.talend.avro.schema.editor.viewer.attribute.view.AttributeViewService;
import org.talend.avro.schema.editor.viewer.attribute.view.AttributeViewServiceImpl;

public class EditorServiceFactory implements IEditorServiceFactory {

	private EditorServiceRegistry serviceRegistry = new EditorServiceRegistry();
	
	public EditorServiceFactory() {
		super();
		init();
	}

	private void init() {		
		serviceRegistry.register(IViewService.class, ViewServiceImpl.class, InitializationPhase.POST_UI);		
		serviceRegistry.register(IEditPreferencesService.class, AvroSchemaEditorPreferencesService.class, InitializationPhase.PRE_UI);		
		serviceRegistry.register(AttributeViewService.class, AttributeViewServiceImpl.class, InitializationPhase.POST_UI);
	}
	
	@Override
	public  Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase) {
		return serviceRegistry.getProvidedServices(phase);
	}

	@Override
	public <T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase) {
		return serviceRegistry.createService(serviceClass, phase);
	}

}
