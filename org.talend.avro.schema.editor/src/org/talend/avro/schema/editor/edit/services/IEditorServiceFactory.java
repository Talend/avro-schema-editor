package org.talend.avro.schema.editor.edit.services;

import java.util.Collection;

/**
 * Avro Schema editor service factory.
 * 
 * @author timbault
 *
 */
public interface IEditorServiceFactory {

	/**
	 * Return the service classes this factory can create for a specified configuration phase.
	 * 
	 * @param phase
	 * @return
	 */
	Collection<Class<? extends IEditorService>> getProvidedServices(InitializationPhase phase);
	
	/**
	 * Create an implementation of the given service class.
	 * 
	 * @param serviceClass
	 * @param phase
	 * @return
	 */
	<T extends IEditorService> T createService(Class<T> serviceClass, InitializationPhase phase);
	
}
