package org.talend.avro.schema.editor.edit.dnd;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class MoveFieldToRecordChecker implements DragAndDropPolicy.Checker {

	@Override
	public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		
		String sourceName = AttributeUtil.getNameFromAttribute(sourceNode);
		
		List<AvroNode> fields = targetNode.getChildren(NodeType.FIELD);
		for (AvroNode field : fields) {
			String fieldName = AttributeUtil.getNameFromAttribute(field);
			
			if (fieldName.trim().equals(sourceName.trim())) {
				return false;
			}
		}
		
		return true;
	}

}
