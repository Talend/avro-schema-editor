package org.talend.avro.schema.editor.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.cmd.Direction;

/**
 * This class provides some convenient methods for editing model.
 * 
 * @author timbault
 *
 */
public class EditUtils {

	/**
	 * Return the avro node types which can be added to the specified target node. This method uses the canAddElement of the controller.
	 * 
	 * @param targetNode
	 * @param schemaController
	 * @return
	 */
	public static NodeType[] getAddableNodeTypes(AvroNode targetNode, AvroSchemaController schemaController) {
		List<NodeType> types = new ArrayList<>();
		for (NodeType type : NodeType.values()) {
			if (schemaController.canAddElement(targetNode, type, TargetPosition.UPON)) {
				types.add(type);
			}
		}
		return types.toArray(new NodeType[types.size()]);
	}
	
	public static List<AvroNode> keepAncestors(List<AvroNode> contextualNodes) {
		
		Set<AvroNode> ancestors = new HashSet<>();
		
		for (AvroNode node : contextualNodes) {
			
			if (ancestors.isEmpty()) {
				ancestors.add(node);
			} else {
				Set<AvroNode> toBeRemoved = new HashSet<>();
				Set<AvroNode> toBeAdded = new HashSet<>();
				for (AvroNode refNode : ancestors) {
					if (ModelUtil.isAncestor(node, refNode)) {
						toBeRemoved.add(refNode);
						toBeAdded.add(node);
					} else if (ModelUtil.isAncestor(refNode, node)) {
						// refNode is kept
						// node is not added
					} else {
						toBeAdded.add(node);
					}
				}
				ancestors.removeAll(toBeRemoved);
				ancestors.addAll(toBeAdded);
			}
			
		}
		
		return new ArrayList<>(ancestors);
	}
	
	public static List<AvroNode> cleanContextualNodesForRemove(List<AvroNode> contextualNodes) {
		return keepAncestors(contextualNodes);
	}
	
	public static List<AvroNode> cleanContextualNodesForCopy(List<AvroNode> contextualNodes) {
		return keepAncestors(contextualNodes);
	}
	
	public static List<AvroNode> prepareNodesForDirectionalMove(List<AvroNode> nodes, final Direction direction) {
		
		Map<AvroNode, List<AvroNode>> parent2children = new HashMap<>();
		
		// first populate map<parent, children>
		for (AvroNode node : nodes) {
			AvroNode parent = node.getParent();
			if (parent != null) {
				List<AvroNode> children = parent2children.get(parent);
				if (children == null) {
					children = new ArrayList<>();
					parent2children.put(parent, children);
				}
				children.add(node);
			}
		}
		
		// then sort the children according to the direction of the move		
		for (Map.Entry<AvroNode, List<AvroNode>> entry : parent2children.entrySet()) {
			final AvroNode parent = entry.getKey();
			Comparator<AvroNode> comparator = new Comparator<AvroNode>() {
				@Override
				public int compare(AvroNode node1, AvroNode node2) {
					if (direction == Direction.UP) {
						return parent.getChildIndex(node1) - parent.getChildIndex(node2);
					} else {
						return parent.getChildIndex(node2) - parent.getChildIndex(node1);
					}
				}
				
			};
			List<AvroNode> children = entry.getValue();
			Collections.sort(children, comparator);
		}
		
		// then return sorted nodes as single list
		List<AvroNode> result = new ArrayList<>();
		for (Map.Entry<AvroNode, List<AvroNode>> entry : parent2children.entrySet()) {
			result.addAll(entry.getValue());
		}
		
		return result;
		
	}
	
}
