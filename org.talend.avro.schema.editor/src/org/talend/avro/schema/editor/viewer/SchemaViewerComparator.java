package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class SchemaViewerComparator extends ViewerComparator {

	private SchemaViewerNodeConverter nodeConverter;
	
	private ViewerComparator comparator;

	public SchemaViewerComparator(ViewerComparator comparator, SchemaViewerNodeConverter nodeConverter) {
		super();
		this.comparator = comparator;
		this.nodeConverter = nodeConverter;
	}

	@Override
	public int category(Object element) {
		return comparator.category(nodeConverter.convertToAvroNode(element));
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return comparator.compare(viewer, nodeConverter.convertToAvroNode(e1), nodeConverter.convertToAvroNode(e2));
	}
	
}
