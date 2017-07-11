package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.path.PathService;

public class PathAttribute extends AvroAttributeImpl<String> {
		
	private PathService pathService;
	
	public PathAttribute(AvroNode node, PathService pathService) {
		super(node, AvroAttributes.PATH, AvroAttributes.PATH_CLASS, null);
		this.pathService = pathService;
	}
	
	@Override
	public String getValue() {
		return pathService.getPath(getHolder());
	}

	public String getValue(AvroContext context) {
		return pathService.getPath(getHolder(), context);
	}
	
	@Override
	public void setValue(String value) {
		throw new UnsupportedOperationException("Cannot change the path attribute value");
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && pathService.hasPath(getHolder());		
	}

	@Override
	public String getCopyOfValue() {
		// is this make sense?
		return getValue();
	}		
	
}
