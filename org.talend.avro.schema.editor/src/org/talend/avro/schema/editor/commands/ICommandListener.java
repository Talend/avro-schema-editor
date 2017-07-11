package org.talend.avro.schema.editor.commands;

/**
 * Listener registered wit he {@ICommandExecutor} service and notified each time a command is executed.
 * 
 * @author timbault
 * @see ICommandExecutor
 *
 */
public interface ICommandListener {

	/**
	 * Method called by the command executor after the call of the run method
	 *  
	 * @param command
	 * @param executor
	 */
	void onRunCommand(IEditCommand command, ICommandExecutor executor);
	
	/**
	 * Method called by the command executor after the call of the undo method
	 * 
	 * @param command
	 * @param executor
	 */
	void onUndoCommand(IEditCommand command, ICommandExecutor executor);
	
	/**
	 * Method called by the command executor after the call of the redo method
	 * 
	 * @param command
	 * @param executor
	 */
	void onRedoCommand(IEditCommand command, ICommandExecutor executor);
	
}
