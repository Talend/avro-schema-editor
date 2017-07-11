package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.utils.Params;

/**
 * Represents some drag and drop parameters.
 * 
 * @author timbault
 *
 */
public interface DnDParams extends Params, DnDContext {

	static final String COPY_NODE = "CopyNode"; //$NON-NLS-1$
	
	static final String REF_NODE = "RefNode"; //$NON-NLS-1$
	
	static final String POSITION = "Position"; //$NON-NLS-1$
	
	static final String SOURCE_PARENT = "SourceParent"; //$NON-NLS-1$
	
	static final String SOURCE_INDEX = "SourceIndex"; //$NON-NLS-1$
		
	static final String SOURCE_REF_NODE = "SourceRefNode"; //$NON-NLS-1$
	
	static final String SOURCE_REF_NODE_POSITION = "SourceRefNodePosition"; //$NON-NLS-1$
	
}
