package org.talend.avro.schema.editor.statistics;

import java.util.HashMap;
import java.util.Map;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class NodeStats {

	private TypeCounter typeCounter = new TypeCounter();

	private UnionStats unionStats = new UnionStats();
	
	private RefStats refStats = new RefStats();
	
	private String context;
	
	private long startTime = -1;
	
	private long finishTime = -1;
	
	private long duration = -1;
	
	private int deepestLevel = 0;
	
	public NodeStats(String context) {
		super();	
		this.context = context;
	}
	
	public String getContext() {
		return context;
	}

	public void start() {
		if (startTime != -1) {
			throw new IllegalStateException("Invalid node statistics state");
		}
		startTime = System.currentTimeMillis();
		typeCounter.start();		
		unionStats.start();
		refStats.start();
	}
	
	public void count(AvroNode node) {
		NodeType type = node.getType();
		typeCounter.count(type);
		switch (type) {
		case UNION:
			unionStats.count((UnionNode) node);
			break;
		case REF:
			refStats.count((RefNode) node);
			break;
		default:
			break;
		}
		// compute deepest level
		deepestLevel = Math.max(deepestLevel, computeLevel(node));
	}
	
	protected int computeLevel(AvroNode node) {
		int level = 1;
		AvroNode parent = node.getParent();
		while (parent != null) {
			parent = parent.getParent();
			level++;
		}
		return level;
	}
	
	public void finish() {
		unionStats.finish();
		typeCounter.finish();
		refStats.finish();
		if (startTime == -1) {
			throw new IllegalStateException("Invalid node statistics state");
		}
		finishTime = System.currentTimeMillis();
		duration = finishTime - startTime;		
	}
	
	public long getDuration() {
		return duration;
	}

	public int getTotal() {
		return typeCounter.getTotal();
	}

	public int getCount(NodeType type) {
		return typeCounter.getCount(type);
	}
		
	public UnionStats getUnionStats() {
		return unionStats;
	}

	public RefStats getRefStats() {
		return refStats;
	}
	
	public int getDeepestLevel() {
		return deepestLevel;
	}

	public static class UnionStats {
				
		private int choiceTypeCount = -1;

		public UnionStats() {
			super();
		}
		
		public void start() {
			choiceTypeCount = 0;
		}
		
		public void count(UnionNode unionNode) {
			if (AttributeUtil.isChoiceType(unionNode)) {
				choiceTypeCount++;
			}
		}
		
		public void finish() {
			//
		}

		public int getChoiceTypeCount() {
			return choiceTypeCount;
		}
		
	}
	
	public static class RefStats {
		
		private TypeCounter counter = new TypeCounter();

		public RefStats() {
			super();
		}
		
		public void start() {
			counter.start();
		}
		
		public void count(RefNode refNode) {
			NodeType referencedType = refNode.getReferencedType();
			counter.count(referencedType);
		}
		
		public void finish() {
			counter.finish();
		}
		
		public int getCount(NodeType type) {
			return counter.getCount(type);
		}
		
	}
	
	public static class TypeCounter {
		
		private int total = -1;
		
		private Map<NodeType, Integer> typeCounters = new HashMap<>();

		public TypeCounter() {
			super();
		}
		
		public void start() {
			total = 0;
			typeCounters.clear();
		}
		
		public void count(NodeType type) {
			Integer count = typeCounters.get(type);
			if (count == null) {
				typeCounters.put(type, 1);
			} else {
				typeCounters.put(type, count + 1);
			}
			total++;
		}
		
		public void finish() {
			//
		}

		public int getCount(NodeType type) {
			Integer count = typeCounters.get(type);
			if (count == null) {
				return 0;
			}
			return count;
		}
		
		public int getTotal() {
			return total;
		}
		
	}
	
}
