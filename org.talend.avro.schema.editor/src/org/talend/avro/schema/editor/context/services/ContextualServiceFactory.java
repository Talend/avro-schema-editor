package org.talend.avro.schema.editor.context.services;

import java.util.Collection;

import org.talend.avro.schema.editor.commands.CommandExecutor;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.AvroSchemaEditorController;
import org.talend.avro.schema.editor.edit.cmd.AvroSchemaEditCommandFactory;
import org.talend.avro.schema.editor.edit.services.AvroNameService;
import org.talend.avro.schema.editor.edit.services.NameService;
import org.talend.avro.schema.editor.model.attributes.cmd.AvroAttributeCommandFactoryImpl;
import org.talend.avro.schema.editor.model.attributes.cmd.IAttributeCommandFactory;
import org.talend.avro.schema.editor.model.path.AvroSchemaPathService;
import org.talend.avro.schema.editor.model.path.PathService;

public class ContextualServiceFactory implements IContextualServiceFactory {

	private ContextualServiceRegistry serviceRegistry = new ContextualServiceRegistry();
	
	public ContextualServiceFactory() {
		super();
		init();
	}

	private void init() {
		serviceRegistry.register(ICommandExecutor.class, CommandExecutor.class);
		serviceRegistry.register(AvroSchemaController.class, AvroSchemaEditorController.class);
		serviceRegistry.register(IEditCommandFactory.class, AvroSchemaEditCommandFactory.class);
		serviceRegistry.register(IAttributeCommandFactory.class, AvroAttributeCommandFactoryImpl.class);
		serviceRegistry.register(PathService.class, AvroSchemaPathService.class);
		serviceRegistry.register(NameService.class, AvroNameService.class);
	}
	
	@Override
	public  Collection<Class<? extends IContextualService>> getProvidedServices() {
		return serviceRegistry.getProvidedServices();
	}

	@Override
	public <T extends IContextualService> T createService(Class<T> serviceClass) {
		return serviceRegistry.createService(serviceClass);
	}

}
