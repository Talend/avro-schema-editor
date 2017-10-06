package org.talend.avro.schema.editor;

import java.util.HashSet;
import java.util.Set;

import org.talend.avro.schema.editor.model.NodeType;

public class AvroSchemaEditorImages {

	public static final String NONE = "None"; //$NON-NLS-1$
	
	public static final String SYNCHRONIZE_IMAGE = "SynchronizeImage"; //$NON-NLS-1$
	
	public static final String INCREASE_TREE_LEVEL_IMAGE = "IncreaseTreeLevelImage"; //$NON-NLS-1$
	
	public static final String DECREASE_TREE_LEVEL_IMAGE = "DecreaseTreeLevelImage"; //$NON-NLS-1$	
	
	public static final String CLEAR = "clear"; //$NON-NLS-1$
	
	public static final String ERROR_OVERLAY = "ErrorOverlayr"; //$NON-NLS-1$
	
	public static final String ELEMENT = "Element"; //$NON-NLS-1$
	
	public static final String BUTTON_EXPAND = "ButtonExpand"; //$NON-NLS-1$

	public static final String BUTTON_COLLAPSE = "ButtonCollapse"; //$NON-NLS-1$
	
	public static final String NAME_SPACE = "NameSpace"; //$NON-NLS-1$
	
	public static final String ADD_ELEMENT = "AddElement"; //$NON-NLS-1$
	
	public static final String REMOVE_ELEMENT = "RemoveElement"; //$NON-NLS-1$
	
	public static final String MOVE_UP = "MoveUp"; //$NON-NLS-1$
	
	public static final String MOVE_DOWN = "MoveDown"; //$NON-NLS-1$
	
	public static final String CONFIGURE = "Configure"; //$NON-NLS-1$
	
	public static final String CHECKED = "Checked"; //$NON-NLS-1$
	
	public static final String UNCHECKED = "Unchecked"; //$NON-NLS-1$
	
	public static final String WITH_COLUMNS_DISPLAY_MODE = "WithColumnsDisplayMode"; //$NON-NLS-1$
	
	public static final String WITHOUT_COLUMNS_DISPLAY_MODE = "WithoutColumnsDisplayMode"; //$NON-NLS-1$
	
	public static final String COPY = "copy"; //$NON-NLS-1$
	
	public static final String PASTE = "paste"; //$NON-NLS-1$
	
	public static final String UNDO = "undo"; //$NON-NLS-1$
	
	public static final String REDO = "redo"; //$NON-NLS-1$
	
	public static final String SCHEMA_FILE = "schemaFile"; //$NON-NLS-1$
	
	public static final String SCHEMA_REGISTRY = "schemaRegistry"; //$NON-NLS-1$
	
	//***********************************************************************************************
	
	private static final String ELEM = "elem"; //$NON-NLS-1$
	
	private static final String OPTIONAL_LABEL = "optional"; //$NON-NLS-1$
	
	private static final boolean OPTIONAL = true;
	
	private static final String SEP = "/"; //$NON-NLS-1$
	
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private static final String DOT = "_"; //$NON-NLS-1$
	
	private static final String ELEM_PATH = ELEM + SEP;
	
	private static final String EXTENSION = ".png"; //$NON-NLS-1$
	
	private static Set<ImageDef> imageDefinitions = new HashSet<>();
	
	static {
		registerDefaultImageDefs(NodeType.ARRAY, NodeType.ENUM, NodeType.FIELD, NodeType.MAP, NodeType.RECORD);
		registerImageDefs(1, NodeType.ARRAY, NodeType.ENUM, NodeType.FIELD, NodeType.MAP, NodeType.RECORD);
		registerImageDefs(2, NodeType.ARRAY, NodeType.ENUM, NodeType.FIELD, NodeType.MAP, NodeType.RECORD, NodeType.FIXED, NodeType.PRIMITIVE_TYPE, NodeType.UNION, NodeType.ROOT);
		registerImageDefs(2, OPTIONAL, NodeType.FIELD, NodeType.ARRAY, NodeType.MAP);
	}
	
	private static final void registerDefaultImageDefs(NodeType... types) {
		registerImageDefs(-1, types);
	}
		
	private static final void registerImageDefs(int version, NodeType... types) {
		for (NodeType type : types) {
			registerImageDef(type, false, version);
		}
	}
	
	private static final void registerImageDefs(int version, boolean optional, NodeType... types) {
		for (NodeType type : types) {
			registerImageDef(type, optional, version);
		}
	}
	
	private static final void registerImageDef(NodeType type, boolean optional, int version) {
		ImageDef def = new ImageDef(type, optional, version);
		imageDefinitions.add(def);
	}
	
	public static final boolean isDefined(NodeType type, boolean optional, int version) {
		ImageDef def = new ImageDef(type, optional, version);
		return imageDefinitions.contains(def);
	}
	
	public static final String getElementImageKey(NodeType type, boolean optional, int version) {
		return ELEMENT + getTypeStr(type) + getOptionalStr(optional) + getVersionStr(version);
	}
	
	public static final String getDefaultElementImageKey(int version) {
		return ELEMENT + getVersionStr(version);
	}
	
	public static final String getElementImagePath(NodeType type, boolean optional, int version) {
		return getFolder(version) + getElemPath(type, optional, version);
	}
	
	public static final String getDefaultElementImagePath(int version) {
		return getFolder(version) + getElemPath(version);
	}
	
	protected static final String getTypeStr(NodeType type) {
		return DOT + type.toString().toLowerCase();
	}
	
	protected static final String getVersionStr(int version) {
		if (version > 0) {
			return DOT + version;
		}
		return EMPTY;
	}
	
	protected static final String getOptionalStr(boolean optional) {
		if (optional) {
			return DOT + OPTIONAL_LABEL;
		}
		return EMPTY;
	}
	
	protected static final String getFolder(int version) {
		if (version <= 0) {
			return ELEM_PATH;
		} else {
			return ELEM + version + SEP;
		}
	}
	
	protected static final String getElemPath(int version) {
		return ELEM + getVersionStr(version) + EXTENSION;
	}
	
	protected static final String getElemPath(NodeType type, boolean optional, int version) {
		return ELEM + DOT + type.toString().toLowerCase() + getOptionalStr(optional) + getVersionStr(version) + EXTENSION;
	}
	
	private static class ImageDef {
		
		private NodeType type;
		
		private boolean optional;
		
		private int version;

		public ImageDef(NodeType type, boolean optional, int version) {
			super();
			this.type = type;
			this.optional = optional;
			this.version = version;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (optional ? 1231 : 1237);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + version;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImageDef other = (ImageDef) obj;
			if (optional != other.optional)
				return false;
			if (type != other.type)
				return false;
			if (version != other.version)
				return false;
			return true;
		}
		
	}
	
}
