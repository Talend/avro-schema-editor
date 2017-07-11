package org.talend.avro.schema.editor.commands;

import java.util.List;

import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.cmd.Direction;
import org.talend.avro.schema.editor.registry.NSNode;

/**
 * This factory creates all the needed editing commands.
 * 
 * @author timbault
 *
 */
public interface IEditCommandFactory extends IContextualService {

	/**
	 * Create a simple composite command.
	 * 
	 * @param cmdLabel
	 * @return
	 */
	CompositeCommand createCompositeCommand(String cmdLabel);
	
	/**
	 * Create a schema composite command.
	 * @param label
	 * @param notifications
	 * @return
	 */
	SchemaEditCompositeCommand createCompositeCommand(String label, int notifications);	
	
	/**
	 * Return a command which adds a new element of the specified type under the given parent node.
	 * 
	 * @param targetNode
	 * @param type
	 * @param notifications
	 * @return
	 */
	IEditCommand createAddElementCommand(AvroNode targetNode, NodeType type, int notifications);
	
	/**
	 * Return a command which moves the specified node in the given direction (Up/Down).
	 * 
	 * @param node
	 * @param direction
	 * @param notifications
	 * @return
	 */
	IEditCommand createMoveUpDownElementCommand(AvroNode node, Direction direction, int notifications);
	
	/**
	 * Return a command which removes the specified single node.
	 * 
	 * @param node
	 * @param notifications
	 * @return
	 */
	IEditCommand createRemoveElementCommand(AvroNode node, int notifications);
	
	/**
	 * Return a command which removes all the specified nodes.
	 * 
	 * @param nodes
	 * @param notifications
	 * @return
	 */
	IEditCommand createRemoveElementsCommand(List<AvroNode> nodes, int notifications);
	
	/**
	 * Return a command which performs a drag and drop action (Move/Copy/Reference). See DragAndDropPolicy.
	 * 
	 * @param action
	 * @param sourceNode
	 * @param targetNode
	 * @param position
	 * @param notifications
	 * @return
	 */
	IEditCommand createDnDElementCommand(DragAndDropPolicy.Action action, AvroNode sourceNode, AvroNode targetNode, 
			TargetPosition position, int notifications);
	
	// Name spaces
	
	IEditCommand createRenameNameSpaceCommand(NSNode nsNode, String newName, int notifications);
	
	IEditCommand createAddNameSpaceCommand(NSNode parentNode, String name, int notifications);
	
	IEditCommand createRemoveNameSpaceCommand(NSNode node, int notifications);
	
	IEditCommand createChangeNameSpaceCommand(AvroNode node, NSNode targetNSNode, int notifications);
	
}
