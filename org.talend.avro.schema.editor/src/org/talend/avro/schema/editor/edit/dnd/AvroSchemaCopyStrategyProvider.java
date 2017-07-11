package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.CopyStrategy.Kind;
import org.talend.avro.schema.editor.model.NodeType;

public class AvroSchemaCopyStrategyProvider implements CopyStrategyProvider {
		
	@Override
	public CopyStrategy getCopyStrategy(AvroContext context, DnDContext dndContext) {
		AvroSchemaCopyStrategy copyStrategy = new AvroSchemaCopyStrategy(context, true, Kind.COPY);
		copyStrategy.registerKind(NodeType.RECORD, Kind.REF);
		copyStrategy.registerKind(NodeType.ENUM, Kind.REF);
		copyStrategy.registerKind(NodeType.FIXED, Kind.REF);
		return copyStrategy;
	}

}
