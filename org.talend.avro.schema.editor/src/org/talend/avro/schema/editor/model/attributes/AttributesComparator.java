package org.talend.avro.schema.editor.model.attributes;

import java.util.Comparator;

public class AttributesComparator implements Comparator<AvroAttribute<?>> {

	private String[] sortedAttributes;
	
	public AttributesComparator(String[] sortedAttributes) {
		super();
		this.sortedAttributes = sortedAttributes;
	}

	@Override
	public int compare(AvroAttribute<?> attr1, AvroAttribute<?> attr2) {
		String name1 = attr1.getName();
		String name2 = attr2.getName();
		int i1 = indexOf(name1);
		int i2 = indexOf(name2);
		return i1 - i2;
	}

	protected int indexOf(String name) {
		for (int i = 0; i < sortedAttributes.length; i++) {
			if (sortedAttributes[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
}
