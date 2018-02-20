package org.talend.avro.schema.editor.model.attributes;

/**
 * Represent the default value of a field in a record.
 *  
 * @author timbault
 *
 */
public class DefaultValue {

	public static final String EMPTY = ""; //$NON-NLS-1$
	
	/**
	 * Indicates if a default value is defined.
	 */
	private boolean defined;
	
	/**
	 * The default value as string
	 */
	private String value;

	public DefaultValue() {
		this(false, EMPTY);
	}

	public DefaultValue(boolean defined, String value) {
		super();
		this.defined = defined;
		this.value = value;
	}

	public boolean isDefined() {
		return defined;
	}

	public void setDefined(boolean defined) {
		this.defined = defined;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public DefaultValue getACopy() {
		return new DefaultValue(defined, value);
	}
	
	public void apply(DefaultValue defaultValue) {
		this.defined = defaultValue.isDefined();
		this.value = defaultValue.getValue();
	}
	
}
