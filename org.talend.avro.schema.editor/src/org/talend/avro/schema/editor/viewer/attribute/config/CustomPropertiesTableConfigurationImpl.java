package org.talend.avro.schema.editor.viewer.attribute.config;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.CustomProperties.KeyValue;

/**
 * Implementation of a {@link TableConfiguration} used by the custom properties attribute UI.
 * 
 * @author timbault
 *
 */
public class CustomPropertiesTableConfigurationImpl extends DefaultTableConfigurationImpl {	
	
	@Override
	public CellLabelProvider getCellLabelProvider(String columnKey) {
		return new KeyValueCellLabelProvider(columnKey);
	}

	@Override
	public ViewerComparator getComparator() {
		return new KeyComparator();
	}
	
	private static class KeyComparator extends ViewerComparator {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			KeyValue kv1 = (KeyValue) e1;
			KeyValue kv2 = (KeyValue) e2;
			return super.compare(viewer, kv1.getKey(), kv2.getKey());
		}		
		
	}
	
	private static class KeyValueCellLabelProvider extends CellLabelProvider {

		private String column;
		
		public KeyValueCellLabelProvider(String column) {
			super();
			this.column = column;
		}

		@Override
		public void update(ViewerCell cell) {
			KeyValue keyValue = (KeyValue) cell.getElement();
			switch (column) {
			case CustomProperties.KEY:
				cell.setText(keyValue.getKey());
				break;
			case CustomProperties.VALUE:
				cell.setText(keyValue.getValue().toString());
				break;
			}
		}
		
	}
	
}
