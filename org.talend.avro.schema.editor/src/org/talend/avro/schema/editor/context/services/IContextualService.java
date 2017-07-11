package org.talend.avro.schema.editor.context.services;

import org.talend.avro.schema.editor.context.AvroContext;

/*
 * Contextual service is a service linked to an avro context. it is not UI. It is not linked to an AvroSchemaEditor instance. 
 * 
 * @author timbault
 *
 */
public interface IContextualService {

	/**
	 * Initialize the service with the given context;
	 * 
	 * @param context
	 */
	void init(AvroContext context);
	
	/**
	 * Dispose the service.
	 */
	void dispose();
	
}
