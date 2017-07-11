package org.talend.avro.schema.editor.edit.services;

import java.util.Collection;
import java.util.Collections;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.EditAttributeInitializer;
import org.talend.avro.schema.editor.edit.EditorLayout;
import org.talend.avro.schema.editor.edit.IEditorConfiguration;
import org.talend.avro.schema.editor.edit.SchemaEditorContentPart;
import org.talend.avro.schema.editor.edit.dnd.AvroSchemaCopyStrategyProvider;
import org.talend.avro.schema.editor.edit.dnd.SchemaEditorDragAndDropPolicyConfiguration;
import org.talend.avro.schema.editor.edit.validator.AvroSchemaEditorNodeAttributesValidators;
import org.talend.avro.schema.editor.io.AvroSchemaGenerator;
import org.talend.avro.schema.editor.io.AvroSchemaGeneratorImpl;
import org.talend.avro.schema.editor.io.AvroSchemaParser;
import org.talend.avro.schema.editor.io.AvroSchemaParserImpl;
import org.talend.avro.schema.editor.preferences.AvroSchemaEditorPreferences;
import org.talend.avro.schema.editor.preferences.IEditPreferencesService;

public class AvroSchemaEditorConfiguration implements IEditorConfiguration {

	@Override
	public Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase) {
		return Collections.emptyList();
	}

	@Override
	public <T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase) {
		return null;
	}
	
	@Override
	public Collection<Class<? extends IContextualService>> getProvidedServices() {
		return Collections.emptyList();
	}

	@Override
	public <T extends IContextualService> T createService(Class<T> serviceClass) {
		return null;
	}

	@Override
	public void configureContextualServices(AvroContext context, IEditorServiceProvider services) {
		
		// configure controller
		AvroSchemaController schemaController = services.getService(AvroSchemaController.class);
		schemaController.setAttributeInitializer(new EditAttributeInitializer(context));
		schemaController.setAvroNodeValidators(new AvroSchemaEditorNodeAttributesValidators());
		schemaController.setDragAndDropPolicyConfiguration(new SchemaEditorDragAndDropPolicyConfiguration(context));
		schemaController.setCopyStrategyProvider(new AvroSchemaCopyStrategyProvider());		
		
	}

	@Override
	public void configureEditorServices(AvroContext context, IEditorServiceProvider services, InitializationPhase phase) {
		
		if (phase == InitializationPhase.PRE_UI) {
			
			// configure preferences service
			IEditPreferencesService preferencesService = services.getService(IEditPreferencesService.class);
			if (preferencesService != null) {
				configurePreferences(preferencesService);
			}			
			
		}
				
	}

	protected void configurePreferences(IEditPreferencesService preferencesService) {
		
		initBooleanPreference(AvroSchemaEditorPreferences.CUSTOM_PROPERTIES_EXPANDED_KEY, 
					AvroSchemaEditorPreferences.CUSTOM_PROPERTIES_EXPANDED_DEFAULT_VALUE,
					preferencesService);
		
		initBooleanPreference(AvroSchemaEditorPreferences.TYPE_PROPERTIES_EXPANDED_KEY, 
					AvroSchemaEditorPreferences.TYPE_PROPERTIES_EXPANDED_DEFAULT_VALUE,
					preferencesService);
		
		initBooleanPreference(AvroSchemaEditorPreferences.SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_KEY,
					AvroSchemaEditorPreferences.SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_DEFAULT_VALUE,
					preferencesService);
		
		initBooleanPreference(AvroSchemaEditorPreferences.SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_KEY,
					AvroSchemaEditorPreferences.SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_DEFAULT_VALUE,
					preferencesService);
		
		initIntegerPreference(AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_KEY,
					AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_DEFAULT_VALUE,
					preferencesService);
		
		initIntegerPreference(AvroSchemaEditorPreferences.HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_KEY,
				AvroSchemaEditorPreferences.HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_DEFAULT_VALUE,
				preferencesService);
		
		initBooleanPreference(AvroSchemaEditorPreferences.SHOW_TOOLTIP_IN_SCHEMA_VIEWER_KEY,
				AvroSchemaEditorPreferences.SHOW_TOOLTIP_IN_SCHEMA_VIEWER_DEFAULT_VALUE,
				preferencesService);
		
		initIntegerPreference(AvroSchemaEditorPreferences.ICONS_VERSION_KEY,
				AvroSchemaEditorPreferences.ICONS_VERSION_DEFAULT_VALUE,
				preferencesService);
		
	}
	
	protected void initBooleanPreference(String key, boolean defaultValue, IEditPreferencesService preferencesService) {
		if (!preferencesService.contains(key)) {
			preferencesService.setDefault(key, defaultValue);
		}
	}
	
	protected void initIntegerPreference(String key, int defaultValue, IEditPreferencesService preferencesService) {
		if (!preferencesService.contains(key)) {
			preferencesService.setDefault(key, defaultValue);
		}
	}
	
	@Override
	public void configureContentPart(SchemaEditorContentPart contentPart) {
		// set initial editor layout
		contentPart.setEditorLayout(EditorLayout.TREE_AND_ATTRIBUTES);
	}

	@Override
	public AvroSchemaParser getParser(AvroContext context) {
		return new AvroSchemaParserImpl(context);
	}

	@Override
	public AvroSchemaGenerator getGenerator(AvroContext context) {
		return new AvroSchemaGeneratorImpl(context);
	}
	
}
