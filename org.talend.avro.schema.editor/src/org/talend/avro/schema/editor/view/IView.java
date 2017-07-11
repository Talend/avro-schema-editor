package org.talend.avro.schema.editor.view;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NotificationObserver;

public interface IView extends NotificationObserver {
		
	String getId();
		
	void reveal(Object object, AvroContext.Kind context);
	
	void select(Object object, AvroContext.Kind context);
		
}
