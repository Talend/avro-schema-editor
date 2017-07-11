package org.talend.avro.schema.editor.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.talend.avro.schema.editor.model.AvroNode;

public class CopyContext {

	private ListenerList listeners = new ListenerList();
	
	private List<AvroNode> nodes = new ArrayList<>();
	
	public void addListener(CopyContextListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(CopyContextListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyListenersOnContextUpdated() {
		for (Object listener : listeners.getListeners()) {
			((CopyContextListener) listener).onCopyContextUpdated(this);
		}
	}
	
	public void setNodesToCopy(List<AvroNode> nodes, boolean append) {
		if (!append) {
			this.nodes.clear();
		}
		for (AvroNode node : nodes) {
			if (!this.nodes.contains(node)) {
				this.nodes.add(node);
			}
		}
		notifyListenersOnContextUpdated();
	}
	
	public void clearNodesToCopy() {
		nodes.clear();
		notifyListenersOnContextUpdated();
	}
	
	public boolean areTheyNodesToCopy() {
		return !nodes.isEmpty();
	}
	
	public List<AvroNode> getNodesToCopy() {
		return Collections.unmodifiableList(nodes);
	}
	
}
