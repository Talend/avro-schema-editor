package org.talend.avro.schema.editor.studio.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.cmd.RemoveElementCommand;

public class StudioRemoveElementCommand extends RemoveElementCommand {

	public StudioRemoveElementCommand(AvroContext context, AvroNode node, int notifications) {
		super(context, node, notifications);
	}

	@Override
	public void run() {
		prepareParameters();
		AvroNode removedNode = getRemovedNode();
		AvroNode parentNode = removedNode.getParent();
		int index = parentNode.getChildIndex(removedNode);
		getController().removeElement(removedNode);
		doNotify(parentNode);
		doRefresh(parentNode);
		if (parentNode.hasChildren()) {
			int childrenCount = parentNode.getChildrenCount();
			if (index >= childrenCount) {
				index--;
			}
			AvroNode child = parentNode.getChild(index);
			doSelect(child);
		}
	}

}
