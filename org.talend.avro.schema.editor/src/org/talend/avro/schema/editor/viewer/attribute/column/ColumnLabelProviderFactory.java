package org.talend.avro.schema.editor.viewer.attribute.column;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;

/**
 * Factory responsible for creation of the column label providers.
 * 
 * @author timbault
 *
 */
public class ColumnLabelProviderFactory {

	protected static final String DEFAULT_DISABLED_TEXT = ""; 
	
	protected static final Color DEFAULT_DISABLED_BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_GRAY); 
	
	private String defaultDisabledText;
	
	private Color defaultDisabledBackgroundColor;
	
	public ColumnLabelProviderFactory() {
		this(DEFAULT_DISABLED_TEXT, DEFAULT_DISABLED_BACKGROUND_COLOR);
	}
	
	public ColumnLabelProviderFactory(String defaultDisabledText) {
		this(defaultDisabledText, DEFAULT_DISABLED_BACKGROUND_COLOR);
	}
	
	public ColumnLabelProviderFactory(String defaultDisabledText, Color defaultDisabledBackgroundColor) {
		super();
		this.defaultDisabledText = defaultDisabledText;
		this.defaultDisabledBackgroundColor = defaultDisabledBackgroundColor;
	}

	public SchemaViewerLabelProvider getColumnLabelProvider(String attributeName, Class<?> attrValueClass) {
		if (String.class.isAssignableFrom(attrValueClass)) {
			return new StringColumnLabelProvider(attributeName);
		} else if (Boolean.class.isAssignableFrom(attrValueClass)) {
			return new BooleanColumnLabelProvider(attributeName);
		} else if (PrimitiveTypes.class.isAssignableFrom(attrValueClass)) {
			return new PrimitiveTypesColumnLabelProvider(attributeName, "-");
		} else if (StringList.class.isAssignableFrom(attrValueClass)) {
			return new StringListColumnLabelProvider(attributeName);
		} else if (Integer.class.isAssignableFrom(attrValueClass)) {
			return new IntegerColumnLabelProvider(attributeName);
		}
		return null;
	}
	
	protected abstract class AbstractColumnLabelProvider<T> implements SchemaViewerLabelProvider {
		
		private String attributeName;
		
		private Class<T> attributeValueClass;

		protected AbstractColumnLabelProvider(String attributeName, Class<T> attributeValueClass) {
			super();
			this.attributeName = attributeName;
			this.attributeValueClass = attributeValueClass;
		}

		@SuppressWarnings("unchecked")
		protected AvroAttribute<T> getAttribute(AvroNode node) {
			return (AvroAttribute<T>) node.getAttributes().getAttribute(attributeName);
		}
		
		protected T getValue(AvroNode node) {
			return node.getAttributes().getAttributeValue(attributeName, attributeValueClass);
		}
		
		@Override
		public String getText(AvroNode node) {
			return getText(node, getAttribute(node), getValue(node));
		}

		protected String getText(AvroNode node, AvroAttribute<T> attribute, T value) {
			return null;
		}
		
		@Override
		public String getToolTipText(AvroNode node) {
			return getToolTipText(node, getAttribute(node), getValue(node));
		}

		protected String getToolTipText(AvroNode node, AvroAttribute<T> attribute, T value) {
			return null;
		}
		
		@Override
		public Image getImage(AvroNode node) {
			return getImage(node, getAttribute(node), getValue(node));
		}

		protected Image getImage(AvroNode node, AvroAttribute<T> attribute, T value) {
			return null;
		}
		
		@Override
		public StyleRange[] getStyleRanges(AvroNode node) {
			return getStyleRanges(node, getAttribute(node), getValue(node));
		}
		
		protected StyleRange[] getStyleRanges(AvroNode node, AvroAttribute<T> attribute, T value) {
			return new StyleRange[0];
		}

		@Override
		public Color getBackgroundColor(AvroNode node) {
			return getBackgroundColor(node, getAttribute(node), getValue(node));
		}

		protected Color getBackgroundColor(AvroNode node, AvroAttribute<T> attribute, T value) {
			return null;
		}

		@Override
		public void dispose() {
			// nothing to dispose
		}
		
	}
	
	protected abstract class EnableDisableColumnLabelProvider<T> extends AbstractColumnLabelProvider<T> {

		private String disabledText;
		
		private Color disabledColor;
		
		protected EnableDisableColumnLabelProvider(String attributeName, Class<T> attributeValueClass) {
			this(attributeName, attributeValueClass, defaultDisabledText, defaultDisabledBackgroundColor);
		}
		
		protected EnableDisableColumnLabelProvider(String attributeName, Class<T> attributeValueClass, String disabledText) {
			this(attributeName, attributeValueClass, disabledText, defaultDisabledBackgroundColor);
		}
		
		protected EnableDisableColumnLabelProvider(String attributeName, Class<T> attributeValueClass, String disabledText, Color disabledColor) {
			super(attributeName, attributeValueClass);
			this.disabledText = disabledText;
			this.disabledColor = disabledColor;
		}
		
		protected String getText(AvroNode node, AvroAttribute<T> attribute, T value) {
			if (attribute.isEnabled()) {
				return getEnabledText(node, attribute, value);
			} else {
				return getDisabledText(node, attribute, value);
			}
		}
		
		protected String getEnabledText(AvroNode node, AvroAttribute<T> attribute, T value) {
			return null;
		}
		
		protected String getDisabledText(AvroNode node, AvroAttribute<T> attribute, T value) {
			return disabledText;
		}
		
		protected Color getBackgroundColor(AvroNode node, AvroAttribute<T> attribute, T value) {
			if (attribute.isEnabled()) {
				return getEnabledBackgroundColor(node, attribute, value);
			} else {
				return getDisabledBackgroundColor(node, attribute, value);
			}
		}
		
		protected Color getEnabledBackgroundColor(AvroNode node, AvroAttribute<T> attribute, T value) {
			return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		}
		
		protected Color getDisabledBackgroundColor(AvroNode node, AvroAttribute<T> attribute, T value) {
			return disabledColor;
		}
		
	}
	
//	protected abstract class AbstractCellLabelProvider<T> extends CellLabelProvider {
//
//		private String attributeName;
//		
//		private Class<T> attributeValueClass;
//
//		private IEnableCellUpdater disabledCellUpdater;
//				
//		protected AbstractCellLabelProvider(String attributeName, Class<T> attributeValueClass, IEnableCellUpdater disabledCellUpdater) {
//			super();
//			this.attributeName = attributeName;
//			this.attributeValueClass = attributeValueClass;
//			this.disabledCellUpdater = disabledCellUpdater;
//		}
//
//		@Override
//		public void update(ViewerCell cell) {
//			AvroNode node = (AvroNode) cell.getElement();
//			AvroAttribute<?> attribute = node.getAttributes().getAttribute(attributeName);
//			boolean update = true;
//			if (disabledCellUpdater != null) {
//				if (attribute.isEnabled()) {
//					update = disabledCellUpdater.updateEnabledCell(cell, attribute);
//				} else {
//					update = disabledCellUpdater.updateDisabledCell(cell, attribute);
//				}
//			}
//			if (update) {
//				T attrValue = node.getAttributes().getAttributeValue(attributeName, attributeValueClass);
//				update(cell, (AvroAttribute<T>) attribute, attrValue);
//			}
//		}		
//		
//		protected abstract void update(ViewerCell cell, AvroAttribute<T> attribute, T value);
//		
//	}
	
	public class StringColumnLabelProvider extends EnableDisableColumnLabelProvider<String> {		

		public StringColumnLabelProvider(String attributeName) {
			this(attributeName, defaultDisabledText);
		}
		
		public StringColumnLabelProvider(String attributeName, String disabledText) {
			super(attributeName, String.class, disabledText);
		}

		@Override
		protected String getEnabledText(AvroNode node, AvroAttribute<String> attribute, String value) {
			return value;
		}
		
	}
	
//	public class StringCellLabelProvider extends AbstractCellLabelProvider<String> {
//
//		public StringCellLabelProvider(String attributeName, IEnableCellUpdater disabledCellUpdater) {
//			super(attributeName, String.class, disabledCellUpdater);
//		}
//
//		@Override
//		protected void update(ViewerCell cell, AvroAttribute<String> attribute, String value) {
//			cell.setText(value);
//		}
//		
//	}
	
	public class BooleanColumnLabelProvider extends AbstractColumnLabelProvider<Boolean> {

		public BooleanColumnLabelProvider(String attributeName) {
			super(attributeName, Boolean.class);
		}

		@Override
		protected Image getImage(AvroNode node, AvroAttribute<Boolean> attribute, Boolean value) {
			if (value) {
				return AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.CHECKED);
			} else {
				return AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.UNCHECKED);
			}
		}
		
	}
	
//	public class BooleanCellLabelProvider extends AbstractCellLabelProvider<Boolean> {
//
//		public BooleanCellLabelProvider(String attributeName, IEnableCellUpdater disabledCellUpdater) {
//			super(attributeName, Boolean.class, disabledCellUpdater);
//		}
//
//		@Override
//		protected void update(ViewerCell cell, AvroAttribute<Boolean> attribute, Boolean value) {
//			if (value) {
//				cell.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.CHECKED));
//			} else {
//				cell.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.UNCHECKED));
//			}
//		}
//		
//	}
	
	public class PrimitiveTypesColumnLabelProvider extends EnableDisableColumnLabelProvider<PrimitiveTypes> {

		public PrimitiveTypesColumnLabelProvider(String attributeName, String disabledText) {
			super(attributeName, PrimitiveTypes.class, disabledText);
		}

		@Override
		protected String getEnabledText(AvroNode node, AvroAttribute<PrimitiveTypes> attribute, PrimitiveTypes value) {
			PrimitiveType primitiveType = value.getValue();
			return primitiveType.getName();			
		}		
		
	}
	
//	public class PrimitiveTypesCellLabelProvider extends AbstractCellLabelProvider<PrimitiveTypes> {
//
//		public PrimitiveTypesCellLabelProvider(String attributeName, IEnableCellUpdater disabledCellUpdater) {
//			super(attributeName, PrimitiveTypes.class, disabledCellUpdater);
//		}
//
//		@Override
//		protected void update(ViewerCell cell, AvroAttribute<PrimitiveTypes> attribute, PrimitiveTypes value) {
//			if (attribute.isEnabled()) {
//				PrimitiveType primitiveType = value.getValue();
//				cell.setText(primitiveType.getName());
//			} else {
//				cell.setText("-");
//				cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
//			}
//		}
//		
//	}
	
	public class StringListColumnLabelProvider extends AbstractColumnLabelProvider<StringList> {

		public StringListColumnLabelProvider(String attributeName) {
			super(attributeName, StringList.class);
		}

		@Override
		protected String getText(AvroNode node, AvroAttribute<StringList> attribute, StringList value) {
			List<String> values = value.getValues();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < values.size(); i++) {
				buffer.append(values.get(i));
				if (i < values.size() - 1) {
					buffer.append(", ");
				}
			}
			return buffer.toString();
		}

	}
	
//	public class StringListCellLabelProvider extends AbstractCellLabelProvider<StringList> {
//
//		public StringListCellLabelProvider(String attributeName, IEnableCellUpdater disabledCellUpdater) {
//			super(attributeName, StringList.class, disabledCellUpdater);
//		}
//
//		@Override
//		protected void update(ViewerCell cell, AvroAttribute<StringList> attribute, StringList value) {
//			List<String> values = value.getValues();
//			StringBuffer buffer = new StringBuffer();
//			for (int i = 0; i < values.size(); i++) {
//				buffer.append(values.get(i));
//				if (i < values.size() - 1) {
//					buffer.append(", ");
//				}
//			}
//			cell.setText(buffer.toString());
//		}
//		
//	}
	
	public class IntegerColumnLabelProvider extends AbstractColumnLabelProvider<Integer> {

		public IntegerColumnLabelProvider(String attributeName) {
			super(attributeName, Integer.class);
		}

		@Override
		protected String getText(AvroNode node, AvroAttribute<Integer> attribute, Integer value) {
			return Integer.toString(value);
		}
		
	}
	
//	public class IntegerCellLabelProvider extends AbstractCellLabelProvider<Integer> {
//
//		public IntegerCellLabelProvider(String attributeName, IEnableCellUpdater disabledCellUpdater) {
//			super(attributeName, Integer.class, disabledCellUpdater);
//		}
//
//		@Override
//		protected void update(ViewerCell cell, AvroAttribute<Integer> attribute, Integer value) {
//			cell.setText(Integer.toString(value));
//		}
//		
//	}
	
//	protected interface IEnableCellUpdater {
//		
//		boolean updateDisabledCell(ViewerCell cell, AvroAttribute<?> attribute);
//		
//		boolean updateEnabledCell(ViewerCell cell, AvroAttribute<?> attribute);
//		
//	}
//	
//	protected class EnableCellUpdater implements IEnableCellUpdater {
//
//		private String label;
//		
//		public EnableCellUpdater(String label) {
//			super();
//			this.label = label;
//		}
//
//		@Override
//		public boolean updateDisabledCell(ViewerCell cell, AvroAttribute<?> attribute) {
//			cell.setText(label);
//			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
//			return false;
//		}
//
//		@Override
//		public boolean updateEnabledCell(ViewerCell cell, AvroAttribute<?> attribute) {
//			cell.setText("");
//			cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//			return true;
//		}		
//		
//	}
	
}
