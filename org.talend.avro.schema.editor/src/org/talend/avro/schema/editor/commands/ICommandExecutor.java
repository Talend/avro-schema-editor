package org.talend.avro.schema.editor.commands;

import java.util.List;

import org.talend.avro.schema.editor.context.services.IContextualService;

/**
 * Service responsible for executing the commands editing an avro schema.
 * 
 * @author timbault
 *
 */
public interface ICommandExecutor extends IContextualService {

	/**
	 * Register a command listener.
	 * 
	 * @param listener
	 */
	void addCommandListener(ICommandListener listener);
	
	/**
	 * Remove a command listener.
	 * 
	 * @param listener
	 */
	void removeCommandListener(ICommandListener listener);
	
	/**
     * Calls command undo.
     */
    void undo();

    /**
     * Calls command redo.
     */
    void redo();
	
    /**
     * @return true if the undo stack is empty.
     */
    boolean isUndoableCommandStackEmpty();

    List<IEditCommand> getUndoableCommandStack();
    
    /**
     * @return true if the redo stack is empty
     */
    boolean isRedoableCommandStackEmpty();

    List<IEditCommand> getRedoableCommandStack();
    
    /**
     * Clears undo and redo stacks.
     */
    void clearUndoAndRedoStacks();

    /**
     * Executes the specified command. Registers this command in the global stack.
     * 
     * @param command The specified command.
     */
    void execute(IEditCommand command);
    
}
