package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.talend.avro.schema.editor.model.SchemaNode;

/**
 * Tree content provider implementation based on a schema content
 * 
 * @author timbault
 *
 */
public class SchemaViewerTreeContentProviderImpl implements ITreeContentProvider {
		
	private SchemaViewerNodeConverter nodeConverter;
	
	public SchemaViewerTreeContentProviderImpl(SchemaViewerNodeConverter nodeConverter) {
		super();
		this.nodeConverter = nodeConverter;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		SchemaNode schemaNode = nodeConverter.convertToSchemaNode(inputElement);
		return schemaNode.getChildren().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		SchemaNode schemaNode = nodeConverter.convertToSchemaNode(parentElement);
		return schemaNode.getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		SchemaNode schemaNode = nodeConverter.convertToSchemaNode(element);
		return schemaNode.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		SchemaNode schemaNode = nodeConverter.convertToSchemaNode(element);
		return schemaNode.hasChildren();
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}
	
}
