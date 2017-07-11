package org.talend.avro.schema.editor.viewer.attribute;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public interface AttributeChangeNotifier {

	void onAttributeChanged(AvroAttribute<?> attribute);
	
}
