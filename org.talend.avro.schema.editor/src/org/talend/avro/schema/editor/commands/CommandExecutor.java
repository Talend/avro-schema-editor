package org.talend.avro.schema.editor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.ListenerList;
import org.talend.avro.schema.editor.context.AbstractContextualService;

/**
 * Default implementation of a {@link ICommandExecutor} service for the standard avro schema editor.
 * 
 * @author timbault
 *
 */
public class CommandExecutor extends AbstractContextualService implements ICommandExecutor {

	private ListenerList listeners = new ListenerList();
	
    private Stack<IEditCommand> undoableCommandStack;

    private Stack<IEditCommand> redoableCommandStack;

    public CommandExecutor() {
        undoableCommandStack = new Stack<IEditCommand>();
        redoableCommandStack = new Stack<IEditCommand>();
    }
    
    @Override
	public void addCommandListener(ICommandListener listener) {
    	listeners.add(listener);
    }

	@Override
	public void removeCommandListener(ICommandListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyListenersOnRun(IEditCommand cmd) {
		for (Object listener : listeners.getListeners()) {
			((ICommandListener) listener).onRunCommand(cmd, this);
		}
	}

	protected void notifyListenersOnUndo(IEditCommand cmd) {
		for (Object listener : listeners.getListeners()) {
			((ICommandListener) listener).onUndoCommand(cmd, this);
		}
	}
	
	protected void notifyListenersOnRedo(IEditCommand cmd) {
		for (Object listener : listeners.getListeners()) {
			((ICommandListener) listener).onRedoCommand(cmd, this);
		}
	}
	
	protected void register(IEditCommand command) {
        if (!redoableCommandStack.empty()) {
            cleanRedoableCommandStack();
        }
        if (command.canUndo()) {
            undoableCommandStack.push(command);
        }
        else {
            cleanUndoableCommandStack();
        }
    }
    
    @Override
	public List<IEditCommand> getUndoableCommandStack() {
    	List<IEditCommand> commands = new ArrayList<>();
    	for (int i = 0; i < undoableCommandStack.size(); i++) {
    		commands.add(undoableCommandStack.get(i));
    	}
		return commands;
	}

	@Override
	public List<IEditCommand> getRedoableCommandStack() {
		List<IEditCommand> commands = new ArrayList<>();
    	for (int i = 0; i < redoableCommandStack.size(); i++) {
    		commands.add(redoableCommandStack.get(i));
    	}
		return commands;
	}

	private void cleanUndoableCommandStack() {
        for (IEditCommand cmd : undoableCommandStack) {
            cmd.dispose();
        }
        undoableCommandStack.clear();
    }

    private void cleanRedoableCommandStack() {
        for (IEditCommand cmd : redoableCommandStack) {
            cmd.dispose();
        }
        redoableCommandStack.clear();
    }

    protected boolean canUndo() {
        if (!undoableCommandStack.empty()) {
            IEditCommand lastElement = undoableCommandStack.lastElement();
            if (lastElement != null) {
                return lastElement.canUndo();
            }
        }
        return false;
    }

    public void undo() {
        if (undoableCommandStack.empty()) {
            return;
        }
        IEditCommand command = undoableCommandStack.pop();
        if (command.canUndo()) {
        	command.undo();
        	redoableCommandStack.push(command);
        	notifyListenersOnUndo(command);
        }
        else {
            command.dispose();
            cleanUndoableCommandStack();
        }
    }

    protected boolean canRedo() {
        if (!redoableCommandStack.empty()) {
            IEditCommand lastElement = redoableCommandStack.lastElement();
            if (lastElement != null) {
                return lastElement.canRedo();
            }
        }
        return false;
    }

    public void redo() {
        if (redoableCommandStack.empty()) {
            return;
        }
        IEditCommand cmd = redoableCommandStack.pop();
        if (cmd.canRedo()) {
        	cmd.redo();
        	undoableCommandStack.push(cmd);
        	notifyListenersOnRedo(cmd);
        }
        else {
            cmd.dispose();
            cleanRedoableCommandStack();
        }
    }

    public boolean isUndoableCommandStackEmpty() {
        return undoableCommandStack.isEmpty();
    }

    public boolean isRedoableCommandStackEmpty() {
        return redoableCommandStack.isEmpty();
    }

    public void clearUndoAndRedoStacks() {
        cleanUndoableCommandStack();
        cleanRedoableCommandStack();
    }

    /**
     * Executes the specified command. Registers this command in the global stack.
     *
     * @param command the command to run
     */
    @Override
    public void execute(IEditCommand command) {
    	command.run();
    	register(command);
    	notifyListenersOnRun(command);
    }
   
	@Override
	public void dispose() {
		clearUndoAndRedoStacks();
		listeners.clear();
	}
   
}
