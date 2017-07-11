package org.talend.avro.schema.editor.preferences;

import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

public class AvroSchemaEditorPreferences {

	public static final String CUSTOM_PROPERTIES_EXPANDED_KEY = "Custom Properties Expanded"; //$NON-NLS-1$
	
	public static final boolean CUSTOM_PROPERTIES_EXPANDED_DEFAULT_VALUE = true;
	
	public static final String TYPE_PROPERTIES_EXPANDED_KEY = "Type Properties Expanded"; //$NON-NLS-1$
	
	public static final boolean TYPE_PROPERTIES_EXPANDED_DEFAULT_VALUE = false;
	
	public static String getPropertiesExpandedKey(String attributeName) {
		switch (attributeName) {
		case AvroAttributes.CUSTOM_PROPERTIES:
			return CUSTOM_PROPERTIES_EXPANDED_KEY;
		case AvroAttributes.TYPE_PROPERTIES:
			return TYPE_PROPERTIES_EXPANDED_KEY;
		default:
			throw new IllegalArgumentException("Invalid attribute name");
		}
	}	
	
	public static final String SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_KEY = "Show element type in schema viewer"; //$NON-NLS-1$
	
	public static final boolean SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_DEFAULT_VALUE = true;
	
	public static final String SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_KEY = "Show element doc in schema viewer"; //$NON-NLS-1$
	
	public static final boolean SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_DEFAULT_VALUE = true;
	
	public static final String DOC_LENGTH_IN_SCHEMA_VIEWER_KEY = "Doc length in schema viewer"; //$NON-NLS-1$
	
	public static final int DOC_LENGTH_IN_SCHEMA_VIEWER_DEFAULT_VALUE = 50;
	
	public static final int DOC_FULL_LENGTH_IN_SCHEMA_VIEWER = -1;
	
	public static final String HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_KEY = "Horizontal space in schema viewer"; //$NON-NLS-1$
	
	public static final int HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_DEFAULT_VALUE = 4;
	
	public static final String SHOW_TOOLTIP_IN_SCHEMA_VIEWER_KEY = "Show tooltip in schema viewer"; //$NON-NLS-1$
	
	public static final boolean SHOW_TOOLTIP_IN_SCHEMA_VIEWER_DEFAULT_VALUE = true;
	
	public static final String ICONS_VERSION_KEY = "Icons version"; //$NON-NLS-1$
	
	public static final int ICONS_VERSION_DEFAULT_VALUE = 2;
	
}
