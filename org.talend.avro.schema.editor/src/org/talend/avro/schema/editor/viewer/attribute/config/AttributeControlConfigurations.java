package org.talend.avro.schema.editor.viewer.attribute.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapValue;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.model.attributes.validator.AttributeCommandValidator;
import org.talend.avro.schema.editor.model.attributes.validator.AvroNameSpaceValidatorProvider;
import org.talend.avro.schema.editor.model.attributes.validator.AvroNameValidatorProvider;
import org.talend.avro.schema.editor.model.attributes.validator.FixedSizeValidatorProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.BooleanAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.CustomPropertiesAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.IntegerAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.MultiChoiceAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.PathAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.StringAttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.ui.StringListAttributeControl;

/**
 * This class defines all the configurations of the avro attributes used in the standard avro schema editor.
 * 
 * @author timbault
 *
 */
public class AttributeControlConfigurations implements AttributeControlConfigurationConstants {
	
	private Map<Class<?>, Class<? extends AttributeControl<?>>> defaultAttributeControlClasses = new HashMap<>();
	
	private Map<String, Class<? extends AttributeControl<?>>> namedAttributeControlClasses = new HashMap<>();
	
	private Map<Class<?>, Object> defaultLayoutDataMap = new HashMap<>();
	
	private Map<String, Object> namedLayoutDataMap = new HashMap<>();
	
	private Map<Class<?>, AttributeControlConfiguration> defaultAttributeControlConfigurations = new HashMap<>();
	
	private Map<String, AttributeControlConfiguration> namedAttributeControlConfigurations = new HashMap<>();	
	
	private int defaultLabelWidth = 120;
	
	private int defaultMultiStringWidth = 400;
	
	private int defaultListHeight = 100;
	
	public AttributeControlConfigurations() {
		super();
		initializeConfigurations();
	}

	protected final void registerDefaultAttributeControlClass(Class<?> attributeValueClass, Class<? extends AttributeControl<?>> attributeControlClass) {
		defaultAttributeControlClasses.put(attributeValueClass, attributeControlClass);
	}
	
	protected final void registerNamedAttributeControlClass(String attributeName, Class<? extends AttributeControl<?>> attributeControlClass) {
		namedAttributeControlClasses.put(attributeName, attributeControlClass);
	}
	
	protected final void registerDefaultLayoutData(Class<?> attributeValueClass, Object layoutData) {
		defaultLayoutDataMap.put(attributeValueClass, layoutData);
	}
	
	protected final void registerNamedLayoutData(String attributeName, Object layoutData) {
		namedLayoutDataMap.put(attributeName, layoutData);
	}
	
	protected final void registerDefaultAttributeControlConfigurations(Class<?> attributeValueClass, AttributeControlConfiguration config) {
		defaultAttributeControlConfigurations.put(attributeValueClass, config);
	}
	
	protected final void registerNamedAttributeControlConfigurations(String attributeName, AttributeControlConfiguration config) {
		namedAttributeControlConfigurations.put(attributeName, config);
	}
	
	@SuppressWarnings("unchecked")
	private void initializeConfigurations() {
		
		// default attribute UI classes
		registerDefaultAttributeControlClass(String.class, StringAttributeControl.class);
		registerDefaultAttributeControlClass(Integer.class, IntegerAttributeControl.class);
		registerDefaultAttributeControlClass(Boolean.class, BooleanAttributeControl.class);
		registerDefaultAttributeControlClass(MultiChoiceValue.class, (Class<? extends AttributeControl<?>>) MultiChoiceAttributeControl.class);
		registerDefaultAttributeControlClass(PrimitiveTypes.class, (Class<? extends AttributeControl<?>>) MultiChoiceAttributeControl.class);
		registerDefaultAttributeControlClass(ArrayOrMapValue.class, (Class<? extends AttributeControl<?>>) MultiChoiceAttributeControl.class);
		registerDefaultAttributeControlClass(StringList.class, StringListAttributeControl.class);		
		registerDefaultAttributeControlClass(CustomProperties.class, CustomPropertiesAttributeControl.class);
		
		// named attribute UI classes
		registerNamedAttributeControlClass(AvroAttributes.PATH, PathAttributeControl.class);
		
		// default layout data
		registerDefaultLayoutData(String.class, createTwoColumnsLayoutData());
		registerDefaultLayoutData(Integer.class, createTwoColumnsLayoutData());
		registerDefaultLayoutData(Boolean.class, createBooleanLayoutData());
		registerDefaultLayoutData(MultiChoiceValue.class, createTwoColumnsLayoutData());
		registerDefaultLayoutData(PrimitiveTypes.class, createTwoColumnsLayoutData());
		registerDefaultLayoutData(ArrayOrMapValue.class, createTwoColumnsLayoutData());
		registerDefaultLayoutData(StringList.class, createLayoutDataForMultiStringAttribute(
				defaultLabelWidth, defaultMultiStringWidth, defaultListHeight));		
		registerDefaultLayoutData(CustomProperties.class, createLayoutDataForMultiStringAttribute(defaultLabelWidth, defaultMultiStringWidth, 100));	
		
		// named layout data
		registerNamedLayoutData(AvroAttributes.NAME, createTwoColumnsLayoutData(defaultLabelWidth));
		registerNamedLayoutData(AvroAttributes.NAME_SPACE, createTwoColumnsLayoutData(defaultLabelWidth));		
		//namedLayoutDataMap.put(AvroAttributes.ALIASES, createLayoutDataForMultiStringAttribute(defaultLabelWidth, defaultMultiStringWidth, 50));	
		registerNamedLayoutData(AvroAttributes.DOC, createLayoutDataForMultiStringAttribute(defaultLabelWidth, defaultMultiStringWidth, 50));
		registerNamedLayoutData(AvroAttributes.PATH, createLayoutDataForMultiStringAttribute(defaultLabelWidth, defaultMultiStringWidth, 50));
		
		// default attribute UI configurations
		registerDefaultAttributeControlConfigurations(String.class, createAttributeControlConfiguration(LABEL_PROVIDER, getBaseAttributeLabelProvider()));
		registerDefaultAttributeControlConfigurations(StringList.class, createAttributeControlConfiguration(LABEL_PROVIDER, getBaseAttributeLabelProvider()));
		registerDefaultAttributeControlConfigurations(Boolean.class, createAttributeControlConfiguration(LABEL_PROVIDER, getBaseAttributeLabelProvider()));
		registerDefaultAttributeControlConfigurations(MultiChoiceValue.class, createAttributeControlConfiguration(LABEL_PROVIDER, getBaseAttributeLabelProvider()));
		registerDefaultAttributeControlConfigurations(CustomProperties.class, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider()));		
		
		// named attribute UI configurations
		
		registerNamedAttributeControlConfigurations(AvroAttributes.SYMBOLS, createAttributeControlConfiguration(
				LABEL_PROVIDER, getStringListAttributeLabelProvider("Symbol"),
				TABLE_CONFIG, new DefaultTableConfigurationImpl(
						DefaultTableConfigurationImpl.DEFAULT_STYLE | SWT.MULTI, false, false)));
				
		registerNamedAttributeControlConfigurations(AvroAttributes.ALIASES, createAttributeControlConfiguration(
				LABEL_PROVIDER, getStringListAttributeLabelProvider("Alias"),
				TABLE_CONFIG, new DefaultTableConfigurationImpl(
						DefaultTableConfigurationImpl.DEFAULT_STYLE | SWT.MULTI, false, false)));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.SIZE, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				INPUT_VALIDATOR, new FixedSizeValidatorProvider()));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.NAME, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				INPUT_VALIDATOR, new AvroNameValidatorProvider()));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.NAME_SPACE, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				INPUT_VALIDATOR, new AvroNameSpaceValidatorProvider()));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.PRIMITIVE_TYPE, createAttributeControlConfiguration(
				LABEL_PROVIDER, new LabelProvider() {

					@Override
					public String getText(Object element) {
						if (element instanceof AvroAttribute<?>) {
							return ((AvroAttribute<?>) element).getName();
						} else if (element instanceof PrimitiveType) {
							return ((PrimitiveType)element).toString().toLowerCase();
						}
						return super.getText(element);
					} 

				},
				MULTI_CHOICE_VALUE_CONTENT_PROVIDER, new PrimitiveTypesContentProvider()
				));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.DOC, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				TEXT_STYLE, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.PATH, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				TEXT_STYLE, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP));
	
		registerNamedAttributeControlConfigurations(AvroAttributes.OPTIONAL, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider()));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.CHOICE_TYPE, createAttributeControlConfiguration(
				LABEL_PROVIDER, getBaseAttributeLabelProvider(),
				COMMAND_VALIDATOR, new AttributeCommandValidator() {
					
					@Override
					public <T> boolean canExecuteCommand(AvroAttribute<T> attribute, T oldValue, T newValue) {						
						boolean multiChoice = (Boolean) newValue;
						if (!multiChoice) {
							// we have to check if some schema are going to be removed by this attribute change.
							// in this case we open a dialog to ask the user if he wants to continue
							// TODO open a dialog here
						}
						return true;
					}
					
				},				
				NOTIFICATIONS, Notifications.NOT_REF_REV_CURRENT
				));
			
		registerNamedAttributeControlConfigurations(AvroAttributes.ARRAY_OR_MAP, createAttributeControlConfiguration(
				LABEL_PROVIDER, new LabelProvider() {

					@Override
					public String getText(Object element) {
						if (element instanceof AvroAttribute<?>) {
							return ((AvroAttribute<?>) element).getName();
						} else if (element instanceof NodeType) {
							return ((NodeType) element).toString().toLowerCase();
						}
						return super.getText(element);
					} 

				},
				MULTI_CHOICE_VALUE_STYLE, RADIO_BUTTONS,
				MULTI_CHOICE_VALUE_CONTENT_PROVIDER, new ArrayOrMapValueContentProvider(),
				NOTIFICATIONS, Notifications.FULL_CURRENT
				));
		
	}

	public AttributeControlConfiguration createAttributeControlConfiguration(Object... confData) {
		BaseAttributeControlConfiguration configuration = new BaseAttributeControlConfiguration();
		int index = 0;
		while (index < confData.length) {
			String key = (String) confData[index++];
			Object conf = confData[index++];
			configuration.registerConfiguration(key, conf);
		}		
		return configuration;
	}	
	
	protected ILabelProvider getBaseAttributeLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((AvroAttribute<?>)element).getName();
			} 
		};
	}
	
	protected ILabelProvider getStringListAttributeLabelProvider(final String actionLabel) {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (ACTION_LABEL.equals(element)) {
					return actionLabel;
				}
				return ((AvroAttribute<?>)element).getName();
			} 
		};
	}
	
	protected Object createCustomPropertiesLayoutData(int heightHint) {
		GridData labelLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		labelLayoutData.horizontalSpan = 2;
		GridData tableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		tableLayoutData.horizontalSpan = 2;
		tableLayoutData.heightHint = heightHint;
		return new GridData[] { labelLayoutData, tableLayoutData };
	}
	
	protected Object createOneColumnLayoutData(int heightHint) {
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		layoutData.heightHint = heightHint;
		return layoutData;
	}
	
	protected Object createBooleanLayoutData() {
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		return layoutData;
	}
	
	protected Object createTwoColumnsLayoutData() {
		return new Object[] { new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING), 
				new GridData(GridData.FILL_HORIZONTAL) };
	}
	
	protected Object createTwoColumnsLayoutData(int widthHint) {
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = widthHint;
		return new Object[] { gridData, 
				new GridData(GridData.FILL_HORIZONTAL) };
	}
	
	protected Object createLayoutDataForMultiStringAttribute(int widthHint1, int widthHint2, int heightHint2) {
		GridData gridData1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData1.widthHint = widthHint1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData2.heightHint = heightHint2;
		gridData2.widthHint = widthHint2;
		return new Object[] { gridData1, gridData2 };
	}
	
	public AttributeControl<?> getAttributeControl(AvroAttribute<?> attribute) {
		AttributeControl<?> attributeControl = null;
		// first search with the attribute name
		String attributeName = attribute.getName();
		Class<? extends AttributeControl<?>> attributeControlClass = namedAttributeControlClasses.get(attributeName);
		if (attributeControlClass == null) {
			// then search with the attribute value class
			Class<?> valueClass = attribute.getValueClass();
			attributeControlClass = defaultAttributeControlClasses.get(valueClass);
		}
		if (attributeControlClass != null) {
			try {
				attributeControl = attributeControlClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return attributeControl;
	}
	
	public Layout getMainLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		return layout;
	}
	
	public Object getLayoutData(AvroAttribute<?> attribute) {
		Object layoutData = namedLayoutDataMap.get(attribute.getName());
		if (layoutData == null) {
			Class<?> valueClass = attribute.getValueClass();
			layoutData = defaultLayoutDataMap.get(valueClass);
		}
		return layoutData;
	}
	
	public AttributeControlConfiguration getAttributeControlConfiguration(AvroAttribute<?> attribute) {
		AttributeControlConfiguration configuration = namedAttributeControlConfigurations.get(attribute.getName());
		if (configuration == null) {
			Class<?> valueClass = attribute.getValueClass();
			configuration = defaultAttributeControlConfigurations.get(valueClass);
		}
		return configuration;
	}
	
	
}
