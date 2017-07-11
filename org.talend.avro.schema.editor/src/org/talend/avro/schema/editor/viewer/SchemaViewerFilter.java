package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SchemaViewerFilter extends ViewerFilter {

	private SchemaViewerNodeConverter nodeConverter;
	
	private ViewerFilter filter;
	
	public SchemaViewerFilter(ViewerFilter filter, SchemaViewerNodeConverter nodeConverter) {
		super();
		this.filter = filter;
		this.nodeConverter = nodeConverter;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return filter.select(viewer, nodeConverter.convertToAvroNode(parentElement), nodeConverter.convertToAvroNode(element));
	}
	
}
