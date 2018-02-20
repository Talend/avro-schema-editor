package org.talend.avro.schema.editor.model.attributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines all the attributes (standard and custom) used in the base avro schema editor
 * 
 * @author timbault
 *
 */
public class AvroAttributes {

	public static final String PATH = "path"; //$NON-NLS-1$
	
	public static final Class<String> PATH_CLASS = String.class;
	
	public static final String NAME_SPACE = "namespace"; //$NON-NLS-1$
	
	public static final Class<String> NAME_SPACE_CLASS = String.class;
	
	public static final String NAME = "name"; //$NON-NLS-1$
	
	public static final Class<String> NAME_CLASS = String.class;
	
	public static final String DOC = "doc"; //$NON-NLS-1$
	
	public static final Class<String> DOC_CLASS = String.class;
	
	public static final String OPTIONAL = "optional"; //$NON-NLS-1$
	
	public static final Class<Boolean> OPTIONAL_CLASS = Boolean.class;
	
	public static final String PRIMITIVE_TYPE = "type"; //$NON-NLS-1$
	
	public static final Class<PrimitiveTypes> PRIMITIVE_TYPE_CLASS = PrimitiveTypes.class;
	
	public static final String DEFAULT = "default"; //$NON-NLS-1$
	
	public static final Class<DefaultValue> DEFAULT_CLASS = DefaultValue.class;
	
	public static final String TYPE_PROPERTIES = "type properties"; //$NON-NLS-1$
	
	public static final Class<CustomProperties> TYPE_PROPERTIES_CLASS = CustomProperties.class;
	
	public static final String ARRAY_OR_MAP = "array or map"; //$NON-NLS-1$
	
	public static final Class<ArrayOrMapValue> ARRAY_OR_MAP_CLASS = ArrayOrMapValue.class;
	
	public static final String CHOICE_TYPE = "choice type"; //$NON-NLS-1$

	public static final Class<Boolean> CHOICE_TYPE_CLASS = Boolean.class;
	
	public static final String SYMBOLS = "symbols"; //$NON-NLS-1$
	
	public static final Class<StringList> SYMBOLS_CLASS = StringList.class;
	
	public static final String ALIASES = "aliases"; //$NON-NLS-1$
		
	public static final Class<StringList> ALIASES_CLASS = StringList.class;
	
	public static final String SIZE = "size"; //$NON-NLS-1$
	
	public static final Class<Integer> SIZE_CLASS = Integer.class;
	
	public static final String CUSTOM_PROPERTIES = "custom properties"; //$NON-NLS-1$
	
	public static final Class<CustomProperties> CUSTOM_PROPERTIES_CLASS = CustomProperties.class;
	
	public static final String[] SORTED_ATTRIBUTES = 
			new String[] { 
					PATH, 
					NAME_SPACE, 
					NAME, 
					DOC, 
					ARRAY_OR_MAP,
					PRIMITIVE_TYPE, 
					TYPE_PROPERTIES,
					DEFAULT,
					OPTIONAL,
					CHOICE_TYPE, 
					SYMBOLS, 
					ALIASES, 
					SIZE,
					CUSTOM_PROPERTIES };		
	
	protected static Map<String, Class<?>> valueClasses = new HashMap<>();
	
	static {
		valueClasses.put(PATH, PATH_CLASS);
		valueClasses.put(NAME_SPACE, NAME_SPACE_CLASS);
		valueClasses.put(NAME, NAME_CLASS);
		valueClasses.put(DOC, DOC_CLASS);
		valueClasses.put(OPTIONAL, OPTIONAL_CLASS);
		valueClasses.put(PRIMITIVE_TYPE, PRIMITIVE_TYPE_CLASS);
		valueClasses.put(DEFAULT, DEFAULT_CLASS);
		valueClasses.put(TYPE_PROPERTIES, TYPE_PROPERTIES_CLASS);
		valueClasses.put(ARRAY_OR_MAP, ARRAY_OR_MAP_CLASS);
		valueClasses.put(CHOICE_TYPE, CHOICE_TYPE_CLASS);
		valueClasses.put(SYMBOLS, SYMBOLS_CLASS);
		valueClasses.put(ALIASES, ALIASES_CLASS);
		valueClasses.put(SIZE, SIZE_CLASS);
		valueClasses.put(CUSTOM_PROPERTIES, CUSTOM_PROPERTIES_CLASS);
	}
	
	public static Class<?> getAttributeValueClass(String attributeName) {
		return valueClasses.get(attributeName);
	}
	
}
