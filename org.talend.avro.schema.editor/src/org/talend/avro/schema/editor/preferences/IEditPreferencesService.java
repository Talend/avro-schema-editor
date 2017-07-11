package org.talend.avro.schema.editor.preferences;

import org.talend.avro.schema.editor.edit.services.IEditorService;

public interface IEditPreferencesService extends IEditorService {

    boolean contains(String name);
  
    void store(String name, String value);
   
    void store(String name, boolean value);
   
    void store(String name, int value);
   
    void store(String name, double value);
    
    void setDefault(String name, String value);
    
    void setDefault(String name, boolean value);
    
    void setDefault(String name, int value);
    
    void setDefault(String name, double value);
    
    String getString(String name);
    
    boolean getBoolean(String name);
    
    int getInteger(String name);
    
    double getDouble(String name);
    
    void addPreferencesListener(IPreferencesListener listener);
   
    void removePreferencesListener(IPreferencesListener listener);
	
}
