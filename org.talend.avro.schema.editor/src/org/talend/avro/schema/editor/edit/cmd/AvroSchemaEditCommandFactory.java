package org.talend.avro.schema.editor.edit.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.avro.schema.editor.commands.CompositeCommand;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AbstractContextualService;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Action;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.cmd.AddElementCommand;
import org.talend.avro.schema.editor.model.cmd.CopyElementCommand;
import org.talend.avro.schema.editor.model.cmd.Direction;
import org.talend.avro.schema.editor.model.cmd.MoveElementCommand;
import org.talend.avro.schema.editor.model.cmd.MoveUpDownElementCommand;
import org.talend.avro.schema.editor.model.cmd.ReferenceElementCommand;
import org.talend.avro.schema.editor.model.cmd.RemoveElementCommand;
import org.talend.avro.schema.editor.model.cmd.SetChoiceTypeCommand;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.registry.cmd.AddNSNodeCommand;
import org.talend.avro.schema.editor.registry.cmd.ChangeNameSpaceCommand;
import org.talend.avro.schema.editor.registry.cmd.RemoveNameSpaceCommand;
import org.talend.avro.schema.editor.registry.cmd.RenameNameSpaceCommand;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of an {@link IEditCommandFactory} for the standard avro schema editor.
 * 
 * @author timbault
 *
 */
public class AvroSchemaEditCommandFactory extends AbstractContextualService implements IEditCommandFactory {
	
	@Override
	public CompositeCommand createCompositeCommand(String cmdLabel) {
		return new CompositeCommand(cmdLabel);
	}

	@Override
	public SchemaEditCompositeCommand createCompositeCommand(String label, int notifications) {
		return new SchemaEditCompositeCommand(label, getContext(), notifications);
	}	

	@Override
	public IEditCommand createAddElementCommand(AvroNode parentNode, NodeType type, int notifications) {
		if (type == NodeType.UNION) {
			// set or add choice?
			// check if parent node has an union node
			// set choice => use specific command SetChoiceTypeCommand
			if (parentNode.hasChildren(NodeType.UNION)) {
				UnionNode unionNode = (UnionNode) parentNode.getChild(0);
				return new SetChoiceTypeCommand(getContext(), unionNode, true, notifications);
			}
		}
		return new AddElementCommand(getContext(), parentNode, type, notifications);
	}

	@Override
	public IEditCommand createMoveUpDownElementCommand(AvroNode node, Direction direction, int notifications) {
		return new MoveUpDownElementCommand(getContext(), node, direction, notifications);
	}
	
	@Override
	public IEditCommand createRemoveElementCommand(AvroNode node, int notifications) {
		return new RemoveElementCommand(getContext(), node, notifications);
	}

	@Override
	public IEditCommand createRemoveElementsCommand(List<AvroNode> nodes, int notifications) {
		
		List<AvroNode> standardNodes = new ArrayList<>();
		Map<UnionNode, List<AvroNode>> choiceMap = new HashMap<>();
		
		// check if there are children of choice nodes.
		// these nodes need a specific behavior.
		for (AvroNode node : nodes) {
			AvroNode parent = node.getParent();
			if (parent != null && parent.getType() == NodeType.UNION && AttributeUtil.isChoiceType(parent)) {
				UnionNode unionNode = (UnionNode) parent;
				List<AvroNode> choices = choiceMap.get(unionNode);
				if (choices == null) {
					choices = new ArrayList<>();
					choiceMap.put(unionNode, choices);
				}
				choices.add(node);
			} else {
				standardNodes.add(node);
			}
		}

		// this is the main composite command
		SchemaEditCompositeCommand mainCmd = new SchemaEditCompositeCommand("Remove elements", getContext(), notifications);
		
		// we have to check if we are going to remove all the children of a choice node. in this case we have to remove also the choice node.
		for (Map.Entry<UnionNode, List<AvroNode>> entry : choiceMap.entrySet()) {
			UnionNode choiceNode = entry.getKey();
			List<AvroNode> choices = entry.getValue();
			SchemaEditCompositeCommand removeChoicesCmd = null;
			if (choiceNode.getChildrenCount() == choices.size()) {
				// we remove all the children if the choice node
				// we have to remove also the choice node
				removeChoicesCmd = new SchemaEditCompositeCommand("Remove choices", getContext(), notifications);
				for (int i = 0; i < choices.size(); i++) {
					AvroNode child = choices.get(i);
					IEditCommand delCmd = null;
					if (i < choices.size() - 1) {
						// create a standard remove cmd
						delCmd = createRemoveElementCommand(child, notifications);						
					} else {
						// last child, remove directly the choice node
						delCmd = createRemoveElementCommand(choiceNode, notifications);
					}
					removeChoicesCmd.addCommand(delCmd);
				}
			} else {
				// standard case
				removeChoicesCmd = new SchemaEditCompositeCommand("Remove choices", getContext(), notifications);
				for (AvroNode node : choices) {
					IEditCommand delCmd = createRemoveElementCommand(node, notifications);
					removeChoicesCmd.addCommand(delCmd);
				}
			}
			mainCmd.addCommand(removeChoicesCmd);
		}
		
		// don't forget the other nodes
		SchemaEditCompositeCommand removeNodesCmd = new SchemaEditCompositeCommand("Remove nodes", getContext(), notifications);
		for (AvroNode node : standardNodes) {
			IEditCommand delCmd = createRemoveElementCommand(node, notifications);
			removeNodesCmd.addCommand(delCmd);
		}
		mainCmd.addCommand(removeNodesCmd);
		
		// return the main composite command
		return mainCmd;
	}

	public IEditCommand createMoveElementCommand(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			int notifications) {
		return new MoveElementCommand(getContext(), sourceNode, targetNode, position, notifications);
	}

	public IEditCommand createCopyElementCommand(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			int notifications) {
		return new CopyElementCommand(getContext(), sourceNode, targetNode, position, notifications);
	}
	
	public IEditCommand createReferenceElementCommand(AvroNode sourceNode, AvroNode targetNode,
			TargetPosition position, int notifications) {
		return new ReferenceElementCommand(getContext(), sourceNode, targetNode, position, notifications);
	}

	@Override
	public IEditCommand createDnDElementCommand(Action action, AvroNode sourceNode, AvroNode targetNode,
			TargetPosition position, int notifications) {
		switch (action) {
		case COPY:
			return createCopyElementCommand(sourceNode, targetNode, position, notifications);
		case MOVE:
			return createMoveElementCommand(sourceNode, targetNode, position, notifications);
		case REFERENCE:
			return createReferenceElementCommand(sourceNode, targetNode, position, notifications);
		}
		return null;
	}

	@Override
	public IEditCommand createRenameNameSpaceCommand(NSNode nsNode, String newName, int notifications) {
		RenameNameSpaceCommand cmd = new RenameNameSpaceCommand(getContext(), nsNode, newName, notifications);
		cmd.init();
		return cmd;
	}

	@Override
	public IEditCommand createAddNameSpaceCommand(NSNode parentNode, String name, int notifications) {		
		return new AddNSNodeCommand(getContext(), parentNode, name, notifications);
	}

	@Override
	public IEditCommand createRemoveNameSpaceCommand(NSNode node, int notifications) {		
		RemoveNameSpaceCommand cmd = new RemoveNameSpaceCommand(getContext(), node, notifications);
		cmd.init();
		return cmd;
	}

	@Override
	public IEditCommand createChangeNameSpaceCommand(AvroNode node, NSNode targetNSNode, int notifications) {
		return new ChangeNameSpaceCommand(getContext(), node, targetNSNode, notifications);
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}		
	
}
