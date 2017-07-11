package org.talend.avro.schema.editor.edit;

public enum EditorLayout {
	TREE, TREE_AND_ATTRIBUTES, TWO_TREES, TWO_TREES_AND_ATTRIBUTES;
	
	public boolean areAttributesDetached() {
		return this == TREE || this == TWO_TREES;
	}
	
	public EditorLayout getAttachedAttributeLayout() {
		switch (this) {
		case TREE:
			return TREE_AND_ATTRIBUTES;
		case TWO_TREES:
			return TWO_TREES_AND_ATTRIBUTES;
		default:
			return this;
		}
	}
	
	public static String[] getValuesAsString() {
		String[] result = new String[values().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = values()[i].toString().toLowerCase();
		}
		return result;
	}
	
	public static EditorLayout getEditorLayout(int index) {
		return values()[index];
	}
	
	public int getIndex() {		
		EditorLayout[] values = values();
		for (int i = 0; i < values.length; i++) {
			if (values[i] == this) {
				return i;
			}
		}
		return -1;
	}
	
}
