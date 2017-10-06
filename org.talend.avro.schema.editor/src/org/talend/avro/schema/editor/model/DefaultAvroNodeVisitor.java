package org.talend.avro.schema.editor.model;

public class DefaultAvroNodeVisitor implements IAvroNodeVisitor {

	@Override
	public AvroNode enterRootNode(RootNode rootNode) {
		return rootNode;
	}

	@Override
	public boolean exitRootNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterRecordNode(RecordNode recordNode) {
		return recordNode;
	}

	@Override
	public boolean exitRecordNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterFieldNode(FieldNode fieldNode) {
		return fieldNode;
	}

	@Override
	public boolean exitFieldNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterUnionNode(UnionNode unionNode) {
		return unionNode;
	}

	@Override
	public boolean exitUnionNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterEnumNode(EnumNode enumNode) {
		return enumNode;
	}

	@Override
	public boolean exitEnumNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterFixedNode(FixedNode fixedNode) {
		return fixedNode;
	}

	@Override
	public boolean exitFixedNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterPrimitiveTypeNode(PrimitiveTypeNode typeNode) {
		return typeNode;
	}

	@Override
	public boolean exitPrimitiveTypeNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterArrayNode(ArrayNode arrayNode) {
		return arrayNode;
	}

	@Override
	public boolean exitArrayNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterMapNode(MapNode mapNode) {
		return mapNode;
	}

	@Override
	public boolean exitMapNode(AvroNode visitedNode) {
		return true;
	}

	@Override
	public AvroNode enterRefNode(RefNode refNode) {
		return refNode;
	}

	@Override
	public boolean exitRefNode(AvroNode visitedNode) {
		return true;
	}

}
