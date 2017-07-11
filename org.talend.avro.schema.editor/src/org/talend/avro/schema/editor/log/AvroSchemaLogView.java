package org.talend.avro.schema.editor.log;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.statistics.NodeStats;
import org.talend.avro.schema.editor.statistics.NodeStats.RefStats;
import org.talend.avro.schema.editor.statistics.NodeStats.UnionStats;

/**
 * Eclipse view part displaying some statistics about avro schema opened in active editor.
 * 
 * @author timbault
 *
 */
public class AvroSchemaLogView extends ViewPart {

	public static final String ID = "org.talend.avro.schema.editor.log.view"; //$NON-NLS-1$
	
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$
	
	private Label label;
	
	private Text text;
	
	private boolean active = false;	
	
	@Override
	public void createPartControl(Composite parent) {		
		
		Composite compo = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		
		compo.setLayout(layout);
		
		label = new Label(compo, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		text = new Text(compo, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		update();
	}	
	
	protected void update() {
		label.setText("Active status: " + Boolean.toString(active).toUpperCase());		
	}
	
	protected void newLine() {
		text.append(NEW_LINE);
	}
	
	public void logMsg(String msg) {
		if (isActive()) {
			text.append(msg);
			newLine();
		}
	}
	
	public void logStats(NodeStats stats) {
		if (isActive()) {
			UnionStats unionStats = stats.getUnionStats();
			text.append("*********************");
			newLine();
			text.append(stats.getContext());
			newLine();
			text.append("*********************");
			newLine();	
			for (NodeType type : NodeType.values()) {
				text.append(type.toString() + ": " + stats.getCount(type));
				switch (type) {
				case UNION:
					text.append(" (choices: " + unionStats.getChoiceTypeCount() + ")");
					break;
				default:					
					break;
				}				
				newLine();
			}
			RefStats refStats = stats.getRefStats();
			text.append("*** REF stats:");
			newLine();
			for (NodeType type : NodeType.REFERENCED_NODE_TYPES) {
				text.append(type.toString() + ": " + refStats.getCount(type));
				newLine();
			}
			text.append("------------------");
			newLine();
			text.append("Deepest level = " + stats.getDeepestLevel());
			newLine();
			text.append("------------------");
			newLine();
			text.append("TOTAL = " + stats.getTotal());
			newLine();
			text.append("Duration = " + stats.getDuration() + " ms");
			newLine();			
		}
	}
	
	public void logNameSpaceTree(NSNode nameSpaceTree) {
		if (isActive()) {
			logNSTree(nameSpaceTree, 0);
		}
	}
	
	protected String expand(int level) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			buffer.append("      ");
		}
		return buffer.toString();
	}
	
	protected void logNSTree(NSNode node, int level) {
		String expand = expand(level);
		int childLevel = level + 1;
		for (NSNode child : node.getChildren()) {			
			text.append(expand);
			text.append(child.getName());
			newLine();
			logNSTree(child, childLevel);
		}
	}
	
	public void clear() {
		text.setText("");
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;	
		update();
	}

	@Override
	public void setFocus() {
		text.setFocus();		
	}

}
