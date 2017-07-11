package org.talend.avro.schema.editor.edit;

public interface IDirtyListener {

	void onDirtyStatusChanged(IDirtyable dirtyable, boolean dirty);
	
	void onDirtyStatusChanged(IDirtyable dirtyable, Object object, boolean dirty);
	
}
