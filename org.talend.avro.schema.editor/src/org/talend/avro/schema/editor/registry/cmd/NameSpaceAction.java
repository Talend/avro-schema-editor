package org.talend.avro.schema.editor.registry.cmd;

import org.eclipse.jface.action.IAction;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.registry.NSNode;

public interface NameSpaceAction extends IAction {

	void setContext(AvroContext context);
	
	void setNSNode(NSNode nsNode);	
	
}
