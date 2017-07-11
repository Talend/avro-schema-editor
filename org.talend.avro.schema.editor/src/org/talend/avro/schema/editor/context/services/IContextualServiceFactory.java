package org.talend.avro.schema.editor.context.services;

import java.util.Collection;

/**
 * Avro contextual service factory.
 * 
 * @author timbault
 *
 */
public interface IContextualServiceFactory {

	/**
	 * Return the service classes this factory can create.
	 * 
	 * @param phase
	 * @return
	 */
	Collection<Class<? extends IContextualService>> getProvidedServices();
	
	/**
	 * Create an implementation of the given service class.
	 * 
	 * @param serviceClass
	 * @return
	 */
	<T extends IContextualService> T createService(Class<T> serviceClass);
	
}
