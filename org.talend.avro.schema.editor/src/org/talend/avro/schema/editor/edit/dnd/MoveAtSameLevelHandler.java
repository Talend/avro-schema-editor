package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

/**
 * This handler allows to change the position of a node under its parent. 
 * 
 * @author timbault
 *
 */
public class MoveAtSameLevelHandler extends AbstractDnDHandler {
	
	public MoveAtSameLevelHandler() {
		super(null);
	}
	
	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position, AvroNodeAttributesValidators validators) {
		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		AvroNode parentNode = sourceNode.getParent();
		params.storeAvroNode(DnDParams.SOURCE_PARENT, parentNode);
		storeOriginalPosition(sourceNode, params);
		parentNode.moveChild(sourceNode, targetNode, position);
		// validate
		validate(parentNode, validators);		
		return params;
	}
	
	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {
		AvroNode sourceNode = params.getSourceNode();
		AvroNode sourceParent = params.getAvroNode(DnDParams.SOURCE_PARENT);
		AvroNode sourceRefNode = params.getAvroNode(DnDParams.SOURCE_REF_NODE);
		TargetPosition sourceRefNodePos = (TargetPosition) params.getObject(DnDParams.SOURCE_REF_NODE_POSITION);
		TargetPosition targetPosition = sourceRefNodePos == TargetPosition.AFTER ? TargetPosition.BEFORE : TargetPosition.AFTER;
		sourceParent.moveChild(sourceNode, sourceRefNode, targetPosition);
		// validate
		validate(sourceParent, validators);		
		return true;
	}
	
}
