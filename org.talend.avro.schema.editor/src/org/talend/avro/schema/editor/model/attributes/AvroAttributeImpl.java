package org.talend.avro.schema.editor.model.attributes;

import org.eclipse.core.runtime.ListenerList;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Base abstract implementation of an avro attribute.
 * 
 * @author timbault
 *
 * @param <T>
 */
public abstract class AvroAttributeImpl<T> implements AvroAttribute<T> {

	private AvroNode node;
	
	private String name;

	private Class<T> valueClass;
	
	private T value;
		
	private boolean visible = true;
	
	private boolean enabled = true;
	
	private ListenerList listeners = new ListenerList();
	
	protected AvroAttributeImpl(AvroNode node, String name, Class<T> valueClass, T value) {
		super();
		this.node = node;
		this.name = name;
		this.valueClass = valueClass;
		this.value = value;
	}

	@Override
	public AvroNode getHolder() {
		return node;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getValueClass() {
		return valueClass;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Specified value has not a valid class");
		}
		T oldValue = this.value;
		this.value = value;
		notifyListenerOnAttributeChange(oldValue, value);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void addListener(AttributeListener<T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(AttributeListener<T> listener) {
		listeners.remove(listener);
	}
	
	@SuppressWarnings("unchecked")
	protected void notifyListenerOnAttributeChange(T oldValue, T newValue) {
		for (Object listener : listeners.getListeners()) {
			((AttributeListener<T>) listener).onAttributeValueChanged(this, oldValue, newValue);
		}
	}
	
}
