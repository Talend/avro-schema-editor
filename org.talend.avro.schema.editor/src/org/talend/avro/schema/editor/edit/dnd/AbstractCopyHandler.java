package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public abstract class AbstractCopyHandler extends AbstractDnDHandler {
	
	private CopyEngine copyEngine;
	
	protected AbstractCopyHandler(AvroContext context, CopyEngine copyEngine) {
		super(context);
		this.copyEngine = copyEngine;
	}

	protected CopyEngine getCopyEngine() {
		return copyEngine;
	}

	protected CopyStrategyProvider getCopyStrategyProvider() {
		return getContext().getService(AvroSchemaController.class).getCopyStrategyProvider();
	}
	
	protected AvroNode copyElement(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		DnDContext dndContext = new DefaultDnDContext(sourceNode, targetNode, position);
		CopyStrategy strategy = getCopyStrategyProvider().getCopyStrategy(getContext(), dndContext);		
		AvroNode nodeCopy = copyEngine.copy(dndContext, strategy);
		return nodeCopy;
	}
	
}
