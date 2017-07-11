package org.talend.avro.schema.editor.viewer.attribute.column;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.model.attributes.cmd.IAttributeCommandFactory;
import org.talend.avro.schema.editor.model.attributes.validator.AvroNameSpaceValidator;
import org.talend.avro.schema.editor.model.attributes.validator.AvroNameValidator;
import org.talend.avro.schema.editor.utils.StringUtils;

public class EditingSupportFactory {

	private AvroContext context;
	
	private EditingPolicy editingPolicy;
	
	public EditingSupportFactory(AvroContext context, EditingPolicy editingPolicy) {
		super();
		this.context = context;
		this.editingPolicy = editingPolicy;
	}

	public SchemaViewerColumnEditingSupport getEditingSupport(String attributeName, Class<?> attrValueClass) {
		if (AvroAttributes.NAME_SPACE.equals(attributeName)) {
			AvroNodeCellEditorValidator validator = new AvroNodeCellEditorValidator() {
                @Override
                public String isValid(AvroNode node, Object value) {	                    	
                    String name = (String) value;
                    AvroNameSpaceValidator nameSpaceValidator = new AvroNameSpaceValidator(context, node);
                    return nameSpaceValidator.isValid(name);
                }
            };
            return new StringAttributeEditingSupport(attributeName, validator, Notifications.NOT_REF);
		}
		if (AvroAttributes.NAME.equals(attributeName)) {
			AvroNodeCellEditorValidator validator = new AvroNodeCellEditorValidator() {
                @Override
                public String isValid(AvroNode node, Object value) {	                    	
                    String name = (String) value;
                    if (node.getType().hasNameSpace()) {
                    	AvroNameValidator nameValidator = new AvroNameValidator(context, node);
                    	return nameValidator.isValid(name);
                    }
                    return null;
                }
            };
            return new StringAttributeEditingSupport(attributeName, validator, Notifications.NOT_REF);
		}
		if (AvroAttributes.SIZE.equals(attributeName)) {
            return new IntegerAttributeEditingSupport(attributeName, 1, Integer.MAX_VALUE, Notifications.NOT_REF);
		}
		if (AvroAttributes.OPTIONAL.equals(attributeName)) {
			return new BooleanAttributeEditingSupport(attributeName, Notifications.NOT_REF);
		}
		if (String.class.isAssignableFrom(attrValueClass)) {
			return new StringAttributeEditingSupport(attributeName, Notifications.NOT_REF);
		}
		if (Boolean.class.isAssignableFrom(attrValueClass)) {
			return new BooleanAttributeEditingSupport(attributeName, Notifications.NOT_REF);
		} 
		if (PrimitiveTypes.class.isAssignableFrom(attrValueClass)) {
			return new PrimitiveTypesAttributeEditingSupport(attributeName, Notifications.NOT_REF);
		}
		if (StringList.class.isAssignableFrom(attrValueClass)) {
			return null;
		} 		
		return null;
	}
		
	protected abstract class AbstractAttributeEditingSupport<T> implements SchemaViewerColumnEditingSupport {
		
		protected String attributeName;
		
		protected Class<T> attributeValueClass;
		
		protected int notifications;
		
		protected AbstractAttributeEditingSupport(String attributeName, Class<T> attributeValueClass, int notifications) {
			this.attributeName = attributeName;
			this.attributeValueClass = attributeValueClass;
			this.notifications = notifications;
		}		
				
		@Override
		public boolean canEdit(AvroNode node) {
			return editingPolicy.isEditable(node, attributeName);
		}
		
		@SuppressWarnings("unchecked")
		protected AvroAttribute<T> getAttribute(AvroNode node) {
			return (AvroAttribute<T>) node.getAttributes().getAttribute(attributeName);
		}
		
		protected T getAttributeValue(AvroNode node) {
			return node.getAttributes().getAttributeValue(attributeName, attributeValueClass);
		}		
		
		@Override
		public Object getValue(AvroNode node) {
			T attrValue = getAttributeValue(node);
			return doGetValue(attrValue);
		}

		protected abstract Object doGetValue(T attributeValue);
		
		protected void changeAttribute(AvroNode node, T newAttributeValue) {
			IAttributeCommandFactory attrCmdFactory = context.getService(IAttributeCommandFactory.class);
			IEditCommand command = attrCmdFactory.createChangeAttributeCommand(getAttribute(node), newAttributeValue, notifications);
			context.getService(ICommandExecutor.class).execute(command);
		}

	}
	
	public class BooleanAttributeEditingSupport extends AbstractAttributeEditingSupport<Boolean> {

		public BooleanAttributeEditingSupport(String attributeName, int notifications) {
			super(attributeName, Boolean.class, notifications);
		}

		@Override
		public CellEditor getCellEditor(AvroNode node, ColumnViewer viewer) {
			return new CheckboxCellEditor((Composite) viewer.getControl());
		}

		@Override
		protected Object doGetValue(Boolean attributeValue) {
			return attributeValue;
		}

		@Override
		public void setValue(AvroNode node, Object value) {
			Boolean newValue = (Boolean) value;
			if (getAttributeValue(node) != newValue) {
				changeAttribute(node, (Boolean) value);
			}
		}
		
	}
	
	public class IntegerAttributeEditingSupport extends AbstractAttributeEditingSupport<Integer> {

		private int min;
		
		private int max;
		
		public IntegerAttributeEditingSupport(String attributeName, int notifications) {
			this(attributeName, Integer.MIN_VALUE, Integer.MAX_VALUE, notifications);
		}
		
		public IntegerAttributeEditingSupport(String attributeName, int min, int max, int notifications) {
			super(attributeName, Integer.class, notifications);
			this.min = min;
			this.max = max;
		}

		@Override
		public CellEditor getCellEditor(AvroNode node, ColumnViewer viewer) {
			TextCellEditor textCellEditor = new TextCellEditor((Composite) viewer.getControl());
			AvroNodeCellEditorValidator validator = new AvroNodeCellEditorValidator() {
                @Override
                public String isValid(AvroNode node, Object value) {	                    	
                    String intStr = (String) value;
                    try {
                    	int intVal = Integer.parseInt(intStr);
                    	if (min > Integer.MIN_VALUE && intVal < min) {
                    		return "Value should be greater than " + min;
                    	}
                    	if (max < Integer.MAX_VALUE && intVal > max) {
                    		return "Value should be lower than " + max;
                    	}
                    	return null;
                    } catch (NumberFormatException e) {
                    	return "Value should be a valid integer";
                    }
                }
            };
			textCellEditor.setValidator(validator);
			return textCellEditor;
		}

		@Override
		protected Object doGetValue(Integer attributeValue) {
			return Integer.toString(attributeValue);
		}

		@Override
		public void setValue(AvroNode node, Object value) {
			int newValue = Integer.parseInt((String) value);
			if (getAttributeValue(node) != newValue) {
				changeAttribute(node, newValue);
			}
		}
		
	}
	
	public class StringAttributeEditingSupport extends AbstractAttributeEditingSupport<String> {
		
		private AvroNodeCellEditorValidator validator;
		
		public StringAttributeEditingSupport(String attributeName, int notifications) {
			this(attributeName, null, notifications);
		}
		
		public StringAttributeEditingSupport(String attributeName, AvroNodeCellEditorValidator validator, int notifications) {
			super(attributeName, String.class, notifications);
			this.validator = validator;
		}

		@Override
		public CellEditor getCellEditor(AvroNode node, ColumnViewer viewer) {
			TextCellEditor textCellEditor = new TextCellEditor((Composite) viewer.getControl());
			if (validator != null) {
				validator.setNode(node);
				textCellEditor.setValidator(validator);
			}
			return textCellEditor;
		}

		@Override
		protected Object doGetValue(String attributeValue) {
			return attributeValue;
		}

		@Override
		public void setValue(AvroNode node, Object value) {
			String newValue = (String) value;
			String attrValue = getAttributeValue(node);
			if (!StringUtils.areEqual(attrValue, newValue)) {
				changeAttribute(node, newValue);
			}
		}
		
	}
	
	public class PrimitiveTypesAttributeEditingSupport extends AbstractAttributeEditingSupport<PrimitiveTypes> {
		
		public PrimitiveTypesAttributeEditingSupport(String attributeName, int notifications) {
			super(attributeName, PrimitiveTypes.class, notifications);
		}

		@Override
		public CellEditor getCellEditor(AvroNode node, ColumnViewer viewer) {
			PrimitiveTypes primitiveTypes = getAttributeValue(node);
			String[] valuesAsString = primitiveTypes.getValuesAsString();
			return new ComboBoxCellEditor((Composite) viewer.getControl(), valuesAsString);
		}

		@Override
		protected Object doGetValue(PrimitiveTypes primitiveTypes) {
			PrimitiveType type = primitiveTypes.getValue();
			return primitiveTypes.getIndexOf(type);
		}

		@Override
		public void setValue(AvroNode node, Object value) {
			Integer selectedTypeIndex = (Integer) value;
			PrimitiveTypes primitiveTypes = getAttributeValue(node);
			PrimitiveType type = primitiveTypes.getValue();
			int currentTypeIndex = primitiveTypes.getIndexOf(type);
			if (currentTypeIndex != selectedTypeIndex) {
				PrimitiveType selectedType = primitiveTypes.getValueFor(selectedTypeIndex);
				PrimitiveTypes newValue = primitiveTypes.getACopy();
				newValue.setValue(selectedType);
				changeAttribute(node, newValue);				
			}
		}		
		
	}
	
	public abstract class AvroNodeCellEditorValidator implements ICellEditorValidator {

		private AvroNode node;
		
		public void setNode(AvroNode node) {
			this.node = node;
		}

		@Override
		public String isValid(Object value) {
			return isValid(node, value);
		}
		
		protected abstract String isValid(AvroNode node, Object value);
		
	}
	
}
