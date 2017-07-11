package org.talend.avro.schema.editor.handlers;

import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.handlers.SchemaEditorPropertyTester;

public class UndoRedoPropertyTester extends SchemaEditorPropertyTester {

	public static final String NAME_SPACE = "org.talend.avro.schema.editor"; //$NON-NLS-1$
	
	public static final String ID = NAME_SPACE + ".UndoRedoPropertyTester"; //$NON-NLS-1$
	
	public static final String UNDOABLE = "undoable"; //$NON-NLS-1$
	
	public static final String REDOABLE = "redoable"; //$NON-NLS-1$
	
	public static final String[] PROPERTIES = new String[] {
			NAME_SPACE + "." + UNDOABLE,
			NAME_SPACE + "." + REDOABLE
	};
		
	@Override
	protected boolean test(Object receiver, String property, Object[] args, Object expectedValue,
			AvroSchemaEditor editor) {
		if (UNDOABLE.equals(property) || REDOABLE.equals(property)) {			
			ICommandExecutor commandExecutor = editor.getServiceProvider().getService(ICommandExecutor.class);
			if (UNDOABLE.equals(property)) {
				return !commandExecutor.isUndoableCommandStackEmpty();
			} else {
				return !commandExecutor.isRedoableCommandStackEmpty();
			}
		}
		return false;
	}

}
