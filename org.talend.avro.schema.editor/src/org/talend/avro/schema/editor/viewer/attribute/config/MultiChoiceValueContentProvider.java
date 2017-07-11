package org.talend.avro.schema.editor.viewer.attribute.config;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;

public interface MultiChoiceValueContentProvider<T> {

	T[] getContent(AvroAttribute<MultiChoiceValue<T>> attribute);
	
}
