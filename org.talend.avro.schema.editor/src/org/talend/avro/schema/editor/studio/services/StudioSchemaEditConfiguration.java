package org.talend.avro.schema.editor.studio.services;

import java.util.Collection;
import java.util.Collections;

import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.context.services.ContextualServiceRegistry;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.EditorLayout;
import org.talend.avro.schema.editor.edit.SchemaEditorContentPart;
import org.talend.avro.schema.editor.edit.services.AvroSchemaEditorConfiguration;
import org.talend.avro.schema.editor.edit.services.IEditorService;
import org.talend.avro.schema.editor.edit.services.InitializationPhase;
import org.talend.avro.schema.editor.io.AvroSchemaGenerator;
import org.talend.avro.schema.editor.io.AvroSchemaParser;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.cmd.IAttributeCommandFactory;
import org.talend.avro.schema.editor.studio.attributes.StudioAttributeCommandFactory;
import org.talend.avro.schema.editor.studio.cmd.StudioSchemaCommandFactory;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class StudioSchemaEditConfiguration extends AvroSchemaEditorConfiguration {
	
	private ContextualServiceRegistry contextualServiceRegistry = new ContextualServiceRegistry();
	
	public StudioSchemaEditConfiguration() {
		super();
		init();
	}

	private void init() {
		contextualServiceRegistry.register(AvroSchemaController.class, StudioSchemaController.class);
		contextualServiceRegistry.register(IEditCommandFactory.class, StudioSchemaCommandFactory.class);
		contextualServiceRegistry.register(IAttributeCommandFactory.class, StudioAttributeCommandFactory.class);
	}
	
	@Override
	public void configureContentPart(SchemaEditorContentPart contentPart) {
	
		// set initial editor layout
		contentPart.setEditorLayout(EditorLayout.TREE);
		
		// change the master viewer input in order to hide the record node		
		SchemaViewer masterViewer = contentPart.getSchemaViewer(Kind.MASTER);
		AvroNode rootNode = masterViewer.getContent();
		AvroNode recordNode = rootNode.getChildren().get(0);
		masterViewer.setContent(recordNode);
		
	}
	
	@Override
	public Collection<Class<? extends IContextualService>> getProvidedServices() {
		return contextualServiceRegistry.getProvidedServices();
	}

	@Override
	public <T extends IContextualService> T createService(Class<T> serviceClass) {
		return contextualServiceRegistry.createService(serviceClass);
	}

	@Override
	public Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase) {
		return Collections.emptyList();
	}

	@Override
	public <T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase) {
		return null;
	}

	@Override
	public AvroSchemaParser getParser(AvroContext context) {		
		return new StudioSchemaParserImpl(context);
	}

	@Override
	public AvroSchemaGenerator getGenerator(AvroContext context) {		
		return new StudioSchemaGeneratorImpl(context);
	}
	
}
