package org.talend.avro.schema.editor.viewer.attribute.column;

import org.eclipse.swt.SWT;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;

public class AttributeColumnConfigurationImpl extends AbstractAttributeColumnConfigurationImpl {	
	
	private static final AttributeColumnConfig[] ATTRIBUTE_COLUMN_CONFIGS = new AttributeColumnConfig[] {				
		config(AvroAttributes.NAME, true, 200, true, SWT.LEFT),
		config(AvroAttributes.NAME_SPACE, 200, true, SWT.CENTER),
		config(AvroAttributes.DOC, 200, true, SWT.CENTER), 
		config(AvroAttributes.OPTIONAL, 100, false, SWT.CENTER), 
		config(AvroAttributes.PRIMITIVE_TYPE, 100, false, SWT.CENTER), 		
		//config(AvroAttributes.DEFAULT, 50, true),
		//config(AvroAttributes.ARRAY_OR_MAP, 30, false),
		config(AvroAttributes.SYMBOLS, 100, true, SWT.CENTER), 
		config(AvroAttributes.ALIASES, 100, true, SWT.CENTER), 
		config(AvroAttributes.SIZE, 50, false, SWT.CENTER)				
	};
	
	private ColumnLabelProviderFactory columnLabelProviderFactory;
	
	private EditingSupportFactory editingSupportFactory;
	
	public AttributeColumnConfigurationImpl(AvroContext context) {
		super(DEFAULT_COLUMN_WIDTH, DEFAULT_COLUMN_RESIZABLE, DEFAULT_COLUMN_STYLE);
		this.columnLabelProviderFactory = new ColumnLabelProviderFactory();
		this.editingSupportFactory = new EditingSupportFactory(context, new DefaultEditingPolicyImpl());
	}
	
	@Override
	protected AttributeColumnConfig[] getConfigs() {
		return ATTRIBUTE_COLUMN_CONFIGS;
	}
		
	@Override
	public SchemaViewerLabelProvider getColumnLabelProvider(String attributeName) {
		Class<?> attrValueClass = AvroAttributes.getAttributeValueClass(attributeName);
		return columnLabelProviderFactory.getColumnLabelProvider(attributeName, attrValueClass);
	}

	@Override
	public SchemaViewerColumnEditingSupport getColumnEditingSupport(String attributeName) {
		Class<?> attrValueClass = AvroAttributes.getAttributeValueClass(attributeName);
		return editingSupportFactory.getEditingSupport(attributeName, attrValueClass);
	}	

}
