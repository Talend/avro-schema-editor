package org.talend.avro.schema.editor.edit.dnd;

import java.util.HashMap;
import java.util.Map;

import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This class stores all the defined drag and drop behaviors for the avro schema editor.
 * 
 * @author timbault
 *
 */
public class DragAndDropPolicy {

	/**
	 * Defines one of the three possible behavior of a drag and drop: move the dragged node, copy it or make a reference.
	 * 
	 * @author timbault
	 *
	 */
	public enum Action {
		MOVE("Move"), COPY("Copy"), REFERENCE("Reference");
		
		private String label;

		private Action(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	
	}
	
	private AvroNodeAttributesValidators validators;
	
	private Map<Action, Map<Key, Checker>> checkers = new HashMap<>();
	
	private Map<Action, Map<Key, DnDHandler>> handlers = new HashMap<>();
	
	public DragAndDropPolicy(AvroNodeAttributesValidators validators) {
		super();
		this.validators = validators;
	}

	// ************************
	// REGISTER CHECKER METHODS
	// ************************
	
	public void registerChecker(Action action, NodeType sourceNodeType, NodeType targetNodeType, TargetPosition position, Checker checker) {
		Key key = new Key(sourceNodeType, targetNodeType, position);
		registerChecker(action, key, checker);
	}
	
	public void registerChecker(Action action, NodeType sourceNodeType, NodeType targetNodeType, TargetPosition[] positions, Checker checker) {		
		for (TargetPosition position : positions) {
			registerChecker(action, sourceNodeType, targetNodeType, position, checker);
		}
	}
		
	public void registerChecker(Action action, NodeType sourceNodeType, NodeType[] targetNodeTypes, TargetPosition[] positions, Checker checker) {
		for (NodeType targetNodeType : targetNodeTypes) {
			registerChecker(action, sourceNodeType, targetNodeType, positions, checker);
		}
	}
	
	// ************************
	// REGISTER HANDLER METHODS
	// ************************
	
	public void registerDnDHandler(Action action, NodeType sourceNodeType, NodeType targetNodeType, TargetPosition position, DnDHandler handler) {
		Key key = new Key(sourceNodeType, targetNodeType, position);
		Map<Key, DnDHandler> handlers = getOrCreateHandlers(action);
		handlers.put(key, handler);
	}
	
	public void registerDnDHandler(Action action, NodeType sourceNodeType, NodeType targetNodeType, TargetPosition[] positions, DnDHandler handler) {
		for (TargetPosition position : positions) {
			registerDnDHandler(action, sourceNodeType, targetNodeType, position, handler);
		}
	}
		
	public void registerDnDHandler(Action action, NodeType sourceNodeType, NodeType[] targetNodeTypes, TargetPosition[] positions, DnDHandler handler) {
		for (NodeType targetNodeType : targetNodeTypes) {
			registerDnDHandler(action, sourceNodeType, targetNodeType, positions, handler);
		}
	}
	
	// *************
	// CHECK METHODS
	// *************
	
	public boolean acceptDnD(Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		Key key = new Key(sourceNode.getType(), targetNode.getType(), position);
		Checker checker = getChecker(action, key);
		if (checker != null) {
			return checker.accept(sourceNode, targetNode, position);
		}
		return false;
	}
	
	public boolean isHandlerDefined(Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		Key key = new Key(sourceNode.getType(), targetNode.getType(), position);
		return getHandler(action, key) != null;
	}
	
	public boolean isHandlerDefined(Action action, DnDParams dndParams) {
		Key key = new Key(dndParams.getSourceNode().getType(), dndParams.getTargetNode().getType(), dndParams.getPosition());
		return getHandler(action, key) != null;
	}
	
	public DnDParams executeDnD(Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		Key key = new Key(sourceNode.getType(), targetNode.getType(), position);
		DnDHandler handler = getHandler(action, key);
		if (handler != null) {
			return handler.executeDnD(sourceNode, targetNode, position, validators);
		}
		return null;
	}
	
	public boolean undoDnD(Action action, DnDParams dndParams) {
		Key key = new Key(dndParams.getSourceNode().getType(), dndParams.getTargetNode().getType(), dndParams.getPosition());
		DnDHandler handler = getHandler(action, key);
		if (handler != null) {
			return handler.undoDnD(dndParams, validators);
		}
		return false;
	}
	
	// *****
	// TOOLS
	// *****
	
	protected Map<Key, DnDHandler> getOrCreateHandlers(Action action) {
		Map<Key, DnDHandler> map = handlers.get(action);
		if (map == null) {
			map = new HashMap<>();
			handlers.put(action, map);
		}
		return map;
	}
	
	protected DnDHandler getHandler(Action action, Key key) {
		Map<Key, DnDHandler> map = handlers.get(action);
		if (map != null) {
			return map.get(key);
		}
		return null;
	}
	
	protected Checker getChecker(Action action, Key key) {
		Map<Key, Checker> map = checkers.get(action);
		if (map != null) {
			return map.get(key);
		}
		return null;
	}
	
	protected Map<Key, Checker> getOrCreateCheckers(Action action) {
		Map<Key, Checker> map = checkers.get(action);
		if (map == null) {
			map = new HashMap<>();
			checkers.put(action, map);
		}
		return map;
	}
	
	protected void registerChecker(Action action, Key key, Checker checker) {
		Map<Key, Checker> map = getOrCreateCheckers(action);
		map.put(key, checker);
	}
	
	public void clearAll() {
		checkers.clear();
		handlers.clear();
	}
	
	// **********
	// interfaces
	// **********
	
	/**
	 * This interface checks if the drag and drop defined by the specified source node, target node and target position is valid.
	 *  
	 * @author timbault
	 *
	 */
	public interface Checker {
		
		/**
		 * Return true is the drop of the source node on the target node at the target position is valid.
		 *  
		 * @param sourceNode
		 * @param targetNode
		 * @param position
		 * @return
		 */
		boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position);
		
	}
	
	/**
	 * A DnDHandler provides the implementation of a drag and drop behavior. 
	 * 
	 * @author timbault
	 *
	 */
	public interface DnDHandler {
		
		DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position, AvroNodeAttributesValidators validators);
		
		boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators);
		
	}
	
	/**
	 * Defines an internal key for the registered checkers and handlers. This key is defined with the combination (source node, target node, target position).
	 *  
	 * @author timbault
	 *
	 */
	private static class Key {
		
		private NodeType sourceNodeType;
		
		private NodeType targetNodeType;

		private TargetPosition position;
		
		public Key(NodeType sourceNodeType, NodeType targetNodeType, TargetPosition position) {
			super();
			this.sourceNodeType = sourceNodeType;
			this.targetNodeType = targetNodeType;
			this.position = position;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + ((sourceNodeType == null) ? 0 : sourceNodeType.hashCode());
			result = prime * result + ((targetNodeType == null) ? 0 : targetNodeType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (position != other.position)
				return false;
			if (sourceNodeType != other.sourceNodeType)
				return false;
			if (targetNodeType != other.targetNodeType)
				return false;
			return true;
		}
		
	}
	
	/**
	 * This method builds a new checker by the composition of the specified ones. If at least one checker does not accept the drag and drop, 
	 * the composite checker does not accept it.
	 * 
	 * @param checkers
	 * @return
	 */
	public static final Checker compose(final Checker... checkers) {
		return new Checker() {
			@Override
			public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
				for (Checker checker : checkers) {
					if (!checker.accept(sourceNode, targetNode, position)) {
						return false;
					}
				}
				return true;
			}
		};
	}
	
	/**
	 * Base checker which always accepts a drag and drop.
	 */
	public static final Checker ACCEPT = new Checker() {
		@Override
		public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
			return true;
		}
	};
	
	/**
	 * Checks that the source and target nodes have the same parent.
	 */
	public static final Checker SAME_PARENT = new Checker() {
		@Override
		public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
			return sourceNode.getParent() == targetNode.getParent();
		}
	};
		
	/**
	 * Checks that the source node is not an ancestor of the target node. 
	 */
	public static final Checker NOT_AN_ANCESTOR = new Checker() {			
		@Override
		public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
			if (position == TargetPosition.UPON) {
				return !ModelUtil.isAncestor(sourceNode, targetNode);
			}
			return false;
		}
	};
	
	/**
	 * Checks that the target node is not the parent of the source node.
	 */
	public static final Checker NOT_DIRECT_PARENT = new Checker() {
		@Override
		public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
			return sourceNode.getParent() != targetNode;
		}
	};
	
	public static final Checker TARGET_UNION_CHOICE = new Checker() {		
		@Override
		public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
			return targetNode.getType() == NodeType.UNION && AttributeUtil.isChoiceType(targetNode);
		}
	};
	
}
