package org.talend.avro.schema.editor.registry;

public class NameSpace {

	public static final String DOT = "."; //$NON-NLS-1$
	
	private NSNode fromNode;
	
	private NSNode toNode;

	public NameSpace(NSNode fromNode, NSNode toNode) {
		super();
		this.fromNode = fromNode;
		this.toNode = toNode;
	}

	public String toString() {
		if (fromNode == null && toNode == null) {
			return "";
		} else if (fromNode == null) {
			return toNode.toString();
		} else if (toNode == null) {
			return fromNode.toString();
		} else {
			return computePath(fromNode, toNode);
		}
	}
	
	protected String computePath(NSNode fromNode, NSNode toNode) {
		
		StringBuffer buffer = new StringBuffer();

		buffer.append(toNode.toString());
		
		NSNode parent = toNode.getParent();
		
		while (parent != fromNode) {
			buffer.insert(0, DOT);
			buffer.insert(0, parent.toString());
			parent = parent.getParent();
		}
		
		buffer.insert(0, DOT);
		buffer.insert(0, fromNode.toString());
		
		return buffer.toString();
		
	}
	
}
