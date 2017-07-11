package org.talend.avro.schema.editor.edit;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;

public class EditorConfigurationLoader {

	private static final String EXTENSION_POINT = "editorConfiguration"; //$NON-NLS-1$

    private static final String ATT_CONFIGURATION = "configuration"; //$NON-NLS-1$

    private static final String ATT_CONTEXT_ID = "contextId"; //$NON-NLS-1$

	public IEditorConfiguration getEditorConfiguration(String contextId) {
		
		IEditorConfiguration editorConfiguration = null;
		
		IExtensionRegistry er = Platform.getExtensionRegistry();
        IExtensionPoint ep = er.getExtensionPoint(AvroSchemaEditorActivator.PLUGIN_ID, EXTENSION_POINT);
        if (ep == null) {
            return null;
        }

        IExtension[] exs = ep.getExtensions();

        for (int i = 0; i < exs.length; i++) {

            String extensionsNamespace = exs[i].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(extensionsNamespace);
            IConfigurationElement[] ces = exs[i].getConfigurationElements();

            for (IConfigurationElement configurationElement : ces) {

            	try {

            		 // context id
                    String ctxId = configurationElement.getAttribute(ATT_CONTEXT_ID);
                    if ((ctxId == null && contextId == null)
                            || (ctxId != null && ctxId.equals(contextId))) {
                    	
                    	String configurationClassName = configurationElement.getAttribute(ATT_CONFIGURATION);
                        if (configurationClassName == null) {
                            throw new IllegalArgumentException("You must provide an editor configuration class");                            
                        }
                        @SuppressWarnings("unchecked")
                        Class<? extends IEditorConfiguration> configurationClass = (Class<? extends IEditorConfiguration>) bundle
                                .loadClass(configurationClassName);
                        if (configurationClass == null
                                || !(IEditorConfiguration.class.isAssignableFrom(configurationClass))) {
                            throw new IllegalArgumentException("Invalid editor configuration class provided");
                        }

                        editorConfiguration = configurationClass.newInstance();
                    	
                    }
            		
            	}
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            	catch (InstantiationException e) {
            		e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                	e.printStackTrace();
                }

            }
            
        }
		
        return editorConfiguration;
        
	}
	
}
