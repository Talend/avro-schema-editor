package org.talend.avro.schema.editor.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Command which contains another commands.
 * 
 * @author timbault
 *
 */
public class CompositeCommand implements IEditCommand {

	/**
	 * List of the child commands.
	 */
	private List<IEditCommand> commands = new ArrayList<>();

	private String label;
	
	public CompositeCommand() {
		this(null);
	}
	
    public CompositeCommand(String label) {
        super();
        this.label = label;
    }
    
    public void setLabel(String label) {
		this.label = label;
	}

	/**
     * Adds a new child command to the composite command.
     */
    public void addCommand(IEditCommand command) {
        commands.add(command);
    }
    
    /* 
     * Execute the child commands in the natural order (i.e. first added command will be executed first)
     * 
     * @see org.talend.avro.schema.editor.commands.IUndoableCommand#run()
     */
    public void run() {
        for (IEditCommand command : commands) {
            runCommand(command);
        }
    }

    protected void runCommand(IEditCommand command) {
        command.run();
    }

    /*
     * If at least one command is not undoable then the main composite command is not
     * 
     * @see org.talend.avro.schema.editor.commands.IUndoableCommand#canUndo()
     */
    public boolean canUndo() {
        for (IEditCommand command : commands) {
            if (!command.canUndo()) {
                return false;
            }
        }
        return true;
    }

    /* 
     * If at least one child command is not redoable, then the main composite command is not
     * 
     * @see org.talend.avro.schema.editor.commands.IUndoableCommand#canRedo()
     */
    @Override
    public boolean canRedo() {
    	for (IEditCommand command : commands) {
            if (!command.canRedo()) {
                return false;
            }
        }
        return true;
    }

    /* 
     * Call the undo method of the child commands in the reverse order (i.e. last added child command will be undo first)
     * 
     * @see org.talend.avro.schema.editor.commands.IUndoableCommand#undo()
     */
    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            IEditCommand cmd = commands.get(i);
            cmd.undo();
        }
    }

    /* 
     * Call the redo method of the child commands in the natural order (i.e. first added child command will be executed first)
     * 
     * @see org.talend.avro.schema.editor.commands.IUndoableCommand#redo()
     */
    @Override
    public void redo() {
    	for (IEditCommand command : commands) {
            command.redo();
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @return true if the composite command is empty
     */
    public boolean isEmpty() {
        return commands.size() == 0;
    }

    @Override
    public void dispose() {
        for (IEditCommand command : commands) {
            command.dispose();
        }
    }
	
}
