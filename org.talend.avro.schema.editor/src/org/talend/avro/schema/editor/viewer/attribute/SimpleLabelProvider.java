package org.talend.avro.schema.editor.viewer.attribute;

import org.eclipse.jface.viewers.LabelProvider;

public class SimpleLabelProvider extends LabelProvider {

	private String label;

	public SimpleLabelProvider(String label) {
		super();
		this.label = label;
	}

	@Override
	public String getText(Object element) {
		return label;
	}
	
}
