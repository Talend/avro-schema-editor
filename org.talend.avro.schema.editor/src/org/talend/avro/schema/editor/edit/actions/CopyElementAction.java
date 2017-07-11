package org.talend.avro.schema.editor.edit.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * 
 * @author timbault
 *
 */
public class CopyElementAction extends ContextualActionImpl {

	public static final String CMD_ID = "org.talend.avro.schema.editor.edit.copy"; //$NON-NLS-1$
	
	public CopyElementAction(String text, int style, int notifications) {
		super(text, style, notifications);
	}

	public CopyElementAction(String text, int notifications) {
		super(text, notifications);
	}	

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.COPY);
	}

	@Override
	public String getToolTipText() {
		return "Copy selected element(s)";
	}
	
	@Override
	protected boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes) {		
		return !contextualNodes.isEmpty();
	}

	@Override
	public void run() {
		getContext().getCopyContext().setNodesToCopy(getContextualNodes(), false);		
	}	
	
}
