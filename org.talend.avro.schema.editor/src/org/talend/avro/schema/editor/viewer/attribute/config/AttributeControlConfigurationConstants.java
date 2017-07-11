package org.talend.avro.schema.editor.viewer.attribute.config;

/**
 * Defines some configuration identifiers/keys and values.
 * 
 * @author timbault
 *
 */
public interface AttributeControlConfigurationConstants {

	/**
	 * Text widget configuration. It specifies the style of a SWT Text control.
	 */
	static final String TEXT_STYLE = "TextStyle"; //$NON-NLS-1$
	
	/**
	 * Specify the kind of representation for a MultiChoiceValue attribute. Such an attribute can have 2 representations: combo or radio buttons.
	 */
	static final String MULTI_CHOICE_VALUE_STYLE = "MultiChoiceValueStyle"; //$NON-NLS-1$
	
	/**
	 * Used with the MULTI_CHOICE_VALUE_STYLE configuration above, it allows to specify a "combo" representation for MultiChoiceValue attribute.
	 */
	static final String COMBO = "Combo"; //$NON-NLS-1$
	
	/**
	 * Used with the MULTI_CHOICE_VALUE_STYLE configuration above, it allows to specify a "radio buttons" representation for MultiChoiceValue attribute.
	 */
	static final String RADIO_BUTTONS = "RadioButtons"; //$NON-NLS-1$
	
	static final String MULTI_CHOICE_VALUE_CONTENT_PROVIDER = "MultiChoiceValueContentProvider"; //$NON-NLS-1$
	
	/**
	 * Most of the AttributeControl need a label provider to get the labels displayed in the controls.
	 */
	static final String LABEL_PROVIDER = "LabelProvider"; // //$NON-NLS-1$
	
	/**
	 * Configuration for AttributeControl using JFace table viewer. The value associated to this configuration key must be a TableConfiguration instance.
	 */
	static final String TABLE_CONFIG = "TableConfig"; //$NON-NLS-1$
	
	/**
	 * Text widget configuration. It specifies an input validator used when editing with text widget.
	 */
	static final String INPUT_VALIDATOR = "InputValidator"; //$NON-NLS-1$
	
	/**
	 * Used by the label provider of a StringListAttributeControl. It allows to specify the label of the actions displayed in the toolbar.
	 */
	static final String ACTION_LABEL = "actionLabel"; //$NON-NLS-1$
	
	/**
	 * This configuration allows to specify the notification which must be used by the generated command when editing attribute.
	 */
	static final String NOTIFICATIONS = "Notifications"; //$NON-NLS-1$
	
	static final String COMMAND_VALIDATOR = "CommandValidator"; //$NON-NLS-1$ 
	
}
