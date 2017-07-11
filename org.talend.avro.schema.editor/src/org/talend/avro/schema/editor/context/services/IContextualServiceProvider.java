package org.talend.avro.schema.editor.context.services;

/**
 * Provides contextual services, i.e. services linked to an avro context. 
 * Contextual services are not linked to an AvroSchemaEditor instance, they are not UI.
 * 
 * @author timbault
 *
 */
public interface IContextualServiceProvider {

	void addServicesObserver(ServicesObserver observer);
	
	void removeServicesObserver(ServicesObserver observer);
	
	/**
	 * Get an instance of the specified service class.
	 * 
	 * @param serviceClass
	 * @return
	 */
	<T extends IContextualService> T getService(Class<T> serviceClass);
	
	/**
	 * Dispose all the services provided.
	 */
	void dispose();
	
}
