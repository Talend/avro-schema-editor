package org.talend.avro.schema.editor.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of a {@link SchemaViewerContentProvider} for the Avro Schema Editor.
 * 
 * @author timbault
 *
 */
public class SchemaViewerContentProviderImpl implements SchemaViewerContentProvider {

	private static final AvroNode[] EMPTY_CHILDREN = new AvroNode[0];

	private AvroContext context;
	
	public SchemaViewerContentProviderImpl(AvroContext context) {
		super();
		this.context = context;
	}

	@Override
	public boolean hasChildren(AvroNode node) {
		return getChildren(node).length > 0;
	}
	
	@Override
	public AvroNode[] getChildren(AvroNode node) {
		NodeType type = node.getType();
		switch (type) {
		case ROOT:
			return getRootChildren((RootNode) node);
		case RECORD:
			return getRecordChildren((RecordNode) node);
		case FIELD:
			return getFieldChildren((FieldNode) node);		
		case UNION:
			UnionNode unionNode = (UnionNode) node;
			if (AttributeUtil.isChoiceType(unionNode)) {
				// in this case the union node is displayed, it has children
				return getUnionChildren(unionNode.getParent(), unionNode);
			} else {
				throw new IllegalArgumentException("Union node for optional type is never displayed");
			}
		case MAP:
		case ARRAY:
			return getArrayOrMapChildren(node, true);
		case ENUM:
		case FIXED:
		case PRIMITIVE_TYPE:
			return EMPTY_CHILDREN;
		case REF:
			// ref node has no children
			return EMPTY_CHILDREN;
		default:
			return EMPTY_CHILDREN;
		}		
	}	
	
	protected AvroNode[] getRootChildren(RootNode rootNode) {
		if (rootNode.hasChildren()) {
			// 2 cases: union or namespaced type, but only one child
			AvroNode child = rootNode.getChild(0);
			if (child.getType() == NodeType.UNION) {
				// children of the first union are necessarily complex types
				return asArray(child.getChildren());
			} else {
				// it is necessarily a record/fixed/enum node, display it
				return asArray(child);
			}
		}
		return EMPTY_CHILDREN;		 
	}
	
	protected AvroNode[] getRecordChildren(RecordNode recordNode) {
		// always display the record children
		return asArray(recordNode.getChildren());
	}
	
	protected AvroNode[] getFieldChildren(FieldNode fieldNode) {
		if (fieldNode.hasChildren()) {
			// field node has only one child
			AvroNode fieldChild = fieldNode.getChild(0);
			NodeType fieldChildType = fieldChild.getType();
			switch (fieldChildType) {
			case RECORD:
			case FIXED:
			case ENUM:
			case REF:
			case MAP:
			case ARRAY:
				return asArray(fieldChild);
			case UNION:
				if (AttributeUtil.isChoiceType(fieldChild)) {
					// in this case display the union node
					return asArray(fieldChild);
				} else {
					return getUnionChildren(fieldNode, (UnionNode) fieldChild);
				}
			default:
				break;
			}
		}
		return EMPTY_CHILDREN;
	}
		
	protected AvroNode[] getArrayOrMapChildren(AvroNode arrayOrMapNode, boolean displayed) {
		if (arrayOrMapNode.hasChildren()) {
			// array or map have only one child
			AvroNode child = arrayOrMapNode.getChild(0);
			NodeType type = child.getType();
			switch (type) {
			case RECORD:
			case FIXED:
			case ENUM:
			case REF:
			case MAP:
			case ARRAY:
				return asArray(child);			
			case UNION:
				if (AttributeUtil.isChoiceType(child)) {
					// in this case display the union node
					return asArray(child);
				} else {
					return getUnionChildren(arrayOrMapNode, (UnionNode) child);
				}
			default:
				break;
			}
		}
		return EMPTY_CHILDREN;
	}
	
	protected AvroNode[] getUnionChildren(AvroNode unionParentNode, UnionNode unionNode) {
		if (unionNode.hasChildren()) {
			if (ModelUtil.isRootUnion(unionNode)) {
				// just returns its children
				return asArray(unionNode.getChildren());
			} else if (AttributeUtil.isChoiceType(unionNode)) {
				// multi choices union
				List<AvroNode> unionChildrenList = new ArrayList<>();
				for (int i = 0; i < unionNode.getChildrenCount(); i++) {
					AvroNode child = unionNode.getChild(i);
					if (child.getType() != NodeType.PRIMITIVE_TYPE || AttributeUtil.getPrimitiveType(child) != PrimitiveType.NULL) {
						unionChildrenList.add(child);
					}
				}
				return asArray(unionChildrenList);
			} else {
				// optional union
				NodeType unionParentType = unionParentNode.getType();
				AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
				NodeType unionChildType = notNullChild.getType();
				switch (unionParentType) {
				case FIELD:
					switch (unionChildType) {
					case RECORD:
					case FIXED:
					case ENUM:
					case REF:
					case MAP:
					case ARRAY:
						// display it
						return asArray(notNullChild);				
					}
					break;
				case MAP:
				case ARRAY:					
					if (unionChildType != NodeType.PRIMITIVE_TYPE) {
						// display it
						return asArray(notNullChild);
					}
				default:
					break;
				}
			}
		}
		return EMPTY_CHILDREN;
	}
		
	@Override
	public AvroNode getParent(AvroNode node) {
		NodeType type = node.getType();
		switch (type) {
		case ROOT:
			return null;
		case RECORD:
		case ENUM:
		case FIXED:
		case PRIMITIVE_TYPE:
		case REF:
			return getComplexTypeParent(node);
		case FIELD:
			return getFieldParent((FieldNode) node);
		case UNION:
			if (AttributeUtil.isChoiceType(node)) {
				return node.getParent();
			} else {
				// should never happen since union node for optional case are never displayed
				throw new IllegalArgumentException("Union node are never displayed");
			}
		case MAP:
		case ARRAY:
			return getComplexTypeParent(node);
		}
		return null;
	}
	
	protected AvroNode getComplexTypeParent(AvroNode node) {
		// for RECORD, FIXED, ENUM and REF
		AvroNode nodeParent = node.getParent();
		if (nodeParent == null) {
			return null;
		}
		NodeType nodeParentType = nodeParent.getType();
		switch (nodeParentType) {
		case ROOT:
		case FIELD:
			return nodeParent;
		case MAP:
		case ARRAY:			
			AvroNode firstParentOfType = ModelUtil.getFirstParentOfType(nodeParent, false, NodeType.MAP, NodeType.ARRAY, NodeType.FIELD);
			if (firstParentOfType == null) {
				// should not happen
				throw new IllegalStateException("Invalid schema!");
			}
			NodeType firstTypeFound = firstParentOfType.getType();
			if (firstTypeFound == NodeType.FIELD) {
				// the found field is the displayed parent of this node
				return firstParentOfType;
			} else {
				// as we have found another map/array node, the parent of record must be displayed
				return nodeParent;
			}
		case UNION:
			if (AttributeUtil.isChoiceType(nodeParent)) {
				// in this case we display the union node so return it.
				return nodeParent;
			} else {
				firstParentOfType = ModelUtil.getFirstParentOfType(nodeParent, false, NodeType.MAP, NodeType.ARRAY, NodeType.FIELD, NodeType.ROOT);
				if (firstParentOfType == null) {
					// should not happen
					throw new IllegalStateException("Invalid schema!");
				}
				firstTypeFound = firstParentOfType.getType();
				if (firstTypeFound == NodeType.ROOT || firstTypeFound == NodeType.FIELD) {
					// return it
					return firstParentOfType;
				} else {
					// map or array
					// second step
					AvroNode firstParentOfType2 = ModelUtil.getFirstParentOfType(firstParentOfType, false, NodeType.MAP, NodeType.ARRAY, NodeType.FIELD);
					if (firstParentOfType2 == null) {
						// should not happen
						throw new IllegalStateException("Invalid schema!");
					}
					if (firstTypeFound == NodeType.FIELD) {
						// the found field is the displayed parent of this node
						return firstParentOfType2;
					} else {
						// as we have found another map/array node, we display the first map/array
						return firstParentOfType;
					}
				}
			}
		}
		return null;
	}

	protected AvroNode getFieldParent(FieldNode fieldNode) {
		return fieldNode.getParent();
	}
	
	protected AvroNode[] asArray(AvroNode... nodes) {
		return nodes;
	}
	
	protected AvroNode[] asArray(Collection<AvroNode> nodes) {
		return nodes.toArray(new AvroNode[nodes.size()]);
	}

	@Override
	public List<AvroNode> getNodesToRefresh(AvroNode node) {
		List<AvroNode> nodes = new ArrayList<>();
		
		if (node.getType().hasNameSpace()) {
			
			// it is a record, enum or fixed node
			// it can be referenced by ref nodes
			// these ref nodes must be refreshed too
			nodes.add(node);
			nodes.addAll(context.getSchemaRegistry().getRefNodes(node));
			
		} else {
		
			FieldNode fieldNode = ModelUtil.getFirstParentOfType(node, true, FieldNode.class);			
			if (fieldNode != null) {
				
				// check if it is a field of a referenced record (i.e. a record referenced by some ref nodes)
				// if it is , we have to refresh the record (and the ref nodes) instead of the field
				
				RecordNode record = ModelUtil.getFirstParentOfType(fieldNode, false, RecordNode.class);
				if (context.getSchemaRegistry().hasRefNodes(record)) {
					nodes.add(record);
					nodes.addAll(context.getSchemaRegistry().getRefNodes(record));
				} else {
					// just refresh the field
					nodes.add(fieldNode);
				}
				
			}
		
		}
		
		return nodes;
	}
	
}
