package org.talend.avro.schema.editor.edit;

import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.edit.dnd.CopyStrategyProvider;
import org.talend.avro.schema.editor.edit.dnd.DnDParams;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicyConfiguration;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.cmd.Direction;

/**
 * This interface provides methods in order to edit avro schema model.
 * Essential rule: all the methods must lead to a valid schema.
 * 
 * @author timbault
 *
 */
public interface AvroSchemaController extends IContextualService {
	
	// ************************
	// controller configuration
	// ************************
	
	void setAttributeInitializer(AttributeInitializer attributeInitializer);
	
	AttributeInitializer getAttributeInitializer();
	
	void setAvroNodeValidators(AvroNodeAttributesValidators validators);
	
	void setDragAndDropPolicyConfiguration(DragAndDropPolicyConfiguration dndPolicyConfiguration);
	
	void setCopyStrategyProvider(CopyStrategyProvider copyStrategyProvider);
	
	CopyStrategyProvider getCopyStrategyProvider();
	
	// ***************************
	// methods to edit avro schema
	// ***************************
	
	/**
	 * Returns true if a new avro element specified by its type can be added to the given target node.
	 * This method should be always called before the addElement method.
	 * 
	 * @param targetNode
	 * @param type
	 * @param position
	 * @return
	 */
	boolean canAddElement(AvroNode targetNode, NodeType type, TargetPosition position);
	
	/**
	 * Create a new avro element of the specified type and add it to the given target node.
	 * This method must perform all the needed operations (link new node with its parent, register new element in schema registry, validate attributes and so on).
	 * Launches an exception if some avro basic rules are violated.
	 * The method canAddElement above should always be called before this one.
	 * 
	 * @param targetNode
	 * @param type
	 * @param position
	 * @return the created avro element.
	 */
	AvroNode addElement(AvroNode targetNode, NodeType type, TargetPosition position);
		
	AvroNode addElement(AvroNode targetNode, AvroNode elementNode, TargetPosition position);
	
	boolean canRemoveElement(AvroNode elementNode);
		
	AvroNode removeElement(AvroNode elementNode);
		
	// methods to edit attributes
	
	void setPrimitiveType(AvroNode node, PrimitiveType type);
	
	AvroNode switchArrayMap(AvroNode node);
	
	void setOptional(AvroNode node, boolean optional);
	
	void setChoice(UnionNode unionNode, boolean choice);
	
	// drag and drop methods
	
	boolean canMoveInDirection(AvroNode node, Direction direction);
	
	boolean canDnDElement(DragAndDropPolicy.Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position);
	
	DnDParams executeDnDElement(DragAndDropPolicy.Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position);
	
	void undoDnDElement(DragAndDropPolicy.Action action, DnDParams dndParams);
		
}
