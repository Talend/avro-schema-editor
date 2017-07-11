package org.talend.avro.schema.editor.preferences;

public interface IPreferencesListener {

	void onIntChange(String key, int newValue);

    void onDoubleChange(String key, double newValue);

    void onStringChange(String key, String newValue);

    void onBooleanChange(String key, boolean newValue);
	
}
