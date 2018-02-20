package org.talend.avro.schema.editor.io;

import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.io.context.ArrayContext;
import org.talend.avro.schema.editor.io.context.ArrayFinishContext;
import org.talend.avro.schema.editor.io.context.ArrayStartContext;
import org.talend.avro.schema.editor.io.context.EnumContext;
import org.talend.avro.schema.editor.io.context.EnumFinishContext;
import org.talend.avro.schema.editor.io.context.EnumStartContext;
import org.talend.avro.schema.editor.io.context.FieldContext;
import org.talend.avro.schema.editor.io.context.FieldFinishContext;
import org.talend.avro.schema.editor.io.context.FieldStartContext;
import org.talend.avro.schema.editor.io.context.FixedContext;
import org.talend.avro.schema.editor.io.context.FixedFinishContext;
import org.talend.avro.schema.editor.io.context.FixedStartContext;
import org.talend.avro.schema.editor.io.context.MapContext;
import org.talend.avro.schema.editor.io.context.MapFinishContext;
import org.talend.avro.schema.editor.io.context.MapStartContext;
import org.talend.avro.schema.editor.io.context.PrimitiveTypeFinishContext;
import org.talend.avro.schema.editor.io.context.PrimitiveTypeStartContext;
import org.talend.avro.schema.editor.io.context.RecordContext;
import org.talend.avro.schema.editor.io.context.RecordFinishContext;
import org.talend.avro.schema.editor.io.context.RecordStartContext;
import org.talend.avro.schema.editor.io.context.RefFinishContext;
import org.talend.avro.schema.editor.io.context.RefStartContext;
import org.talend.avro.schema.editor.io.context.RootFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.UnionFinishContext;
import org.talend.avro.schema.editor.io.context.UnionStartContext;
import org.talend.avro.schema.editor.io.context.impl.RootContextImpl;
import org.talend.avro.schema.editor.io.def.DefaultValueUtil;
import org.talend.avro.schema.editor.model.ArrayNode;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.EnumNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.FixedNode;
import org.talend.avro.schema.editor.model.IAvroNodeVisitor;
import org.talend.avro.schema.editor.model.MapNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.RefNodeImpl;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This schema visitor visits an avro schema model (built with {@link AvroNode}) and generates a corresponding {@link Schema} object.
 * 
 * @author timbault
 *
 */
public class GenerateSchemaVisitor implements IAvroNodeVisitor {

	/**
	 * Avro schema editor context.
	 */
	private AvroContext avroContext;
	
	/**
	 * The generated schema.
	 */
	private Schema schema;
	
	/**
	 * The current schema context.
	 */
	private SchemaContext context;
	
	private static final AttributeInitializer dummyAttributeInitializer = new DummyAttributeInitializer();
	
	private Set<AvroNode> visitedReferencedNodes = new HashSet<>();
	
	public GenerateSchemaVisitor(AvroContext avroContext) {
		super();
		this.avroContext = avroContext;
	}

	/**
	 * Return the generated schema
	 * 
	 * @return
	 */
	public Schema getSchema() {
		return schema;
	}

	@Override
	public AvroNode enterRootNode(RootNode rootNode) {
		context = new RootContextImpl();
		return rootNode;
	}

	@Override
	public boolean exitRootNode(AvroNode rootNode) {
		RootFinishContext rootFinishContext = (RootFinishContext) context;
		schema = rootFinishContext.endRoot();
		return true;
	}

	@Override
	public AvroNode enterRecordNode(RecordNode recordNode) {
		if (visitedReferencedNodes.contains(recordNode)) {
			
			// already visited
			// reference it
			startReference(recordNode);
			// and returns a dummy ref node
			return new RefNodeImpl(recordNode, avroContext);
			
		} else {
			
			RecordStartContext recordStartContext = (RecordStartContext) context;
			RecordContext recordContext = recordStartContext.record(AttributeUtil.getNameFromAttribute(recordNode));
			
			// name space
			recordContext = recordContext.namespace(AttributeUtil.getNameSpaceFromAttribute(recordNode));
			
			// doc
			if (AttributeUtil.hasDoc(recordNode)) {
				recordContext = recordContext.doc(AttributeUtil.getDoc(recordNode));
			}
			
			// aliases
			if (AttributeUtil.hasAliases(recordNode)) {
				recordContext = recordContext.aliases(AttributeUtil.getAliases(recordNode));
			}
			
			// fields
			context = recordContext.fields();
			
			// register record node
			visitedReferencedNodes.add(recordNode);
			
			return recordNode;
		}		
	}
	
	@Override
	public boolean exitRecordNode(AvroNode recordNode) {	
		if (recordNode.getType().isRef()) {
			endReference();
		} else {
			RecordFinishContext recordFinishContext = (RecordFinishContext) context;
			context = recordFinishContext.endRecord();
		}
		return true;
	}

	@Override
	public AvroNode enterFieldNode(FieldNode fieldNode) {
		
		FieldStartContext fieldStartContext = (FieldStartContext) context;
		
		// name
		FieldContext fieldContext = fieldStartContext.name(AttributeUtil.getNameFromAttribute(fieldNode));
		
		// doc
		if (AttributeUtil.hasDoc(fieldNode)) {
			fieldContext = fieldContext.doc(AttributeUtil.getDoc(fieldNode));
		}
		
		// aliases
		if (AttributeUtil.hasAliases(fieldNode)) {
			fieldContext = fieldContext.aliases(AttributeUtil.getAliases(fieldNode));
		}
		
		// handle custom properties
		String[] customAttributeNames = AttributeUtil.getCustomAttributeNames(fieldNode, avroContext);
		for (String key : customAttributeNames) {
			String value = AttributeUtil.getCustomAttributeValueAsString(fieldNode, key, avroContext);
			fieldContext = fieldContext.custom(key, value);
		}
		
		// field type
		context = fieldContext.type();		
		
		// if this field has not child, simulate a primitive type child
		if (!fieldNode.hasChildren()) {
			visitDummyPrimitiveTypeNode(fieldNode);
		}
		
		return fieldNode;
	}
	
	@Override
	public boolean exitFieldNode(AvroNode fieldNode) {
		FieldFinishContext fieldFinishContext = (FieldFinishContext) context;
		if (hasDefaultValue(fieldNode)) {
			context = fieldFinishContext.setDefaultValue(getDefaultValue(fieldNode));
		} else {
			context = fieldFinishContext.noDefault();
		}
		return true;
	}

	@Override
	public AvroNode enterUnionNode(UnionNode unionNode) {
		UnionStartContext unionStartContext = (UnionStartContext) context;		
		context = unionStartContext.union();
		return unionNode;
	}

	@Override
	public boolean exitUnionNode(AvroNode unionNode) {		
		UnionFinishContext unionFinishContext = (UnionFinishContext) context;			
		context = unionFinishContext.endUnion();		
		return true;
	}

	@Override
	public AvroNode enterEnumNode(EnumNode enumNode) {
		
		if (visitedReferencedNodes.contains(enumNode)) {
			
			// already visited
			// reference it
			startReference(enumNode);
			
			// and returns a dummy ref node
			return new RefNodeImpl(enumNode, avroContext);
			
		} else {
		
			EnumStartContext enumStartContext = (EnumStartContext) context;
			EnumContext enumContext = enumStartContext.enumeration(AttributeUtil.getNameFromAttribute(enumNode));

			// name space
			enumContext = enumContext.namespace(AttributeUtil.getNameSpaceFromAttribute(enumNode));

			// doc
			if (AttributeUtil.hasDoc(enumNode)) {
				enumContext = enumContext.doc(AttributeUtil.getDoc(enumNode));
			}

			// aliases
			if (AttributeUtil.hasAliases(enumNode)) {
				enumContext = enumContext.aliases(AttributeUtil.getAliases(enumNode));
			}

			// symbols
			context = enumContext.symbols(AttributeUtil.getSymbols(enumNode));

			// register enum node
			visitedReferencedNodes.add(enumNode);
			
			return enumNode;
		}
	}

	@Override
	public boolean exitEnumNode(AvroNode enumNode) {	
		if (enumNode.getType().isRef()) {
			endReference();
		} else {
			EnumFinishContext enumFinishContext = (EnumFinishContext) context;
			context = enumFinishContext.endEnum();
		}
		return true;
	}

	@Override
	public AvroNode enterFixedNode(FixedNode fixedNode) {
		
		if (visitedReferencedNodes.contains(fixedNode)) {
			
			// already visited
			// reference it
			startReference(fixedNode);
			
			// and returns a dummy ref node
			return new RefNodeImpl(fixedNode, avroContext);
			
		} else {
		
			FixedStartContext fixedStartContext = (FixedStartContext) context;
			FixedContext fixedContext = fixedStartContext.fixed(AttributeUtil.getNameFromAttribute(fixedNode));

			// name space
			fixedContext = fixedContext.namespace(AttributeUtil.getNameSpaceFromAttribute(fixedNode));

			// aliases
			if (AttributeUtil.hasAliases(fixedNode)) {
				fixedContext.aliases(AttributeUtil.getAliases(fixedNode));
			}

			// size
			context = fixedContext.size(AttributeUtil.getSize(fixedNode));

			// register fixed node
			visitedReferencedNodes.add(fixedNode);
			
			return fixedNode;
		}
	}

	@Override
	public boolean exitFixedNode(AvroNode fixedNode) {
		if (fixedNode.getType().isRef()) {
			endReference();
		} else {
			FixedFinishContext fixedFinishContext = (FixedFinishContext) context;
			context = fixedFinishContext.endFixed();
		}
		return true;
	}

	@Override
	public AvroNode enterPrimitiveTypeNode(PrimitiveTypeNode primitiveTypeNode) {
		PrimitiveTypeStartContext primitiveTypeStartContext = (PrimitiveTypeStartContext) context;
		context = primitiveTypeStartContext.primitiveType(AttributeUtil.getPrimitiveType(primitiveTypeNode));
		return null;
	}

	@Override
	public boolean exitPrimitiveTypeNode(AvroNode enumNode) {
		PrimitiveTypeFinishContext primitiveTypeFinishContext = (PrimitiveTypeFinishContext) context;
		context = primitiveTypeFinishContext.endPrimitiveType();
		return true;
	}

	@Override
	public AvroNode enterArrayNode(ArrayNode arrayNode) {
		ArrayStartContext arrayStartContext = (ArrayStartContext) context;
		ArrayContext arrayContext = arrayStartContext.array();
		context = arrayContext.type();
		if (!arrayNode.hasChildren()) {
			visitDummyPrimitiveTypeNode(arrayNode);
		}
		return arrayNode;
	}

	@Override
	public boolean exitArrayNode(AvroNode arrayNode) {
		ArrayFinishContext arrayFinishContext = (ArrayFinishContext) context;
		context = arrayFinishContext.endArray();
		return true;
	}

	@Override
	public AvroNode enterMapNode(MapNode mapNode) {
		MapStartContext mapStartContext = (MapStartContext) context;
		MapContext mapContext = mapStartContext.map();
		context = mapContext.type();
		if (!mapNode.hasChildren()) {
			visitDummyPrimitiveTypeNode(mapNode);
		}
		return mapNode;
	}

	@Override
	public boolean exitMapNode(AvroNode mapNode) {
		MapFinishContext mapFinishContext = (MapFinishContext) context;
		context = mapFinishContext.endMap();
		return true;
	}

	@Override
	public AvroNode enterRefNode(RefNode refNode) {
		AvroNode referencedNode = refNode.getReferencedNode();
		if (visitedReferencedNodes.contains(referencedNode)) {
			startReference(refNode);
			return refNode;
		} else {
			NodeType referencedNodeType = referencedNode.getType();
			switch (referencedNodeType) {
			case RECORD:
				enterRecordNode((RecordNode) referencedNode);
				break;
			case ENUM:
				enterEnumNode((EnumNode) referencedNode);
				break;
			case FIXED:
				enterFixedNode((FixedNode) referencedNode);
				break;
			default:
				throw new IllegalArgumentException("Invalid referenced node type");
			}
			visitedReferencedNodes.add(referencedNode);
			return referencedNode;
		}
	}

	protected void startReference(AvroNode node) {
		RefStartContext refStartContext = (RefStartContext) context;
		String fullName = AttributeUtil.getFullName(node);
		context = refStartContext.ref(fullName);
	}
	
	protected void endReference() {
		RefFinishContext refFinishContext = (RefFinishContext) context;
		context = refFinishContext.endRef();
	}
	
	@Override
	public boolean exitRefNode(AvroNode refNode) {
		NodeType refType = refNode.getType();
		if (refType.isRef()) {
			endReference();
		} else {
			switch (refType) {
			case RECORD:
				exitRecordNode((RecordNode) refNode);
				break;
			case ENUM:
				exitEnumNode((EnumNode) refNode);
				break;
			case FIXED:
				exitFixedNode((FixedNode) refNode);
				break;
			default:
				throw new IllegalArgumentException("Invalid referenced node type");
			}
		}
		return true;
	}	

	protected boolean hasDefaultValue(AvroNode node) {
		return AttributeUtil.hasDefaultValue(node);
	}
	
	protected Object getDefaultValue(AvroNode node) {
		// node is a field, only field have a default value
		FieldNode fieldNode = (FieldNode) node;
		String defaultValue = AttributeUtil.getDefaultValueAsString(fieldNode);
		NodeType fieldType = ModelUtil.getTypeOfNode(fieldNode);
		switch (fieldType) {
		case PRIMITIVE_TYPE:
			PrimitiveType type = AttributeUtil.getPrimitiveType(fieldNode);
			return ModelUtil.parsePrimitiveType(defaultValue, type);
		case RECORD:
			RecordNode recordNode = (RecordNode) ModelUtil.getFirstChildOfType(fieldNode, false, NodeType.RECORD);
			return DefaultValueUtil.recordDefaultValue(defaultValue, recordNode);
		case ARRAY:
			ArrayNode arrayNode = (ArrayNode) ModelUtil.getFirstChildOfType(fieldNode, false, NodeType.ARRAY);
			return DefaultValueUtil.arrayDefaultValue(defaultValue, arrayNode);
		case ENUM:
			// String
			return defaultValue;
		case FIXED:
			// TODO
			return defaultValue;
		case MAP:
			MapNode mapNode = (MapNode) ModelUtil.getFirstChildOfType(fieldNode, false, NodeType.MAP);
			return DefaultValueUtil.mapDefaultValue(defaultValue, mapNode);
		case UNION:
			// TODO
			return defaultValue;
		case REF:
			// TODO
			return defaultValue;
		default:
			return defaultValue;
		}		
	}
	
	protected void visitDummyPrimitiveTypeNode(AvroNode node) {
		PrimitiveTypeNode dummyNode = dummyPrimitiveTypeNode(node);
		enterPrimitiveTypeNode(dummyNode);
		exitPrimitiveTypeNode(dummyNode);
	}
	
	protected PrimitiveTypeNode dummyPrimitiveTypeNode(AvroNode node) {
		PrimitiveTypeNode dummyNode = new PrimitiveTypeNode(avroContext);
		dummyNode.init(dummyAttributeInitializer);
		AttributeUtil.setPrimitiveType(dummyNode, AttributeUtil.getPrimitiveType(node));
		return dummyNode;
	}
	
	/**
	 * This attribute initializer is used internally to build a dummy primitive type node. 
	 * 
	 * @author timbault
	 *
	 */
	private static class DummyAttributeInitializer implements AttributeInitializer {

		@Override
		public boolean provideInitialAttributeValue(NodeType type, String attributeName) {
			return false;
		}

		@Override
		public Object getInitialAttributeValue(NodeType type, String attributeName) {
			return null;
		}

		@Override
		public boolean isVisible(NodeType type, String attributeName) {
			return false;
		}

		@Override
		public boolean isEnabled(NodeType type, String attributeName) {
			return false;
		}
		
	}	
	
}
