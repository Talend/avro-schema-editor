package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;

public abstract class SchemaEditorPropertyTester extends PropertyTester {

	protected AvroSchemaEditor getEditor() {
		
		IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

        if (window != null) {
            IWorkbenchPage workbenchPage = window.getActivePage();
            if (workbenchPage != null) {
            	IWorkbenchPart activePart = workbenchPage.getActivePart();                
                if (activePart instanceof IWithAvroSchemaEditor) {
                	return ((IWithAvroSchemaEditor) activePart).getEditor();
                }
            }
        }
        
        return null;
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		AvroSchemaEditor editor = getEditor();
		if (editor != null) {
			return test(receiver, property, args, expectedValue, editor);
		}
		return false;
	}
	
	protected AvroContext.Kind getContextKind(Object[] args) {
		for (Object arg : args) {
			if (arg instanceof String) {
				String stringArg = (String) arg;
				try {
					Kind kind = AvroContext.Kind.valueOf(stringArg.toUpperCase());
					return kind;
				} catch (IllegalArgumentException e) {
					continue;
				}				
			}
		}
		return null;
	}
	
	protected abstract boolean test(Object receiver, String property, Object[] args, Object expectedValue, AvroSchemaEditor editor);
	
}
