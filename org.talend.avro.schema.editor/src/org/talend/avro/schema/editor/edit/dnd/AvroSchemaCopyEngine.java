package org.talend.avro.schema.editor.edit.dnd;

import java.util.HashMap;
import java.util.Map;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.CopyStrategy.Kind;
import org.talend.avro.schema.editor.model.ArrayNode;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.EnumNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.FixedNode;
import org.talend.avro.schema.editor.model.IAvroNodeVisitor;
import org.talend.avro.schema.editor.model.MapNode;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.RefNodeImpl;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.UnionNode;

public class AvroSchemaCopyEngine implements CopyEngine {

	private AvroContext context;
	
	public AvroSchemaCopyEngine(AvroContext context) {
		super();
		this.context = context;
	}

	@Override
	public AvroNode copy(DnDContext dndContext, CopyStrategy strategy) {
		AvroNode node = dndContext.getSourceNode();
		AvroNode copyNode = null;
		if (strategy.deepCopy(dndContext)) {
			// copy all hierarchy
			CopyVisitor copyVisitor = new CopyVisitor(strategy, dndContext, context);
			node.visitNode(copyVisitor);
			copyNode = copyVisitor.getCopy(node);
		} else {
			// copy only the given node
			// TODO
		}
		return copyNode;
	}	
	
	private class CopyVisitor implements IAvroNodeVisitor {

		private AvroContext context;
		
		private CopyStrategy strategy;
								
		private Map<AvroNode, AvroNode> origin2copy = new HashMap<>();
				
		private DnDContext dndContext;
		
		public CopyVisitor(CopyStrategy strategy, DnDContext dndContext, AvroContext context) {
			super();
			this.strategy = strategy;
			this.dndContext = dndContext;
			this.context = context;
		}

		public AvroNode getCopy(AvroNode node) {
			return origin2copy.get(node);
		}
		
		protected void link(AvroNode nodeCopy, AvroNode parent) {
			AvroNode parentCopy = origin2copy.get(parent);
			if (parentCopy != null) {
				parentCopy.addChild(nodeCopy);
				nodeCopy.setParent(parentCopy);
			}
		}
		
		protected void copyNode(AvroNode node, AvroNode nodeCopy) {
			AvroNode parent = node.getParent();			
			link(nodeCopy, parent);
			if (strategy.copyAttributes(dndContext, node, nodeCopy)) {
				nodeCopy.init(strategy.getAttributeInitializer(dndContext, node, nodeCopy));
			}						
			origin2copy.put(node, nodeCopy);
		}
		
		@Override
		public AvroNode enterRootNode(RootNode rootNode) {
			// nothing to do, the root cannot be copied
			return rootNode;
		}

		@Override
		public boolean exitRootNode(AvroNode rootNode) {
			return false;
		}

		@Override
		public AvroNode enterRecordNode(RecordNode recordNode) {
			Kind kind = strategy.getKindOfCopy(dndContext, recordNode);
			AvroNode nodeCopy = null;
			switch (kind) {
			case COPY:
				nodeCopy = new RecordNode(context);
				break;
			case REF:
				nodeCopy = new RefNodeImpl(recordNode, context);
				break;			
			}
			copyNode(recordNode, nodeCopy);			
			if (kind == Kind.COPY) {
				return recordNode;
			} else {
				return null;
			}
		}

		@Override
		public boolean exitRecordNode(AvroNode recordNode) {					
			return true;
		}

		@Override
		public AvroNode enterFieldNode(FieldNode fieldNode) {
			copyNode(fieldNode, new FieldNode(context));
			return fieldNode;
		}

		@Override
		public boolean exitFieldNode(AvroNode fieldNode) {
			return true;
		}

		@Override
		public AvroNode enterUnionNode(UnionNode unionNode) {
			copyNode(unionNode, new UnionNode(context));
			return unionNode;
		}

		@Override
		public boolean exitUnionNode(AvroNode unionNode) {
			return true;
		}

		@Override
		public AvroNode enterEnumNode(EnumNode enumNode) {
			Kind kind = strategy.getKindOfCopy(dndContext, enumNode);
			AvroNode nodeCopy = null;
			switch (kind) {
			case COPY:
				nodeCopy = new EnumNode(context);
				break;
			case REF:
				nodeCopy = new RefNodeImpl(enumNode, context);
				break;			
			}
			copyNode(enumNode, nodeCopy);			
			return null;
		}

		@Override
		public boolean exitEnumNode(AvroNode enumNode) {
			return true;
		}

		@Override
		public AvroNode enterFixedNode(FixedNode fixedNode) {
			Kind kind = strategy.getKindOfCopy(dndContext, fixedNode);
			AvroNode nodeCopy = null;
			switch (kind) {
			case COPY:
				nodeCopy = new FixedNode(context);
				break;
			case REF:
				nodeCopy = new RefNodeImpl(fixedNode, context);
				break;			
			}
			copyNode(fixedNode, nodeCopy);			
			return null;
		}

		@Override
		public boolean exitFixedNode(AvroNode fixedNode) {
			return true;
		}
		
		@Override
		public AvroNode enterPrimitiveTypeNode(PrimitiveTypeNode typeNode) {
			copyNode(typeNode, new PrimitiveTypeNode(context));
			return null;
		}

		@Override
		public boolean exitPrimitiveTypeNode(AvroNode typeNode) {
			return true;
		}

		@Override
		public AvroNode enterArrayNode(ArrayNode arrayNode) {
			copyNode(arrayNode, new ArrayNode(context));
			return arrayNode;
		}

		@Override
		public boolean exitArrayNode(AvroNode arrayNode) {
			return true;
		}

		@Override
		public AvroNode enterMapNode(MapNode mapNode) {
			copyNode(mapNode, new MapNode(context));
			return mapNode;
		}

		@Override
		public boolean exitMapNode(AvroNode mapNode) {
			return true;
		}

		@Override
		public AvroNode enterRefNode(RefNode refNode) {
			RefNode refNodeCopy = new RefNodeImpl(refNode.getReferencedNode(), context);
			copyNode(refNode, refNodeCopy);
			return refNode;
		}

		@Override
		public boolean exitRefNode(AvroNode refNode) {
			return true;
		}		
		
	}
	
}


