package org.talend.avro.schema.editor.edit.dnd;

/**
 * This is responsible for the DnD configuration.
 * 
 * @author timbault
 *
 */
public interface DragAndDropPolicyConfiguration {

	/**
	 * Performs the drag and drop configuration
	 * 
	 * @param dndPolicy
	 */
	void configureDragAndDropPolicy(DragAndDropPolicy dndPolicy);
	
}
