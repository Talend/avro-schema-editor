package org.talend.avro.schema.editor.preferences;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.talend.avro.schema.editor.edit.services.AbstractEditorService;

public class AvroSchemaEditorPreferencesService extends AbstractEditorService implements IEditPreferencesService {
	
    private static IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();

    private ListenerList listeners = new ListenerList();        

	@Override
    public boolean contains(String name) {
        return preferenceStore.contains(name);
    }

    public void store(String name, double value) {
    	preferenceStore.setValue(name, value);
        fireOnDoubleChange(name, value);
    }

    public void store(String name, String value) {
    	preferenceStore.setValue(name, value);
        fireOnStringChange(name, value);
    }

    public void store(String name, boolean value) {
    	preferenceStore.setValue(name, value);
        fireOnBooleanChange(name, value);
    }

    public void store(String name, int value) {
    	preferenceStore.setValue(name, value);
        fireOnIntegerChange(name, value);
    }
    
    @Override
	public void setDefault(String name, String value) {
    	preferenceStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, boolean value) {
		preferenceStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, int value) {
		preferenceStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, double value) {
		preferenceStore.setDefault(name, value);
	}

	@Override
    public String getString(String name) {
    	return preferenceStore.getString(name);
    }

    @Override
    public boolean getBoolean(String name) {
    	return preferenceStore.getBoolean(name);
    }

    @Override
    public int getInteger(String name) {
        return preferenceStore.getInt(name);
    }

    @Override
    public double getDouble(String name) {
    	return preferenceStore.getDouble(name);
    }

    @Override
    public void addPreferencesListener(IPreferencesListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePreferencesListener(IPreferencesListener listener) {
        listeners.remove(listener);
    }

    private void fireOnIntegerChange(String key, int newValue) {
        for (Object listener : listeners.getListeners()) {
            ((IPreferencesListener) listener).onIntChange(key, newValue);
        }
    }

    private void fireOnDoubleChange(String key, double newValue) {
        for (Object listener : listeners.getListeners()) {
            ((IPreferencesListener) listener).onDoubleChange(key, newValue);
        }
    }

    private void fireOnStringChange(String key, String newValue) {
        for (Object listener : listeners.getListeners()) {
            ((IPreferencesListener) listener).onStringChange(key, newValue);
        }
    }

    private void fireOnBooleanChange(String key, boolean newValue) {
        for (Object listener : listeners.getListeners()) {
            ((IPreferencesListener) listener).onBooleanChange(key, newValue);
        }
    }

    @Override
    public void dispose() {
    	listeners.clear();
    }
    
}
