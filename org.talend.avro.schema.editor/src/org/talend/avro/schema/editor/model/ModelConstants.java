package org.talend.avro.schema.editor.model;

/**
 * Defines all the constants used in the model.
 * 
 * @author timbault
 * @see NodeType
 *
 */
public interface ModelConstants {

	// NodeType constants
	
	public static final boolean NA = false;
	
	public static final boolean NAMED = true;
	
	public static final boolean UNNAMED = false;
	
	public static final boolean WITH_NAMESPACE = true;
	
	public static final boolean WITHOUT_NAMESPACE = false;
	
	public static final boolean WITH_PROPERTIES = true;
	
	public static final boolean WITHOUT_PROPERTIES = false;
	
	public static final boolean IS_LEAF = true;
	
	public static final boolean IS_NODE = false;
	
	public static final boolean MULTI_CHILDREN = true;
	
	public static final boolean SINGLE_CHILD = false;
	
	public static final boolean NO_CHILD = false;
	
	public static final boolean IS_REF = true;
	
	public static final boolean IS_NOT_REF = false;
	
	// constants for avro schema controller
	
	public static final int NONE = 0;
	
	public static final int REGISTER = 1;
	
	public static final int UNREGISTER = 1;
	
	public static final int VALIDATE = 1 << 1;
	
	public static final int REGISTER_AND_VALIDATE = REGISTER | VALIDATE;
		
	public static final int UNREGISTER_AND_VALIDATE = UNREGISTER | VALIDATE;
	
	public static final int FIRST_POSITION = 0;
	
	public static final int LAST_POSITION = -1;
	
}
