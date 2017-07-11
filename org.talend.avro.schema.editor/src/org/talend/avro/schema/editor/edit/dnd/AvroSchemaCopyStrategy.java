package org.talend.avro.schema.editor.edit.dnd;

import java.util.HashMap;
import java.util.Map;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NameService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

public class AvroSchemaCopyStrategy implements CopyStrategy {

	private AvroContext context;
	
	private boolean deepCopy;
	
	private Kind defaultKind;
	
	private Map<NodeType, Kind> kinds = new HashMap<>();
	
	public AvroSchemaCopyStrategy(AvroContext context, boolean deepCopy, Kind defaultKind) {
		super();
		this.context = context;
		this.deepCopy = deepCopy;
		this.defaultKind = defaultKind;		
	}

	@Override
	public boolean deepCopy(DnDContext dndContext) {
		return deepCopy;
	}
		
	public void registerKind(NodeType type, Kind kind) {
		kinds.put(type, kind);
	}
	
	@Override
	public Kind getKindOfCopy(DnDContext dndContext, AvroNode nodeToCopy) {
		if (nodeToCopy == dndContext.getSourceNode()) {
			return Kind.COPY;
		}
		NodeType type = nodeToCopy.getType();
		Kind kind = kinds.get(type);
		if (kind != null) {
			return kind;
		}
		return defaultKind;
	}

	@Override
	public boolean copyAttributes(DnDContext dndContext, AvroNode nodeToCopy, AvroNode nodeCopy) {
		if (nodeCopy.getType() == NodeType.REF) {
			// do not copy attributes for a ref node! Ref node has no attributes!
			return false;
		}
		return true;
	}

	@Override
	public AttributeInitializer getAttributeInitializer(DnDContext dndContext, AvroNode nodeToCopy, AvroNode nodeCopy) {
		return new CopyAttributeInitializer(nodeToCopy, nodeCopy, context, dndContext);
	}

	private static class CopyAttributeInitializer implements AttributeInitializer {

		private AvroContext context;
		
		private AvroNode nodeToCopy;
		
		private AvroNode nodeCopy;
		
		private AvroNode targetNode;
		
		public CopyAttributeInitializer(AvroNode nodeToCopy, AvroNode nodeCopy, AvroContext context, DnDContext dndContext) {
			super();
			this.nodeCopy = nodeCopy;
			this.nodeToCopy = nodeToCopy;
			this.context = context;
			this.targetNode = dndContext.getTargetNode();
		}

		@Override
		public boolean provideInitialAttributeValue(NodeType type, String attributeName) {
			switch (attributeName) {
			case AvroAttributes.PATH:
				return false;
			case AvroAttributes.NAME:
				return type.isNamed();
			}
			return true;
		}

		@Override
		public Object getInitialAttributeValue(NodeType type, String attributeName) {		
			AvroAttribute<?> attribute = nodeToCopy.getAttributes().getAttribute(attributeName);
			// be careful for name attribute
			if (AvroAttributes.NAME.equals(attributeName)) {
				return context.getService(NameService.class).getValidNameCopy(nodeToCopy, nodeCopy, targetNode);				
			}
			return attribute.getCopyOfValue();			
		}

		@Override
		public boolean isVisible(NodeType type, String attributeName) {			
			return nodeToCopy.getAttributes().getAttribute(attributeName).isVisible();
		}

		@Override
		public boolean isEnabled(NodeType type, String attributeName) {
			return nodeToCopy.getAttributes().getAttribute(attributeName).isEnabled();
		}
		
	}
	
}
