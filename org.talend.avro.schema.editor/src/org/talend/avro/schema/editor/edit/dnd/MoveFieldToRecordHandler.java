package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelConstants;
import org.talend.avro.schema.editor.model.TargetPosition;

public class MoveFieldToRecordHandler extends AbstractDnDHandler {

	public MoveFieldToRecordHandler(AvroContext context) {
		super(context);
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {
		
		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		
		AvroNode sourceParent = sourceNode.getParent();
		params.storeAvroNode(DnDParams.SOURCE_PARENT, sourceParent);
		
		storeOriginalPosition(sourceNode, params);
		
		unlinkNodes(sourceParent, sourceNode, ModelConstants.UNREGISTER);
		
		linkNodes(targetNode, sourceNode, ModelConstants.REGISTER);
		
		return params;
	}

	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {	
		
		AvroNode sourceNode = params.getSourceNode();
		AvroNode currentParent = sourceNode.getParent();
		AvroNode sourceParent = params.getAvroNode(DnDParams.SOURCE_PARENT);
		
		AvroNode sourceRefNode = null;
		TargetPosition targetPosition = TargetPosition.UPON;
		
		if (params.isAvroNodeDefined(DnDParams.SOURCE_REF_NODE)) {
			sourceRefNode = params.getAvroNode(DnDParams.SOURCE_REF_NODE);
			TargetPosition sourceRefNodePos = (TargetPosition) params.getObject(DnDParams.SOURCE_REF_NODE_POSITION);
			targetPosition = sourceRefNodePos == TargetPosition.AFTER ? TargetPosition.BEFORE : TargetPosition.AFTER;
		}		
		
		unlinkNodes(currentParent, sourceNode, ModelConstants.UNREGISTER);
		
		if (sourceRefNode == null) {
			linkNodes(sourceParent, sourceNode, ModelConstants.REGISTER);
		} else {
			linkNodes(sourceParent, sourceNode, sourceRefNode, targetPosition, ModelConstants.REGISTER);
		}
		
		return true;
	}	
	
}
