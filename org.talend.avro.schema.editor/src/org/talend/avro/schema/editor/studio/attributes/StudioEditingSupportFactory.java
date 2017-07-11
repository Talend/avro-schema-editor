package org.talend.avro.schema.editor.studio.attributes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.viewer.attribute.column.EditingPolicy;
import org.talend.avro.schema.editor.viewer.attribute.column.EditingSupportFactory;
import org.talend.avro.schema.editor.viewer.attribute.column.SchemaViewerColumnEditingSupport;

public class StudioEditingSupportFactory extends EditingSupportFactory {

	public StudioEditingSupportFactory(AvroContext context, EditingPolicy editingPolicy) {
		super(context, editingPolicy);
	}

	public SchemaViewerColumnEditingSupport getEditingSupport(String attributeName, Class<?> attrValueClass) {
		if (StudioSchemaTypes.class.isAssignableFrom(attrValueClass)) {
			return new StudioSchemaTypesAttributeEditingSupport(attributeName, Notifications.NOT_REF);
		}
		return super.getEditingSupport(attributeName, attrValueClass);
	}
	
	public class StudioSchemaTypesAttributeEditingSupport extends AbstractAttributeEditingSupport<StudioSchemaTypes> {
		
		public StudioSchemaTypesAttributeEditingSupport(String attributeName, int notifications) {
			super(attributeName, StudioSchemaTypes.class, notifications);
		}

		@Override
		public CellEditor getCellEditor(AvroNode node, ColumnViewer viewer) {
			StudioSchemaTypes studioTypes = getAttributeValue(node);
			String[] valuesAsString = studioTypes.getValuesAsString();
			return new ComboBoxCellEditor((Composite) viewer.getControl(), valuesAsString);
		}

		@Override
		protected Object doGetValue(StudioSchemaTypes studioTypes) {
			StudioSchemaType type = studioTypes.getValue();
			return studioTypes.getIndexOf(type);
		}

		@Override
		public void setValue(AvroNode node, Object value) {
			Integer selectedTypeIndex = (Integer) value;
			StudioSchemaTypes studioTypes = getAttributeValue(node);
			StudioSchemaType type = studioTypes.getValue();
			int currentTypeIndex = studioTypes.getIndexOf(type);
			if (currentTypeIndex != selectedTypeIndex) {
				StudioSchemaType selectedType = studioTypes.getValueFor(selectedTypeIndex);
				StudioSchemaTypes newValue = studioTypes.getACopy();
				newValue.setValue(selectedType);
				changeAttribute(node, newValue);				
			}
		}		
		
	}
	
}
