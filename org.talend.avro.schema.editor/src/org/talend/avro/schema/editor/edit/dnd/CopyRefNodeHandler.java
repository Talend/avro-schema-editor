package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class CopyRefNodeHandler extends AbstractCopyHandler {

	public CopyRefNodeHandler(AvroContext context, CopyEngine copyEngine) {
		super(context, copyEngine);
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {

		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);

		AvroNode nodeToCopy = ((RefNode) sourceNode).getReferencedNode();
		
		AvroNode copy = copyElement(nodeToCopy, targetNode, position);
		
		getController().addElement(targetNode, copy, position);

		params.storeAvroNode(DnDParams.COPY_NODE, copy);		
		
		return params;
	}

	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {
		AvroNode copy = params.getAvroNode(DnDParams.COPY_NODE);
		getController().removeElement(copy);
		return true;
	}

}
