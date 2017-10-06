package org.talend.avro.schema.editor.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.dialogs.SearchPattern;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.DefaultAvroNodeVisitor;
import org.talend.avro.schema.editor.model.EnumNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * 
 * @author timbault
 *
 */
public class SearchNodeContext {
	
	private List<AvroNode> foundNodes = new ArrayList<>();
	
	private int index;

	private SchemaRegistry schemaRegistry;
	
	private AvroContext context;
	
	public enum SearchType {
		RECORD("Record", NodeType.RECORD),
		ENUM("Enum", NodeType.ENUM),
		FIXED("Fixed", NodeType.FIXED),
		FIELD("Field", NodeType.FIELD),
		DOC("Doc", null);
		
		private String label;

		private NodeType type;
		
		private SearchType(String label, NodeType type) {
			this.label = label;
			this.type = type;
		}
		
		public String getLabel() {
			return label;
		}

		public NodeType getType() {
			return type;
		}		
		
		public static SearchType getType(String valueStr) {
			for (SearchType type : values()) {
				if (type.toString().toLowerCase().equals(valueStr.toLowerCase())) {
					return type;
				}
			}
			return null;
		}
		
	}
	
	public SearchNodeContext(SchemaRegistry schemaRegistry, AvroContext context) {
		super();
		this.schemaRegistry = schemaRegistry;
		this.context = context;
	}

	protected List<AvroNode> findNodes(NodeType type, String pattern, boolean withRef) {
		return schemaRegistry.getNodeAndRefNodesFromFullName(type, pattern, withRef);
	}
	
	protected List<AvroNode> findFields(String pattern) {
		SearchFieldsVisitor visitor = new SearchFieldsVisitor(pattern);
		AvroNode inputNode = context.getInputNode();
		inputNode.visitNode(visitor);
		return visitor.getFields();
	}
	
	protected List<AvroNode> findDoc(String pattern) {
		SearchDocVisitor visitor = new SearchDocVisitor(pattern);
		AvroNode inputNode = context.getInputNode();
		inputNode.visitNode(visitor);
		return visitor.getNodes();
	}
	
	public boolean searchNodes(SearchType type, String pattern, boolean withRef) {
		reset();
		List<AvroNode> foundNodes = null;
		switch (type) {
		case RECORD:
		case ENUM:
		case FIXED:
			foundNodes = findNodes(type.getType(), pattern, withRef);
			break;
		case FIELD:
			foundNodes = findFields(pattern);
			break;
		case DOC:
			foundNodes = findDoc(pattern);
			break;
		}		
		if (foundNodes != null && !foundNodes.isEmpty()) {
			registerFoundNodes(foundNodes);
			return true;
		}
		return false;
	}
	
	protected void registerFoundNodes(List<AvroNode> nodes) {
		index = -1;
		foundNodes.clear();
		foundNodes.addAll(nodes);
	}
	
	public boolean hasNext() {
		return !foundNodes.isEmpty() && index < foundNodes.size() - 1;
	}
	
	public AvroNode next() {		
		if (!foundNodes.isEmpty() && index < foundNodes.size() - 1) {
			index++;
			return foundNodes.get(index);
		}
		return null;
	}
	
	public boolean hasPrevious() {
		return !foundNodes.isEmpty() && index > 0;
	}
	
	public AvroNode previous() {
		if (!foundNodes.isEmpty() && index > 0) {
			index--;
			return foundNodes.get(index);
		}
		return null;
	}
	
	public void reset() {
		foundNodes.clear();
		index = -1;
	}
	
	protected boolean isTextMatchingSearchPattern(String text, String searchPattern) {
        SearchPattern matcher = new SearchPattern(SearchPattern.RULE_PATTERN_MATCH
                | SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_PREFIX_MATCH
                | SearchPattern.RULE_BLANK_MATCH);
        matcher.setPattern("*" + searchPattern);
        return matcher.matches(text);
    }
	
	public void dispose() {
		foundNodes.clear();
	}
	
	private class SearchFieldsVisitor extends DefaultAvroNodeVisitor {
		
		private String pattern;
		
		private List<AvroNode> fields = new ArrayList<>();
		
		public SearchFieldsVisitor(String pattern) {
			super();
			this.pattern = pattern;
		}

		public List<AvroNode> getFields() {
			return fields;
		}

		@Override
		public AvroNode enterFieldNode(FieldNode fieldNode) {
			String fieldName = AttributeUtil.getNameFromAttribute(fieldNode);
			if (isTextMatchingSearchPattern(fieldName, pattern)) {
				fields.add(fieldNode);
			}
			return super.enterFieldNode(fieldNode);
		}		
		
	}
	
	private class SearchDocVisitor extends DefaultAvroNodeVisitor {
		
		private String pattern;
		
		private List<AvroNode> nodes = new ArrayList<>();
		
		public SearchDocVisitor(String pattern) {
			super();
			this.pattern = pattern;
		}

		public List<AvroNode> getNodes() {
			return nodes;
		}

		protected void checkDoc(AvroNode node) {
			String doc = AttributeUtil.getDoc(node);
			if (isTextMatchingSearchPattern(doc, pattern)) {
				nodes.add(node);
			}
		}
		
		@Override
		public AvroNode enterFieldNode(FieldNode fieldNode) {
			checkDoc(fieldNode);
			return super.enterFieldNode(fieldNode);
		}

		@Override
		public AvroNode enterRecordNode(RecordNode recordNode) {
			checkDoc(recordNode);
			return super.enterRecordNode(recordNode);
		}

		@Override
		public AvroNode enterEnumNode(EnumNode enumNode) {
			checkDoc(enumNode);
			return super.enterEnumNode(enumNode);
		}		
		
	}
	
}
