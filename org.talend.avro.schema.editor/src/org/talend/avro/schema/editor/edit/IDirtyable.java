package org.talend.avro.schema.editor.edit;

public interface IDirtyable {

	void setDirty(boolean dirty);

	void setDirty(Object object, boolean dirty);
	
	void addDirtyListener(IDirtyListener listener);
	
	void removeDirtyListener(IDirtyListener listener);
	
}
