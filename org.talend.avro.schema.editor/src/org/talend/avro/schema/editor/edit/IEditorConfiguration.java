package org.talend.avro.schema.editor.edit;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.services.IContextualServiceFactory;
import org.talend.avro.schema.editor.edit.services.IEditorServiceFactory;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.edit.services.InitializationPhase;
import org.talend.avro.schema.editor.io.AvroSchemaGenerator;
import org.talend.avro.schema.editor.io.AvroSchemaParser;

/**
 * 
 * @author timbault
 *
 */
public interface IEditorConfiguration extends IEditorServiceFactory, IContextualServiceFactory {
	
	AvroSchemaParser getParser(AvroContext context);
	
	AvroSchemaGenerator getGenerator(AvroContext context);
		
	void configureContextualServices(AvroContext context, IEditorServiceProvider services);
	
	void configureEditorServices(AvroContext context, IEditorServiceProvider services, InitializationPhase phase);
		
	void configureContentPart(SchemaEditorContentPart contentPart);	
	
}
