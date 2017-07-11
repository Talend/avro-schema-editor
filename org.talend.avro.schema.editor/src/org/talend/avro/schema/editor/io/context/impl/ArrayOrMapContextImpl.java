package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.avro.SchemaBuilder.MapBuilder;
import org.apache.avro.SchemaBuilder.TypeBuilder;
import org.talend.avro.schema.editor.io.context.ArrayContext;
import org.talend.avro.schema.editor.io.context.MapContext;
import org.talend.avro.schema.editor.io.context.TypeStartContext;

public class ArrayOrMapContextImpl implements ArrayContext, MapContext {

	private ArrayBuilder<?> arrayBuilder;
	
	private MapBuilder<?> mapBuilder;

	public ArrayOrMapContextImpl(MapBuilder<?> mapBuilder) {
		super();
		this.mapBuilder = mapBuilder;
	}
	
	public ArrayOrMapContextImpl(ArrayBuilder<?> arrayBuilder) {
		super();
		this.arrayBuilder = arrayBuilder;
	}

	@Override
	public TypeStartContext type() {
		TypeBuilder<?> typeBuilder = null;
		if (arrayBuilder != null) {
			typeBuilder = arrayBuilder.items();
		} else if (mapBuilder != null) {
			typeBuilder = mapBuilder.values();
		}
		else {
			throw new UnsupportedOperationException();
		}
		return new TypeStartContextImpl(typeBuilder);
	}

}
