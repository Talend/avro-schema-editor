package org.talend.avro.schema.editor.view;

import org.talend.avro.schema.editor.context.AvroContext;

public interface IContextualView extends IView {

	AvroContext getContext();
	
}
