package org.talend.avro.schema.editor.studio.attributes;

import org.eclipse.swt.SWT;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;
import org.talend.avro.schema.editor.viewer.attribute.column.AbstractAttributeColumnConfigurationImpl;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfig;
import org.talend.avro.schema.editor.viewer.attribute.column.DefaultEditingPolicyImpl;
import org.talend.avro.schema.editor.viewer.attribute.column.SchemaViewerColumnEditingSupport;

public class StudioAttributeColumnConfigurationImpl extends AbstractAttributeColumnConfigurationImpl {

	private static final AttributeColumnConfig[] ATTRIBUTE_COLUMN_CONFIGS = new AttributeColumnConfig[] {				
			config(StudioAttributes.NAME, "Column", true, 300, true, SWT.LEFT),
			config(StudioAttributes.KEY, "key", 50, true, SWT.CENTER),
			config(StudioAttributes.TYPE, "type", 100, true, SWT.CENTER),
			config(StudioAttributes.NULLABLE, "Nullable", 100, true, SWT.CENTER),
			config(StudioAttributes.DATE_FORMAT, "Date Format", 100, true, SWT.CENTER),
			config(StudioAttributes.DEFAULT, "Default", 100, true, SWT.CENTER),
			config(StudioAttributes.DOC, "Comment", 100, true, SWT.CENTER)
		};
	
	private StudioColumnLabelProviderFactory columnLabelProviderFactory;
	
	private StudioEditingSupportFactory editingSupportFactory;
	
	public StudioAttributeColumnConfigurationImpl(AvroContext context) {
		super(DEFAULT_COLUMN_WIDTH, DEFAULT_COLUMN_RESIZABLE, DEFAULT_COLUMN_STYLE);
		this.columnLabelProviderFactory = new StudioColumnLabelProviderFactory();
		this.editingSupportFactory = new StudioEditingSupportFactory(context, new DefaultEditingPolicyImpl());
	}

	@Override
	protected AttributeColumnConfig[] getConfigs() {
		return ATTRIBUTE_COLUMN_CONFIGS;
	}

	@Override
	public SchemaViewerLabelProvider getColumnLabelProvider(String attributeName) {
		Class<?> attrValueClass = StudioAttributes.getAttributeValueClass(attributeName);
		return columnLabelProviderFactory.getColumnLabelProvider(attributeName, attrValueClass);
	}

	@Override
	public SchemaViewerColumnEditingSupport getColumnEditingSupport(String attributeName) {
		Class<?> attrValueClass = AvroAttributes.getAttributeValueClass(attributeName);
		return editingSupportFactory.getEditingSupport(attributeName, attrValueClass);
	}

}
