package org.talend.avro.schema.editor.edit.services;

import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

/**
 * Service linked to an AvroSchemaEditor instance. It is a UI service.
 * 
 * @author timbault
 *
 */
public interface IEditorService extends IContextualService {
	
	/**
	 * Initialize the service with the given AvroSchemaInstance context.
	 * 
	 * @param editor
	 */
	void init(AvroSchemaEditor editor);
			
}
