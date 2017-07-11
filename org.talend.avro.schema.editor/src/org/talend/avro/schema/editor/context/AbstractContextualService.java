package org.talend.avro.schema.editor.context;

import org.talend.avro.schema.editor.context.services.IContextualService;

/**
 * Base abstract implementation of a contextual service.
 * 
 * @author timbault
 *
 */
public abstract class AbstractContextualService implements IContextualService {

	private AvroContext context;

	@Override
	public void init(AvroContext context) {
		this.context = context;
	}

	protected AvroContext getContext() {
		return context;
	}
	
}
