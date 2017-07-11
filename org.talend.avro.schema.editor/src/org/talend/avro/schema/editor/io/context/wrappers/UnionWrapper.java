package org.talend.avro.schema.editor.io.context.wrappers;

import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
import org.apache.avro.SchemaBuilder.UnionAccumulator;

public class UnionWrapper {

	private UnionAccumulator<?> unionAccu;

	public UnionWrapper(Object object) {
		super();
		if (object instanceof UnionAccumulator<?>) {
			unionAccu = (UnionAccumulator<?>) object;
		}
	}
	
	public Object endUnion() {
		if (unionAccu == null) {
			throw new UnsupportedOperationException();
		}
		return unionAccu.endUnion();
	}	
	
	public BaseTypeBuilder<?> and() {
		if (unionAccu == null) {
			throw new UnsupportedOperationException();
		}
		return unionAccu.and();
	}
	
}
