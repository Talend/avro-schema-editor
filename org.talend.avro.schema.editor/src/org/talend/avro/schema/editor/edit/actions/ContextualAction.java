package org.talend.avro.schema.editor.edit.actions;

import org.eclipse.jface.action.IAction;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContextListener;

/**
 * Action which encapsulates an avro context.
 * <p>
 * This action can be a listener of the encapsulated context in order to update its state according to the context changes.
 * 
 * @author timbault
 *
 */
public interface ContextualAction extends IAction, AvroContextListener {
	
	/**
	 * Initialize the action with the specified context.
	 * 
	 * @param context
	 * @param link if true, the action will be linked to the context.
	 */
	void init(AvroContext context, boolean link);
	
}
