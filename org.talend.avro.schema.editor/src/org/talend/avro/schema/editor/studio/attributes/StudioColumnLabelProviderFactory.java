package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;
import org.talend.avro.schema.editor.viewer.attribute.column.ColumnLabelProviderFactory;

public class StudioColumnLabelProviderFactory extends ColumnLabelProviderFactory {

	public SchemaViewerLabelProvider getColumnLabelProvider(String attributeName, Class<?> attrValueClass) {
		if (StudioSchemaTypes.class.isAssignableFrom(attrValueClass)) {
			return new StudioSchemaTypesColumnLabelProvider(attributeName, "-");
		}
		if (StudioAttributes.DATE_FORMAT.equals(attributeName)) {
			return new StringColumnLabelProvider(attributeName, "");
		}
		return super.getColumnLabelProvider(attributeName, attrValueClass);
	}
	
	public class StudioSchemaTypesColumnLabelProvider extends EnableDisableColumnLabelProvider<StudioSchemaTypes> {

		public StudioSchemaTypesColumnLabelProvider(String attributeName, String disabledText) {
			super(attributeName, StudioSchemaTypes.class, disabledText);
		}

		@Override
		protected String getEnabledText(AvroNode node, AvroAttribute<StudioSchemaTypes> attribute, StudioSchemaTypes value) {
			StudioSchemaType type = value.getValue();
			return type.getLabel();
		}
		
	}
	
}
