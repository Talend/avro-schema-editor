package org.talend.avro.schema.editor.viewer.attribute.ui;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.cmd.IAttributeCommandFactory;
import org.talend.avro.schema.editor.model.attributes.validator.AttributeCommandValidator;
import org.talend.avro.schema.editor.model.attributes.validator.DefaultInputValidtor;
import org.talend.avro.schema.editor.model.attributes.validator.InputValidatorProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurationConstants;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;

/**
 * Base abstract implementation of an {@link AttributeControl}. 
 * 
 * @author timbault
 *
 * @param <T>
 */
public abstract class BaseAttributeControl<T> implements AttributeControl<T> {

	private AvroAttribute<T> attribute;

	private AvroContext context;
	
	private boolean updating = false;
	
	private boolean modifying = false;
	
	private AttributeControlConfiguration attributeControlConfig;
	
	protected void setAttribute(AvroAttribute<T> attribute) {
		this.attribute = attribute;
	}

	protected AvroAttribute<T> getAttribute() {
		return attribute;
	}
	
	protected AvroContext getContext() {
		return context;
	}

	protected void setContext(AvroContext context) {
		this.context = context;
	}

	protected void executeCommand(IEditCommand cmd) {
		context.getService(ICommandExecutor.class).execute(cmd);
	}
	
	protected void initialize(AvroAttribute<T> element, AvroContext context) {
		if (!(element instanceof AvroAttribute<?>)) {
			throw new IllegalArgumentException("element must be an attribute");
		}
		setAttribute(element);
		setContext(context);
	}
	
	protected void updateEnabledState() {
		setEnabled(getAttribute().isEnabled());
	}
	
	protected void setUpdating(boolean updating) {
		this.updating = updating;
	}

	protected boolean isUpdating() {
		return updating;
	}
	
	protected boolean isModifying() {
		return modifying;
	}

	protected void setModifying(boolean modifying) {
		this.modifying = modifying;
	}

	@Override
	public void update() {
		if (!isModifying()) {
			setUpdating(true);
			doUpdate();
			updateEnabledState();
			setUpdating(false);
		}
	}	
	
	protected IEditCommand getChangeAttributeCommand(T oldValue, T newValue) {
		boolean canExecuteCommand = true;
		if (hasConfiguration(AttributeControlConfigurationConstants.COMMAND_VALIDATOR)) {
			AttributeCommandValidator commandValidator = getConfiguration(AttributeControlConfigurationConstants.COMMAND_VALIDATOR, AttributeCommandValidator.class);
			canExecuteCommand = commandValidator.canExecuteCommand(getAttribute(), oldValue, newValue);
		}
		if (canExecuteCommand) {
			IAttributeCommandFactory attributeCommandFactory = getContext().getService(IAttributeCommandFactory.class);
			// get the notifications from the attribute configuration
			int notifications = Notifications.NOT_REF; // default notification
			if (hasConfiguration(AttributeControlConfigurationConstants.NOTIFICATIONS)) {
				notifications = getConfiguration(AttributeControlConfigurationConstants.NOTIFICATIONS, Integer.class);
				if (Notifications.notifyCurrentContext(notifications)) {
					notifications = Notifications.addCurrentContext(notifications, context);
				}
			}
			return attributeCommandFactory.createChangeAttributeCommand(getAttribute(), newValue, notifications);
		}
		return null;
	}
	
	protected void changeAttribute(T oldValue, T newValue) {
		if (!isUpdating()) {
			setModifying(true);			
			IEditCommand changeAttrCmd = getChangeAttributeCommand(oldValue, newValue);
			if (changeAttrCmd != null) {
				executeCommand(changeAttrCmd);
			}
			setModifying(false);
		}
	}
	
	protected void doLayoutData(Object layoutData, Control... controls) {
//		if (controls.length == 1 && (layoutData)) {
//			throw new IllegalArgumentException("Attribute UI " + getAttribute().getName() + " needs only one layout data object");
//		}
		if (controls.length > 1 && !(layoutData instanceof Object[])) {
			throw new IllegalArgumentException("Attribute control for " + getAttribute().getName() + " needs an array of layout data");
		}
		if (controls.length > 1 && layoutData instanceof Object[] && ((Object[])layoutData).length < controls.length) {
			throw new IllegalArgumentException("Attribute control for " + getAttribute().getName() + " needs an array of layout data of size " + controls.length);
		}
		if (controls.length == 1) {
			controls[0].setLayoutData(layoutData);			
		} else if (controls.length > 1) {
			Object[] layoutDataArray = (Object[]) layoutData;	
			for (int i = 0; i < layoutDataArray.length; i++) {
				controls[i].setLayoutData(layoutDataArray[i]);
			}
		}
	}
	
	protected void updateLabel(Label label) {
		label.setText(getLabel(getAttribute()));
	}
	
	protected abstract void doUpdate();
	
	@Override
	public void setConfiguration(AttributeControlConfiguration configuration) {
		this.attributeControlConfig = configuration;
	}

	protected AttributeControlConfiguration getConfiguration() {
		return attributeControlConfig;
	}
	
	protected boolean hasConfiguration() {
		return attributeControlConfig != null;
	}
	
	protected boolean hasConfiguration(String configId) {
		return attributeControlConfig != null && attributeControlConfig.hasConfiguration(configId);
	}
	
	protected <U> U getConfiguration(String configId, Class<U> configClass) {
		return attributeControlConfig.getConfiguration(configId, configClass);
	}
		
	protected String getLabel(Object element) {
		if (hasConfiguration(AttributeControlConfigurations.LABEL_PROVIDER)) {
			ILabelProvider labelProvider = getConfiguration(AttributeControlConfigurations.LABEL_PROVIDER, ILabelProvider.class);
			if (labelProvider != null) {
				return labelProvider.getText(element);
			}
		}
		return element.toString();
	}
	
	protected int getTextStyle(int initialStyle) {
		int textStyle = initialStyle;
		if (hasConfiguration(AttributeControlConfigurations.TEXT_STYLE)) {
			int textStyleConfig = getConfiguration(AttributeControlConfigurations.TEXT_STYLE, Integer.class);
			textStyle = textStyle | textStyleConfig;
		}
		return textStyle;
	}
	
	protected IInputValidator getInputValidator() {
		IInputValidator validator = null;
		if (hasConfiguration(AttributeControlConfigurations.INPUT_VALIDATOR)) {
			InputValidatorProvider inputValidatorProvider = 
					getConfiguration(AttributeControlConfigurations.INPUT_VALIDATOR, InputValidatorProvider.class);
			validator = inputValidatorProvider.getInputValidator(getAttribute().getHolder(), context);
		}
		if (validator == null) {
			validator = new DefaultInputValidtor();
		}
		return validator;
	}
	
	/**
	 * Work only if control has a GridData layout data.
	 * 
	 * @param control
	 * @param visible
	 */
	protected void setControlVisible(Control control, boolean visible) {
		control.setVisible(visible);
		GridData layoutData = (GridData) control.getLayoutData();
		layoutData.exclude = !visible;
	}
	
}
