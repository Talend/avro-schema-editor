package org.talend.avro.schema.editor.commands;

/**
 * This interface represents an undoable command, i.e. an action we can run, undo and redo.
 * Methods canUndo and canRedo are automatically called before methods undo and redo.
 * The methods run, undo and redo are not intended to be called directly. It is called via the {@link CommandExecutor}.
 * 
 * @author timbault
 *
 */
public interface IEditCommand {
	
	/**
     * Runs the command.
     * 
     */
    void run();

    /**
     * @return
     */
    boolean canUndo();

    /**
     * Performs the undo.
     * 
     */
    void undo();
    
    /**
     * @return
     */
    boolean canRedo();

    /**
     * Performs the redo.
     * 
     */
    void redo();

    /**
     * @return Returns the label of the command launch action.
     */
    String getLabel();

    /**
     * Dispose any internal resources
     */
    void dispose();
	
}
