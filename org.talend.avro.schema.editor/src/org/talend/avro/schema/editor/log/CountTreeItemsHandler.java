package org.talend.avro.schema.editor.log;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.statistics.NodeStats;
import org.talend.avro.schema.editor.utils.UIUtils;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class CountTreeItemsHandler extends AbstractLogViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.log.computeStats"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		AvroSchemaEditor editor = UIUtils.pickAvroSchemaEditorFromEditorParts();
		
		if (editor != null) {
			
			computeAndDisplayStats(editor);
			
		}
		
		return null;
	}

	protected void computeAndDisplayStats(AvroSchemaEditor editor) {
		
		SchemaViewer masterViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
		TreeViewer treeViewer = masterViewer.getTreeViewer();
		AvroNode inputNode = (AvroNode) treeViewer.getInput();
		NodeType inputType = inputNode.getType();
		String name = AttributeUtil.getNameFromAttribute(inputNode);
		
		NodeStats stats = new NodeStats("Compute stats of " + inputType.toString().toLowerCase() 
				+ " " + name + " from editor " + editor.getName());
		stats.start();
		
		Tree tree = treeViewer.getTree();
		
		TreeItem[] items = tree.getItems();
		for (TreeItem item : items) {
			visitTreeItem(item, stats);
		}
		
		stats.finish();
		AvroSchemaLogger.logStats(stats, false);
		
	}
	
	protected void visitTreeItem(TreeItem treeItem, NodeStats stats) {		
		SchemaNode node = (SchemaNode) treeItem.getData();
		if (node != null) {
			stats.count(node.getAvroNode());
			TreeItem[] items = treeItem.getItems();
			for (TreeItem item : items) {
				visitTreeItem(item, stats);
			}
		}
	}
	
}
