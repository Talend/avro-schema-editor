package org.talend.avro.schema.editor.model;

/**
 * Visitor of an avro schema tree. This defines "enter" and "exit" methods for each type of node.
 * The AvroNode returned by "enter" method is the node for which children should be visited. If it returns null, the children are not visited.
 * The boolean returned by "exit" method indicates if the visitor must continue the visit of the tree. 
 * 
 * @author timbault
 *
 */
public interface IAvroNodeVisitor {

	AvroNode enterRootNode(RootNode rootNode);
	
	boolean exitRootNode(AvroNode visitedNode);
	
	AvroNode enterRecordNode(RecordNode recordNode);
	
	boolean exitRecordNode(AvroNode visitedNode);
	
	AvroNode enterFieldNode(FieldNode fieldNode);
	
	boolean exitFieldNode(AvroNode visitedNode);	
	
	AvroNode enterUnionNode(UnionNode unionNode);
	
	boolean exitUnionNode(AvroNode visitedNode);

	AvroNode enterEnumNode(EnumNode enumNode);
	
	boolean exitEnumNode(AvroNode visitedNode);

	AvroNode enterFixedNode(FixedNode fixedNode);
	
	boolean exitFixedNode(AvroNode visitedNode);
	
	AvroNode enterPrimitiveTypeNode(PrimitiveTypeNode typeNode);
	
	boolean exitPrimitiveTypeNode(AvroNode visitedNode);

	AvroNode enterArrayNode(ArrayNode arrayNode);
	
	boolean exitArrayNode(AvroNode visitedNode);
	
	AvroNode enterMapNode(MapNode mapNode);
	
	boolean exitMapNode(AvroNode visitedNode);
	
	AvroNode enterRefNode(RefNode refNode);
	
	boolean exitRefNode(AvroNode visitedNode);
	
}
