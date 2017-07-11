package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This defines the attribute keys for the studio schema editor.
 * 
 * @author timbault
 *
 */
public class StudioAttributes extends AvroAttributes {

	public static final String STUDIO_SCHEMA_FIELD = "talend.schema.field"; //$NON-NLS-1$
	
	public static final String KEY = STUDIO_SCHEMA_FIELD + ".key"; //$NON-NLS-1$

	public static final Class<Boolean> KEY_CLASS = Boolean.class;
	
	public static final String TYPE = STUDIO_SCHEMA_FIELD + ".type"; //$NON-NLS-1$
	
	public static final Class<StudioSchemaTypes> TYPE_CLASS = StudioSchemaTypes.class;
	
	public static final String NULLABLE = STUDIO_SCHEMA_FIELD + ".nullable"; //$NON-NLS-1$
	
	public static final Class<Boolean> NULLABLE_CLASS = Boolean.class;
	
	public static final String DATE_FORMAT = STUDIO_SCHEMA_FIELD + ".dateformat"; //$NON-NLS-1$
	
	public static final Class<String> DATE_FORMAT_CLASS = String.class;
	
	static {
		valueClasses.put(KEY, KEY_CLASS);
		valueClasses.put(TYPE, TYPE_CLASS);
		valueClasses.put(NULLABLE, NULLABLE_CLASS);
		valueClasses.put(DATE_FORMAT, DATE_FORMAT_CLASS);
	}
		
	public static final String[] CUSTOM_ATTRIBUTES = new String[] { KEY, TYPE, NULLABLE, DATE_FORMAT };
	
	public static final String[] SORTED_STUDIO_ATTRIBUTES = new String[] { NAME, KEY, TYPE, NULLABLE, DATE_FORMAT, DOC };
	
	public static final boolean isCustomAttribute(String attrName) {
		return AttributeUtil.contains(CUSTOM_ATTRIBUTES, attrName);
	}
	
	public static Class<?> getAttributeValueClass(String attributeName) {
		if (isCustomAttribute(attributeName)) {
			return valueClasses.get(attributeName);
		}
		return AvroAttributes.getAttributeValueClass(attributeName);
	}

	
}
