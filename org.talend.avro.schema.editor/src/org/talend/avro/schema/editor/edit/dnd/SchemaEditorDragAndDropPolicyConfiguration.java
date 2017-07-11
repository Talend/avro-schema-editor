package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Checker;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;

/**
 * Drag and drop configuration for the Avro Schema Editor. 
 * <p>
 * It defines all the DnD behaviors (move, copy, reference) for all the possible source/target node pairs.
 * 
 * @author timbault
 *
 */
public class SchemaEditorDragAndDropPolicyConfiguration implements DragAndDropPolicyConfiguration {

	private AvroContext context;
	
	private static final DragAndDropPolicy.Checker MOVE_COPY_ARRAY_MAP_CHECKER = new MoveOrCopyArrayOrMapChecker();
	
	private static final DragAndDropPolicy.Checker MOVE_NAME_SPACED_ELEMENT_CHECKER = new MoveNameSpacedElementChecker();
	
	private static final DragAndDropPolicy.DnDHandler MOVE_AT_SAME_LEVEL_HANDLER = new MoveAtSameLevelHandler();
	
	private static final NodeType[] POSSIBLE_CHOICE_TYPES = new NodeType[] {
		NodeType.REF, NodeType.RECORD, NodeType.ENUM, NodeType.FIXED, NodeType.ARRAY, NodeType.MAP, NodeType.PRIMITIVE_TYPE	
	};
	
	private static final NodeType[] SUB_CHOICE_TYPES = new NodeType[] {
		NodeType.REF, NodeType.RECORD, NodeType.ENUM, NodeType.FIXED, NodeType.PRIMITIVE_TYPE	
	};
		
	private static final NodeType[] TYPED_NODE_TYPES_WITH_UNION = new NodeType[] {
		NodeType.FIELD, NodeType.ARRAY, NodeType.MAP, NodeType.UNION	
	};
	
	private static final NodeType[] ARRAY_AND_MAP = NodeType.ARRAY_OR_MAP;
	
	private static final TargetPosition[] UPON = new TargetPosition[] { TargetPosition.UPON };
	
	public SchemaEditorDragAndDropPolicyConfiguration(AvroContext context) {
		super();
		this.context = context;
	}

	@Override
	public void configureDragAndDropPolicy(DragAndDropPolicy dndPolicy) {
		
		// MOVE field
		// field -> field
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.FIELD, NodeType.FIELD, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.FIELD, NodeType.FIELD, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// COPY field
		// field -> field
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.FIELD, NodeType.FIELD, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);

		// MOVE field
		// field -> record
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.FIELD, NodeType.RECORD, TargetPosition.UPON, 
				compose(DragAndDropPolicy.NOT_DIRECT_PARENT, new MoveFieldToRecordChecker()));
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.FIELD, NodeType.RECORD, TargetPosition.UPON, new MoveFieldToRecordHandler(context));
		
		// COPY field
		// field -> record
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.FIELD, NodeType.RECORD, TargetPosition.UPON, DragAndDropPolicy.NOT_DIRECT_PARENT);

		// MOVE record
		// record -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, TYPED_NODE_TYPES_WITH_UNION, UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT, MOVE_NAME_SPACED_ELEMENT_CHECKER)); 
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, TYPED_NODE_TYPES_WITH_UNION, UPON, new MoveNameSpacedElementHandler(context));
		
		// MOVE record (at root level or under a choice node)
		// record -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// MOVE (under a choice node)
		// record -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.RECORD, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// COPY/REFERENCE of a record
		// record -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.RECORD, TYPED_NODE_TYPES_WITH_UNION, UPON, DragAndDropPolicy.NOT_DIRECT_PARENT);
		dndPolicy.registerChecker(DragAndDropPolicy.Action.REFERENCE, NodeType.RECORD, TYPED_NODE_TYPES_WITH_UNION, UPON, MOVE_NAME_SPACED_ELEMENT_CHECKER);

		// MOVE enum
		// enum -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, TYPED_NODE_TYPES_WITH_UNION, UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT, MOVE_NAME_SPACED_ELEMENT_CHECKER)); 
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, TYPED_NODE_TYPES_WITH_UNION, UPON, new MoveNameSpacedElementHandler(context));
		
		// MOVE (at root level or under a choice node)
		// enum -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// MOVE (under a choice node)
		// enum -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.ENUM, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// COPY/REFERENCE enum
		// enum -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.ENUM, TYPED_NODE_TYPES_WITH_UNION, UPON, DragAndDropPolicy.NOT_DIRECT_PARENT);
		dndPolicy.registerChecker(DragAndDropPolicy.Action.REFERENCE, NodeType.ENUM, TYPED_NODE_TYPES_WITH_UNION, UPON, MOVE_NAME_SPACED_ELEMENT_CHECKER);
		
		// MOVE fixed
		// fixed -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, TYPED_NODE_TYPES_WITH_UNION, UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT, MOVE_NAME_SPACED_ELEMENT_CHECKER)); 
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, TYPED_NODE_TYPES_WITH_UNION, UPON, new MoveNameSpacedElementHandler(context));
		
		// MOVE (at root level or under a choice node)
		// fixed -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);		
		
		// MOVE (under a choice node)
		// fixed -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.FIXED, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// COPY/REFERENCE enum
		// fixed -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.FIXED, TYPED_NODE_TYPES_WITH_UNION, UPON, DragAndDropPolicy.NOT_DIRECT_PARENT);
		dndPolicy.registerChecker(DragAndDropPolicy.Action.REFERENCE, NodeType.FIXED, TYPED_NODE_TYPES_WITH_UNION, UPON, MOVE_NAME_SPACED_ELEMENT_CHECKER);
		
		// MOVE ref node
		// ref -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.REF, TYPED_NODE_TYPES_WITH_UNION, UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT, MOVE_NAME_SPACED_ELEMENT_CHECKER)); 
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.REF, TYPED_NODE_TYPES_WITH_UNION, UPON, new MoveNameSpacedElementHandler(context));
		
		// MOVE ref node (at root level or under a choice node)
		// ref -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.REF, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.REF, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);		

		// MOVE ref node (under a choice node)
		// ref -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.REF, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.REF, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// COPY/REFERENCE ref node
		// ref -> [field, array, map, union]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.REF, TYPED_NODE_TYPES_WITH_UNION, UPON, DragAndDropPolicy.NOT_DIRECT_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.COPY, NodeType.REF, TYPED_NODE_TYPES_WITH_UNION, UPON, 
				new CopyRefNodeHandler(context, getCopyEngine(context)));
		dndPolicy.registerChecker(DragAndDropPolicy.Action.REFERENCE, NodeType.REF, TYPED_NODE_TYPES_WITH_UNION, UPON, MOVE_NAME_SPACED_ELEMENT_CHECKER);
			
		// MOVE primitive type node (under a choice node)
		// primitive type -> [record, enum, fixed, ref, array, map, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.PRIMITIVE_TYPE, POSSIBLE_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.PRIMITIVE_TYPE, POSSIBLE_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// MOVE primitive type
		// primitive type -> union (choice)
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.PRIMITIVE_TYPE, NodeType.UNION, TargetPosition.UPON, 
				compose(DragAndDropPolicy.NOT_DIRECT_PARENT, new MovePrimitiveTypeChecker()));
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.PRIMITIVE_TYPE, NodeType.UNION, TargetPosition.UPON, 
				new MovePrimitiveTypeHandler(context));
		
		// COPY primitive type
		// primitive type -> union (choice)
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.PRIMITIVE_TYPE, NodeType.UNION, TargetPosition.UPON, 
				compose(DragAndDropPolicy.NOT_DIRECT_PARENT, new MovePrimitiveTypeChecker()));
		
		// MOVE/COPY (upon typed element target)
		// array -> [field, array, map]
		registerMoveAndCopyCheckerAndHandler(NodeType.ARRAY, NodeType.TYPED_NODE_TYPES, dndPolicy);
		// array -> union
		registerMoveAndCopyCheckerAndHandler(NodeType.ARRAY, NodeType.UNION, dndPolicy);
		
		// MOVE (under a choice node)
		// array -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.ARRAY, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.ARRAY, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);
		
		// MOVE (under a choice node, only before or after target array/map)
		// array -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.ARRAY, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.ARRAY, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
			
		// MOVE/COPY (upon typed element target)
		// map -> [field, array, map]
		registerMoveAndCopyCheckerAndHandler(NodeType.MAP, NodeType.TYPED_NODE_TYPES, dndPolicy);		
		// map -> union
		registerMoveAndCopyCheckerAndHandler(NodeType.MAP, NodeType.UNION, dndPolicy);				
		
		// MOVE (under a choice node)
		// map -> [record, enum, fixed, ref, primitive type]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.MAP, SUB_CHOICE_TYPES, TargetPosition.ALL, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.MAP, SUB_CHOICE_TYPES, TargetPosition.ALL, MOVE_AT_SAME_LEVEL_HANDLER);

		// MOVE (under a choice node, only before or after target array/map)
		// map -> [array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.MAP, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, DragAndDropPolicy.SAME_PARENT);
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.MAP, ARRAY_AND_MAP, TargetPosition.BEFORE_AND_AFTER, MOVE_AT_SAME_LEVEL_HANDLER);
				
		// MOVE union (choice)
		// union -> [field, array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, NodeType.UNION, NodeType.TYPED_NODE_TYPES, UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT, new MoveUnionChoiceChecker()));
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, NodeType.UNION, NodeType.TYPED_NODE_TYPES, UPON, new MoveUnionChoiceHandler(context));
		
		// COPY union (choice)
		// union -> [field, array, map]
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, NodeType.UNION, NodeType.TYPED_NODE_TYPES, UPON,
				compose(DragAndDropPolicy.NOT_DIRECT_PARENT, new MoveUnionChoiceChecker()));
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.COPY, NodeType.UNION, NodeType.TYPED_NODE_TYPES, UPON,
				new CopyUnionChoiceHandler(context, getCopyEngine(context)));
		
	}

	protected void registerMoveAndCopyCheckerAndHandler(NodeType sourceType, NodeType[] targetTypes, DragAndDropPolicy dndPolicy) {
		for (NodeType targetType : targetTypes) {
			registerMoveAndCopyCheckerAndHandler(sourceType, targetType, dndPolicy);
		}
	}
	
	protected void registerMoveAndCopyCheckerAndHandler(NodeType sourceType, NodeType targetType, DragAndDropPolicy dndPolicy) {
		dndPolicy.registerChecker(DragAndDropPolicy.Action.MOVE, sourceType, targetType, TargetPosition.UPON, 
				compose(DragAndDropPolicy.NOT_AN_ANCESTOR, DragAndDropPolicy.NOT_DIRECT_PARENT,	MOVE_COPY_ARRAY_MAP_CHECKER));		
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.MOVE, sourceType, targetType, TargetPosition.UPON,
				new MoveArrayOrMapHandler(context));
		dndPolicy.registerChecker(DragAndDropPolicy.Action.COPY, sourceType, targetType, TargetPosition.UPON,
				compose(DragAndDropPolicy.NOT_DIRECT_PARENT, MOVE_COPY_ARRAY_MAP_CHECKER));
		dndPolicy.registerDnDHandler(DragAndDropPolicy.Action.COPY, sourceType, targetType, TargetPosition.UPON,
				new CopyArrayOrMapHandler(context, getCopyEngine(context)));
	}
	
	protected CopyEngine getCopyEngine(AvroContext context) {
		return new AvroSchemaCopyEngine(context);
	}
	
	protected static final Checker compose(Checker... checkers) {
		return DragAndDropPolicy.compose(checkers);
	}
	
}
