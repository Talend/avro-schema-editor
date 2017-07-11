package org.talend.avro.schema.editor.model.path;

import java.util.ArrayList;
import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.AbstractEditorService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class AvroSchemaPathService extends AbstractEditorService implements PathService {

	private static final String STEP_SEP = "/"; //$NON-NLS-1$
	
	private static final String NAME_SEP = ":"; //$NON-NLS-1$
	
	private static final String ANCHOR = "#"; //$NON-NLS-1$
	
	private static final String POSITION = "@"; //$NON-NLS-1$	
	
	@Override
	public boolean hasPath(AvroNode node) {
		NodeType type = node.getType();
		switch (type) {
		case RECORD:
		case FIELD:
		case ENUM:
		case REF:
		case PRIMITIVE_TYPE:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String getPath(AvroNode node) {
		return new Path(node).toString();
	}
	
	@Override
	public String getPath(AvroNode node, AvroContext context) {
		SchemaNode schemaNode = getSchemaNode(node, context);	
		if (schemaNode == null) {
			return new Path(node).toString();
		} else {
			return new Path(schemaNode).toString();
		}
	}

	@Override
	public AvroNode getNode(String path) {
		// TODO
		return null;
	}
	
	@Override
	public AvroNode getNode(String path, AvroContext context) {
		// TODO
		return null;
	}

	protected SchemaNode getSchemaNode(AvroNode node, AvroContext context) {
		List<SchemaNode> schemaNodes = context.getSchemaNodes();
		for (SchemaNode schemaNode : schemaNodes) {
			if (schemaNode.getAvroNode() == node) {
				return schemaNode;
			}
		}
		return null;
	}
	
	@Override
	public void dispose() {
		// nothing to dispose
	}
	
	private static class Path {

		private boolean useSchemaNodes;
		
		private List<AvroNode> avroNodes = new ArrayList<>();
		
		private List<SchemaNode> schemaNodes = new ArrayList<>();

		public Path(AvroNode node) {
			super();
			this.useSchemaNodes = false;
			populateAvroNodes(node);
		}
		
		public Path(SchemaNode node) {
			super();
			this.useSchemaNodes = true;
			populateSchemaNodes(node);
		}				

		private void populateAvroNodes(AvroNode node) {
			avroNodes.add(0, node);
			while (node.hasParent()) {
				node = node.getParent();
				avroNodes.add(0, node);
			}
		}
		
		private void populateSchemaNodes(SchemaNode node) {
			schemaNodes.add(0, node);
			while (node.hasParent()) {
				node = node.getParent();
				schemaNodes.add(0, node);
			}
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			String[] enclosingNameSpace = new String[1];
			if (useSchemaNodes) {
				for (SchemaNode schemaNode : schemaNodes) {
					AvroNode node = schemaNode.getAvroNode();
					append(buffer, node, enclosingNameSpace);
				}
			} else {
				for (AvroNode node : avroNodes) {
					append(buffer, node, enclosingNameSpace);
				}
			}
			return buffer.toString();
		}
		
		protected void append(StringBuffer buffer, AvroNode node, String[] enclosingNameSpace) {
			NodeType type = node.getType();
			switch (type) {
			case ROOT:
				// the root!
				break;
			case RECORD:
			case ENUM:
			case FIXED:
			case REF:
			case PRIMITIVE_TYPE:
				buffer.append(STEP_SEP);
				buffer.append(getTypeName(node, enclosingNameSpace));
				break;
			case FIELD:
				buffer.append(ANCHOR);
				buffer.append(AttributeUtil.getNameFromAttribute(node));
				break;				
			default:
				break;
			}
		}
		
		protected String getTypeName(AvroNode node, String[] enclosingNS) {
			StringBuffer buffer = new StringBuffer();
			NodeType nodeType = node.getType();
			if (nodeType == NodeType.PRIMITIVE_TYPE) {
				return AttributeUtil.getNameFromAttribute(node);
			} else if (nodeType.hasNameSpace()) {
				String ns = AttributeUtil.getTrueNameSpace(node);
				if (ns != null && !ns.trim().isEmpty()) {
					String nameSpace = ns.trim();
					// check if it is the enclosing name space
					if (enclosingNS[0] == null || !enclosingNS[0].trim().equals(nameSpace)) {
						buffer.append(nameSpace);
						buffer.append(NAME_SEP);
						enclosingNS[0] = nameSpace;
					}
				}
				String name = AttributeUtil.getNameFromAttribute(node);
				buffer.append(name);
				return buffer.toString();
			}
			return "";
		}
		
	}

}
